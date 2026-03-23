package com.mobily.bug_it.feature_bug.data.remote

import android.content.ContentResolver
import android.net.Uri
import java.util.concurrent.TimeUnit
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ImageUploadClient(
    private val contentResolver: ContentResolver,
    private val apiKey: String = resolveBuildConfigString("IMGBB_API_KEY"),
    private val api: ImageUploadApi = createApi()
) {
    suspend fun uploadImage(imageUri: Uri): Result<String> {
        return runCatching {
            val inputStream = contentResolver.openInputStream(imageUri)
                ?: throw IllegalStateException("Cannot open image stream")

            val bytes = inputStream.use { it.readBytes() }
            if (bytes.isEmpty()) throw IllegalStateException("Image is empty")

            val requestBody = bytes.toRequestBody("image/jpeg".toMediaType())
            val part = MultipartBody.Part.createFormData(
                "image",
                "bug_report_${System.currentTimeMillis()}.jpg",
                requestBody
            )

            val response = api.uploadImage(apiKey, part)
            if (!response.success || response.data?.url == null) {
                throw IllegalStateException("Image upload failed")
            }

            response.data.url
        }.recoverCatching { throw it.toUploadException() }
    }

    companion object {
        private fun resolveBuildConfigString(fieldName: String): String {
            return try {
                val buildConfigClass = Class.forName("com.mobily.bug_it.BuildConfig")
                buildConfigClass.getDeclaredField(fieldName).get(null) as String
            } catch (e: Exception) { "" }
        }

        private fun createApi(): ImageUploadApi {
            val logger = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
            val client = OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(logger)
                .build()

            return Retrofit.Builder()
                .baseUrl("https://api.imgbb.com/1/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ImageUploadApi::class.java)
        }

        private fun Throwable.toUploadException(): BugUploadException {
            return when (this) {
                is BugUploadException -> this
                is java.net.SocketTimeoutException -> BugUploadException.Timeout
                is java.net.UnknownHostException -> BugUploadException.NetworkUnavailable
                is java.io.IOException -> BugUploadException.NetworkUnavailable
                else -> BugUploadException.Unknown(message ?: "Unknown", this)
            }
        }
    }
}
