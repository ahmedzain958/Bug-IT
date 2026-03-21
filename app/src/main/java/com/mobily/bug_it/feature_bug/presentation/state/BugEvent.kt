package com.mobily.bug_it.feature_bug.presentation.state

sealed class BugEvent {
    object BugSubmitted : BugEvent()
    data class ShowError(val message: String) : BugEvent()
}