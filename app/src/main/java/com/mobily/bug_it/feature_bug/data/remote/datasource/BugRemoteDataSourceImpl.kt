package com.mobily.bug_it.feature_bug.data.remote.datasource

import android.content.ContentResolver
import android.net.Uri
import com.mobily.bug_it.BuildConfig
import com.mobily.bug_it.feature_bug.data.model.BugReportPayload
import com.mobily.bug_it.feature_bug.data.remote.BugUploadApi
import com.mobily.bug_it.feature_bug.data.remote.ImageUploadApi
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

/**
 * Implementation of BugRemoteDataSource using Retrofit APIs.
 * This class handles the low-level network details and data transformations.
 */
class BugRemoteDataSourceImpl @Inject constructor(
    private val bugApi: BugUploadApi,
    private val imageApi: ImageUploadApi,
    private val contentResolver: ContentResolver
) : BugRemoteDataSource {

    /**
     * Uploads the bug report to Google Apps Script.
     * runCatching encapsulates the exception into a Result.
     */
    override suspend fun uploadBug(payload: BugReportPayload): Result<Unit> = runCatching {
        val endpoint = BuildConfig.BUG_UPLOAD_ENDPOINT
        val response = bugApi.uploadBug(endpoint, payload)
        if (!response.isSuccessful || response.body()?.ok != true) {
            throw Exception(response.body()?.message ?: "Failed to upload to Google Sheets")
        }
    }

    /**
     * Fetches the list of bug reports from Google Apps Script.
     */
    override suspend fun getBugs(): Result<List<BugReportPayload>> = runCatching {
        val endpoint = BuildConfig.BUG_UPLOAD_ENDPOINT
        val response = bugApi.getBugs(endpoint)
        if (!response.isSuccessful || response.body()?.ok != true) {
            throw Exception("Failed to fetch bugs from Google Sheets")
        }
        response.body()?.bugs ?: emptyList()
    }

    /**
     * Uploads image to ImgBB and returns the URL.
     */
    override suspend fun uploadImage(uri: Uri): Result<String> = runCatching {
        val apiKey = BuildConfig.IMGBB_API_KEY
        val inputStream = contentResolver.openInputStream(uri) 
            ?: throw Exception("Could not open image stream")
        
        val bytes = inputStream.use { it.readBytes() }
        val requestBody = bytes.toRequestBody("image/jpeg".toMediaType())
        val part = MultipartBody.Part.createFormData(
            "image", 
            "bug_${System.currentTimeMillis()}.jpg", 
            requestBody
        )

        val response = imageApi.uploadImage(apiKey, part)
        if (!response.success || response.data?.url == null) {
            throw Exception("ImgBB upload failed")
        }
        response.data.url
    }
}
