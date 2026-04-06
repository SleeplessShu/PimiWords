package com.sleeplessdog.pimi.dictionary.dictionary_screen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.sleeplessdog.pimi.dictionary.GroupDictionaryUiMapper
import com.sleeplessdog.pimi.dictionary.authorisation.AuthState
import com.sleeplessdog.pimi.dictionary.authorisation.DataTransferStatus
import com.sleeplessdog.pimi.dictionary.authorisation.DictionarySyncState
import com.sleeplessdog.pimi.games.domain.models.CombinedGroupsDictionaryUi
import com.sleeplessdog.pimi.games.domain.usecases.CreateUserGroupUC
import com.sleeplessdog.pimi.games.domain.usecases.DeleteUserGroupUC
import com.sleeplessdog.pimi.games.domain.usecases.ObserveAllGroupsForDictionaryUC
import com.sleeplessdog.pimi.games.domain.usecases.RenameUserGroupUC
import com.sleeplessdog.pimi.payments.PremiumGate
import com.sleeplessdog.pimi.utils.ConstantsPaths.TAG_AUTH
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed class DictionaryUiEvent {
    object RequestGoogleSignIn : DictionaryUiEvent()
    object ResetGoogleSignIn : DictionaryUiEvent()
    object ShowPremiumDialog : DictionaryUiEvent()
}

class DictionaryViewModel(
    private val observeAllGroups: ObserveAllGroupsForDictionaryUC,
    private val createUserGroup: CreateUserGroupUC,
    private val renameUserGroup: RenameUserGroupUC,
    private val deleteUserGroup: DeleteUserGroupUC,
    private val groupDictionaryUiMapper: GroupDictionaryUiMapper,
    private val syncController: DatabaseSyncController,
    private val authController: FirebaseAuthController,
    private val premiumGate: PremiumGate,

    ) : ViewModel() {

    private var authDeclinedThisSession = false

    private val _syncState = MutableStateFlow(
        DictionarySyncState(
            auth = AuthState.UNKNOWN,
            globalDb = DataTransferStatus.UNKNOWN,
            userDb = DataTransferStatus.UNKNOWN
        )
    )
    val syncState: StateFlow<DictionarySyncState> = _syncState

    private val _pendingEvent = MutableStateFlow<DictionaryUiEvent?>(null)
    val pendingEvent: StateFlow<DictionaryUiEvent?> = _pendingEvent

    private val groupRefreshTrigger = MutableStateFlow(0)

    val groupState: StateFlow<CombinedGroupsDictionaryUi> =
        groupRefreshTrigger
            .flatMapLatest {
                observeAllGroups()
                    .map { domain ->
                        CombinedGroupsDictionaryUi(
                            userGroups = domain.userGroups.map(groupDictionaryUiMapper::map),
                            globalGroups = domain.globalGroups.map(groupDictionaryUiMapper::map)
                        )
                    }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = CombinedGroupsDictionaryUi()
            )

    init {
        checkAuthorization()
        observeSyncState()
        observeDeployCompleted()
        ensureSavedWordsGroup()
    }

    private fun observeDeployCompleted() {
        viewModelScope.launch {
            syncController.deployCompleted.collect { instance ->
                groupRefreshTrigger.update { it + 1 }
            }
        }
    }

    private fun checkAuthorization() {
        viewModelScope.launch {

            if (authDeclinedThisSession) return@launch

            _syncState.update { it.copy(auth = AuthState.UNKNOWN) }

            val authorized = authController.isUserAuthorized()
            Log.d(TAG_AUTH, "checkAuthorization: $authorized ")

            if (authorized) {
                val userUID = syncController.getUid()
                Log.d(TAG_AUTH, "userUID: $userUID")
                FirebaseCrashlytics.getInstance().setUserId(userUID)

                _syncState.update {
                    it.copy(auth = AuthState.AUTHORIZED)
                }

                startSync()

            } else {

                _syncState.update {
                    it.copy(auth = AuthState.NOT_AUTHORIZED)
                }
                _pendingEvent.value = DictionaryUiEvent.RequestGoogleSignIn

                _syncState.update {
                    it.copy(auth = AuthState.AUTHORIZING)
                }
            }
        }
    }

    fun onAuthDeclined() {
        authDeclinedThisSession = true
        _syncState.update { it.copy(auth = AuthState.NOT_AUTHORIZED) }
        _pendingEvent.value = null
    }

    fun clearPendingEvent() {
        _pendingEvent.value = null
    }

    fun onGoogleIdTokenReceived(token: String) {

        viewModelScope.launch {


            authController.signInWithGoogle(token)
                .onSuccess {

                    _syncState.update {
                        it.copy(auth = AuthState.AUTHORIZED)
                    }

                    startSync()
                }
                .onFailure {

                    _syncState.update {
                        it.copy(auth = AuthState.ERROR)
                    }
                }
        }
    }

    private fun observeSyncState() {
        viewModelScope.launch {
            syncController.syncState.collect { dbState ->

                _syncState.update { current ->

                    current.copy(
                        globalDb = dbState.globalDb,
                        userDb = dbState.userDb
                    )
                }
            }
        }
    }

    fun tryCreateUserGroup(groupName: String) {
        viewModelScope.launch {
            val userGroupsSize = groupState.value.userGroups.size
            val isPremium = premiumGate.check()

            when {
                userGroupsSize <= 3 -> newUserGroup(groupName)
                isPremium -> newUserGroup(groupName)
                else -> showPremiumDialog()
            }
        }
    }

    private fun showPremiumDialog() {
        _pendingEvent.value = DictionaryUiEvent.ShowPremiumDialog
    }

    private suspend fun newUserGroup(groupName: String) {
        createUserGroup(
            groupName = groupName,
        )
    }


    fun renameGroup(groupKey: String, newName: String) {
        viewModelScope.launch {
            renameUserGroup(groupKey, newName)
        }
    }

    fun deleteGroup(groupKey: String) {
        viewModelScope.launch {
            deleteUserGroup(groupKey)
        }
    }

    private fun startSync() {
        viewModelScope.launch {

            syncController.checkGlobalDatabase()

            if (_syncState.value.auth == AuthState.AUTHORIZED) {
                syncController.checkUserDatabase()
            }
        }
    }


    private val _isGroupsRefreshing = MutableStateFlow(false)
    val isGroupsRefreshing: StateFlow<Boolean> = _isGroupsRefreshing

    fun refreshGroups() {
        viewModelScope.launch {
            _isGroupsRefreshing.value = true
            groupRefreshTrigger.update { it + 1 }
            observeAllGroups().first()
            _isGroupsRefreshing.value = false
        }
    }

    fun refreshSync() {
        viewModelScope.launch {
            authDeclinedThisSession = false
            _pendingEvent.value = DictionaryUiEvent.ResetGoogleSignIn
            _syncState.update {
                DictionarySyncState(
                    auth = AuthState.UNKNOWN,
                    globalDb = DataTransferStatus.UNKNOWN,
                    userDb = DataTransferStatus.UNKNOWN
                )
            }
            checkAuthorization()
        }
    }

    private fun ensureSavedWordsGroup() {
        viewModelScope.launch {
            syncController.ensureSavedWordsGroup()
        }
    }

    fun onAuthFailed() {
        _syncState.update {
            it.copy(auth = AuthState.ERROR)
        }
    }
}