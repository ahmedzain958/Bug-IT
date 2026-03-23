package com.mobily.bug_it.feature_bug.presentation.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobily.bug_it.feature_bug.domain.usecase.UploadBugUseCase
import com.mobily.bug_it.feature_bug.presentation.state.BugEvent
import com.mobily.bug_it.feature_bug.presentation.state.BugUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for reporting a bug.
 * Uses MVVM pattern and communicates with the UI via StateFlow and SharedFlow.
 */
@HiltViewModel
class BugViewModel @Inject constructor(
    private val uploadBugUseCase: UploadBugUseCase
) : ViewModel() {
    
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

    /**
     * Submits the bug report.
     * Uses viewModelScope for automatic cancellation when ViewModel is cleared,
     * preventing memory leaks and orphaned background tasks.
     */
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

            val result = uploadBugUseCase(
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
