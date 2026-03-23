package com.mobily.bug_it.feature_bug.data.repository

import android.net.Uri
import com.mobily.bug_it.feature_bug.data.model.BugReportPayload
import com.mobily.bug_it.feature_bug.data.remote.datasource.BugRemoteDataSource
import com.mobily.bug_it.feature_bug.domain.model.BugReport
import com.mobily.bug_it.feature_bug.domain.repository.BugRepository
import java.time.Instant
import javax.inject.Inject

/**
 * Repository Implementation following SOLID principles.
 * Acts as a single source of truth for the domain layer.
 */
class BugRepositoryImpl @Inject constructor(
    private val remoteDataSource: BugRemoteDataSource
) : BugRepository {

    override suspend fun uploadBug(description: String, imageUris: List<Uri>): Result<Unit> {
        val imageUrls = mutableListOf<String>()
        
        // Parallelization could be added here for multiple images, 
        // but sequential upload is safer for rate limiting.
        for (uri in imageUris) {
            val result = remoteDataSource.uploadImage(uri)
            if (result.isSuccess) {
                result.getOrNull()?.let { imageUrls.add(it) }
            } else {
                return Result.failure(result.exceptionOrNull() ?: Exception("Image upload failed"))
            }
        }

        val payload = BugReportPayload(
            description = description.trim(),
            imageUris = imageUrls,
            reportedAtIso = Instant.now().toString()
        )

        return remoteDataSource.uploadBug(payload)
    }

    override suspend fun getBugs(): Result<List<BugReport>> {
        return remoteDataSource.getBugs().map { list ->
            list.map { it.toDomain() }
        }
    }

    /**
     * Mapper extension function to keep data models out of the domain layer.
     */
    private fun BugReportPayload.toDomain(): BugReport {
        return BugReport(
            description = this.description,
            imageUrls = this.imageUris,
            reportedAtIso = this.reportedAtIso
        )
    }
}
