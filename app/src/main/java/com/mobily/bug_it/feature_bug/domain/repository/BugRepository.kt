package com.mobily.bug_it.feature_bug.domain.repository

import android.net.Uri
import com.mobily.bug_it.feature_bug.domain.model.BugReport

/**
 * Repository interface for Bug reporting.
 * Interface Segregation allows for easy mocking in tests.
 */
interface BugRepository {
    /**
     * Uploads a bug report including description and local image URIs.
     * @return Result indicating success or failure.
     */
    suspend fun uploadBug(description: String, imageUris: List<Uri>): Result<Unit>

    /**
     * Fetches the list of all bug reports.
     * @return Result containing list of BugReport or error.
     */
    suspend fun getBugs(): Result<List<BugReport>>
}
