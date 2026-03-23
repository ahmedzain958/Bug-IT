package com.mobily.bug_it.feature_bug.data.remote.datasource

import android.net.Uri
import com.mobily.bug_it.feature_bug.data.model.BugReportPayload

/**
 * Interface for remote bug data operations.
 */
interface BugRemoteDataSource {
    suspend fun uploadBug(payload: BugReportPayload): Result<Unit>
    suspend fun getBugs(): Result<List<BugReportPayload>>
    suspend fun uploadImage(uri: Uri): Result<String>
}
