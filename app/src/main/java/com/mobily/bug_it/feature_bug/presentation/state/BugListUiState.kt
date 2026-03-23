package com.mobily.bug_it.feature_bug.presentation.state

import com.mobily.bug_it.feature_bug.domain.model.BugReport

data class BugListUiState(
    val bugs: List<BugReport> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val error: String? = null
)
