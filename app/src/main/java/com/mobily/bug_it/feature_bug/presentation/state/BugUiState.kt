package com.mobily.bug_it.feature_bug.presentation.state

import android.net.Uri

data class BugUiState(
    val description: String = "",
    val imageUri: Uri? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)