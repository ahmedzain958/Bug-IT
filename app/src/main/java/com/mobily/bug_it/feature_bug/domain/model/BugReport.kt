package com.mobily.bug_it.feature_bug.domain.model

/**
 * Domain model representing a bug report.
 * This is decoupled from data layer (API/DB) models to follow Clean Architecture.
 */
data class BugReport(
    val description: String,
    val imageUrls: List<String>,
    val reportedAtIso: String
)
