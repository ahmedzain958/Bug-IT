package com.mobily.bug_it.feature_bug.data.model

data class BugReportPayload(
    val description: String,
    val imageUris: List<String>,
    val reportedAtIso: String,
    val deviceModel: String
)
