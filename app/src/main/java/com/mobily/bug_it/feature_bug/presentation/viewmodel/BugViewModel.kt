package com.mobily.bug_it.feature_bug.presentation.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobily.bug_it.feature_bug.presentation.state.BugEvent
import com.mobily.bug_it.feature_bug.presentation.state.BugUiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BugViewModel : ViewModel() {
    private val _state = MutableStateFlow(BugUiState())
    val state = _state.asStateFlow()
    private val _events = MutableSharedFlow<BugEvent>()
    val events = _events.asSharedFlow()

    fun onDescriptionChange(text: String) {
        _state.update { it.copy(description = text) }
    }

    fun addImages(uris: List<Uri>) {
        _state.update { it.copy(imageUris = it.imageUris + uris) }
    }

    fun removeImage(uri: Uri) {
        _state.update { it.copy(imageUris = it.imageUris - uri) }
    }

    fun submit() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            delay(1500)
            if (_state.value.description.isBlank()) {
                _events.emit(BugEvent.ShowError("Description is required"))
            } else {
                _events.emit(BugEvent.BugSubmitted)
            }
            _state.update { it.copy(isLoading = false) }
        }
    }
}