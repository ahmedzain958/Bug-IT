package com.mobily.bug_it.feature_bug.data.remote

import com.mobily.bug_it.feature_bug.data.model.BugReportPayload
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.concurrent.TimeUnit
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class BugUploadClient(
    private val endpointUrl: String,
    private val api: BugUploadApi = createApi()
) {
    suspend fun upload(payload: BugReportPayload): Result<Unit> {
        if (endpointUrl.isBlank()) {
            return Result.failure(BugUploadException.MissingEndpoint)
        }

        return runCatching {
            val response = api.uploadBug(endpointUrl = endpointUrl, payload = payload)
            if (!response.isSuccessful) {
                val errorText = response.errorBody()?.string()?.take(500)
                throw BugUploadException.Http(code = response.code(), body = errorText)
            }
            val body = response.body()
            if (body?.ok != true) {
                throw BugUploadException.Api(body?.message ?: "Upload failed")
            }
        }.recoverCatching { throw it.toUploadException() }
    }

    suspend fun getBugs(): Result<List<BugReportPayload>> {
        if (endpointUrl.isBlank()) return Result.failure(BugUploadException.MissingEndpoint)
        return runCatching {
            val response = api.getBugs(endpointUrl)
            if (!response.isSuccessful) throw BugUploadException.Http(response.code(), null)
            val body = response.body()
            if (body?.ok != true) throw BugUploadException.Api("Failed to fetch bugs")
            body.bugs ?: emptyList()
        }.recoverCatching { throw it.toUploadException() }
    }

    companion object {
        private fun createApi(): BugUploadApi {
            val logger = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
            val client = OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .followRedirects(true)
                .followSslRedirects(true)
                .addInterceptor(logger)
                .build()

            return Retrofit.Builder()
                .baseUrl("https://script.google.com/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(BugUploadApi::class.java)
        }

        private fun Throwable.toUploadException(): BugUploadException {
            return when (this) {
                is BugUploadException -> this
                is SocketTimeoutException -> BugUploadException.Timeout
                is UnknownHostException -> BugUploadException.NetworkUnavailable
                is IOException -> BugUploadException.NetworkUnavailable
                else -> BugUploadException.Unknown(message ?: "Unknown", this)
            }
        }
    }
}