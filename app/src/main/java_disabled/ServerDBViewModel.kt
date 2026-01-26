package com.sleeplessdog.matchthewords.settings.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sleeplessdog.matchthewords.server.domain.ServerDateInteractor
import com.sleeplessdog.matchthewords.server.domain.ServerDbInteractor
import kotlinx.coroutines.launch
import java.io.File

class DatabaseViewModel(
    private val interactor: ServerDbInteractor, private val dateManager: ServerDateInteractor
) : ViewModel() {

    private val _state = MutableLiveData<DbUpdateState>()
    val state: LiveData<DbUpdateState> = _state

    fun getCurrentDatabaseDate(): String{
        return dateManager.getLocalDbDate()
    }

    fun checkForDatabaseUpdate() {
        viewModelScope.launch {
            _state.postValue(DbUpdateState.Loading)

            val localDate = dateManager.getLocalDbDate()

            val result = interactor.checkForUpdate(localDate)
                if (result.isSuccess) {
                val serverDate = result.getOrNull()
                if (serverDate != null) {
                    _state.postValue(DbUpdateState.UpdateAvailable(serverDate))
                } else {
                    _state.postValue(DbUpdateState.UpToDate)
                }
            } else {
                _state.postValue(
                    DbUpdateState.Error(
                        result.exceptionOrNull()?.message ?: "Unknown error"
                    )
                )
            }
        }
    }


    fun downloadDatabase(targetFile: File, serverDate: String) {
        viewModelScope.launch {
            _state.postValue(DbUpdateState.Loading)

            val result = interactor.downloadDatabase(targetFile)
            if (result.isSuccess) {
                dateManager.saveLocalDbDate(serverDate)
                _state.postValue(DbUpdateState.Success)
            } else {
                _state.postValue(
                    DbUpdateState.Error(
                        result.exceptionOrNull()?.message ?: "Download error"
                    )
                )
            }
        }
    }
}


sealed class DbUpdateState {
    object Loading : DbUpdateState()
    object UpToDate : DbUpdateState()
    data class UpdateAvailable(val serverDate: String) : DbUpdateState()
    object Success : DbUpdateState()
    data class Error(val message: String) : DbUpdateState()
}
