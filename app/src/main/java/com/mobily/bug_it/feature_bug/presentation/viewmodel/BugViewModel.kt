package com.mobily.bug_it.feature_bug.presentation.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mobily.bug_it.feature_bug.data.BugRepository
import com.mobily.bug_it.feature_bug.presentation.state.BugEvent
import com.mobily.bug_it.feature_bug.presentation.state.BugUiState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BugViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository = BugRepository(application)
    
    private val _state = MutableStateFlow(BugUiState())
    val state = _state.asStateFlow()
    
    private val _events = MutableSharedFlow<BugEvent>()
    val events = _events.asSharedFlow()

    fun onDescriptionChange(text: String) {
        _state.update { 
            it.copy(
                description = text, 
                error = null,
                isDescriptionError = false
            ) 
        }
    }

    fun addImages(uris: List<Uri>) {
        _state.update { it.copy(imageUris = it.imageUris + uris, error = null) }
    }

    fun removeImage(uri: Uri) {
        _state.update { it.copy(imageUris = it.imageUris - uri) }
    }

    fun submit() {
        val currentState = _state.value
        if (currentState.isLoading) return

        if (currentState.description.isBlank()) {
            _state.update { 
                it.copy(
                    isDescriptionError = true, 
                    error = "Description is required" 
                ) 
            }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null, isDescriptionError = false) }

            val result = repository.uploadBug(
                description = currentState.description,
                imageUris = currentState.imageUris
            )

            result.onSuccess {
                _events.emit(BugEvent.BugSubmitted)
                _state.update {
                    it.copy(
                        description = "",
                        imageUris = emptyList(),
                        isLoading = false,
                        error = null,
                        isDescriptionError = false
                    )
                }
            }.onFailure { throwable ->
                val message = throwable.message ?: "Failed to upload bug"
                _events.emit(BugEvent.ShowError(message))
                _state.update { it.copy(isLoading = false, error = message) }
            }
        }
    }
}
