package com.mobily.bug_it.feature_bug.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobily.bug_it.feature_bug.data.BugRepository
import com.mobily.bug_it.feature_bug.presentation.state.BugListUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BugListViewModel(
    private val repository: BugRepository = BugRepository()
) : ViewModel() {
    private val _state = MutableStateFlow(BugListUiState())
    val state = _state.asStateFlow()

    init {
        loadBugs()
    }

    fun loadBugs() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            repository.getBugs()
                .onSuccess { bugs ->
                    _state.update { it.copy(bugs = bugs, isLoading = false) }
                }
                .onFailure { throwable ->
                    _state.update { it.copy(error = throwable.message, isLoading = false) }
                }
        }
    }
}