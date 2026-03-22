package com.mobily.bug_it.feature_bug.presentation.state

import com.mobily.bug_it.feature_bug.data.model.BugReportPayload

data class BugListUiState(
    val bugs: List<BugReportPayload> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)