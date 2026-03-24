package com.sleeplessdog.matchthewords.dictionary.dictionary_screen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.sleeplessdog.matchthewords.backend.domain.models.CombinedGroupsDictionaryUi
import com.sleeplessdog.matchthewords.backend.domain.usecases.CreateUserGroupUC
import com.sleeplessdog.matchthewords.backend.domain.usecases.DeleteUserGroupUC
import com.sleeplessdog.matchthewords.backend.domain.usecases.ObserveAllGroupsForDictionaryUC
import com.sleeplessdog.matchthewords.backend.domain.usecases.RenameUserGroupUC
import com.sleeplessdog.matchthewords.dictionary.GroupDictionaryUiMapper
import com.sleeplessdog.matchthewords.dictionary.authorisation.AuthState
import com.sleeplessdog.matchthewords.dictionary.authorisation.DataTransferStatus
import com.sleeplessdog.matchthewords.dictionary.authorisation.DictionarySyncState
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
}

class DictionaryViewModel(
    private val observeAllGroups: ObserveAllGroupsForDictionaryUC,
    private val createUserGroup: CreateUserGroupUC,
    private val renameUserGroup: RenameUserGroupUC,
    private val deleteUserGroup: DeleteUserGroupUC,
    private val groupDictionaryUiMapper: GroupDictionaryUiMapper,
    private val syncController: DatabaseSyncController,
    private val authController: FirebaseAuthController,

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
    }

    private fun observeDeployCompleted() {
        viewModelScope.launch {
            syncController.deployCompleted.collect { instance ->
                Log.d("DICT_VM", "Deploy completed: $instance — refreshing groups")
                groupRefreshTrigger.update { it + 1 }
            }
        }
    }

    private fun checkAuthorization() {
        viewModelScope.launch {

            if (authDeclinedThisSession) return@launch

            _syncState.update { it.copy(auth = AuthState.UNKNOWN) }

            val authorized = authController.isUserAuthorized()

            if (authorized) {
                val userUID = syncController.getUid()
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

    fun addNewUserGroup(name: String) {
        viewModelScope.launch {
            createUserGroup(
                groupName = name,
            )
        }
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

    fun onAuthFailed() {
        _syncState.update {
            it.copy(auth = AuthState.ERROR)
        }
    }
}