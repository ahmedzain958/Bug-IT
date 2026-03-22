package com.mobily.bug_it.feature_bug.data

import android.content.Context
import android.net.Uri
import android.os.Build
import com.mobily.bug_it.feature_bug.data.model.BugReportPayload
import com.mobily.bug_it.feature_bug.data.remote.BugUploadClient
import com.mobily.bug_it.feature_bug.data.remote.ImageUploadClient
import java.time.Instant

class BugRepository(
    private val context: Context? = null,
    private val uploadClient: BugUploadClient = BugUploadClient(resolveBuildConfigString("BUG_UPLOAD_ENDPOINT")),
    private val imageUploadClient: ImageUploadClient? = context?.contentResolver?.let { ImageUploadClient(it) }
) {
    suspend fun uploadBug(description: String, imageUris: List<Uri>): Result<Unit> {
        // Upload images first and collect URLs
        val imageUrls = mutableListOf<String>()
        for (uri in imageUris) {
            val result = imageUploadClient?.uploadImage(uri)
            if (result?.isSuccess == true) {
                imageUrls.add(result.getOrNull() ?: continue)
            } else {
                // Log or handle individual image failure, but continue
                val error = result?.exceptionOrNull()?.message ?: "Unknown error"
                android.util.Log.e("BugRepository", "Failed to upload image: $error")
            }
        }

        val payload = BugReportPayload(
            description = description.trim(),
            imageUris = imageUrls,
            reportedAtIso = Instant.now().toString(),
            deviceModel = "${Build.MANUFACTURER} ${Build.MODEL}",
            appVersion = resolveBuildConfigString("VERSION_NAME", fallback = "1.0")
        )

        return uploadClient.upload(payload)
    }

    companion object {
        private fun resolveBuildConfigString(fieldName: String, fallback: String = ""): String {
            return runCatching {
                val buildConfigClass = Class.forName("com.mobily.bug_it.BuildConfig")
                buildConfigClass.getDeclaredField(fieldName).get(null) as String
            }.getOrDefault(fallback)
        }
    }
}
