package com.mobily.bug_it.feature_bug.domain.usecase

import android.net.Uri
import com.mobily.bug_it.feature_bug.domain.repository.BugRepository
import javax.inject.Inject

/**
 * Use case to handle the logic of uploading a new bug report.
 * Encapsulates the requirement that a description is mandatory.
 */
class UploadBugUseCase @Inject constructor(
    private val repository: BugRepository
) {
    suspend operator fun invoke(description: String, imageUris: List<Uri>): Result<Unit> {
        if (description.isBlank()) {
            return Result.failure(IllegalArgumentException("Description cannot be empty"))
        }
        return repository.uploadBug(description, imageUris)
    }
}
