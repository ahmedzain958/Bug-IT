package com.mobily.bug_it.feature_bug.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobily.bug_it.feature_bug.domain.usecase.GetBugsUseCase
import com.mobily.bug_it.feature_bug.presentation.state.BugListUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for viewing the list of bug reports.
 * Implements MVVM pattern.
 */
@HiltViewModel
class BugListViewModel @Inject constructor(
    private val getBugsUseCase: GetBugsUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(BugListUiState())
    val state = _state.asStateFlow()

    init {
        loadBugs(isInitial = true)
    }

    /**
     * Loads the list of bugs from the use case.
     * Use of viewModelScope ensures automatic cancellation of coroutines when 
     * the ViewModel is cleared, preventing memory leaks.
     */
    fun loadBugs(isInitial: Boolean = false) {
        viewModelScope.launch {
            if (isInitial) {
                _state.update { it.copy(isLoading = true, error = null) }
            } else {
                _state.update { it.copy(isRefreshing = true, error = null) }
            }
            
            getBugsUseCase()
                .onSuccess { bugs ->
                    _state.update { it.copy(bugs = bugs, isLoading = false, isRefreshing = false) }
                }
                .onFailure { throwable ->
                    _state.update { it.copy(error = throwable.message, isLoading = false, isRefreshing = false) }
                }
        }
    }
}
