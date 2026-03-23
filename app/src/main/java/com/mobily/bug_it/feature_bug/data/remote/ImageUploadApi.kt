package com.mobily.bug_it.feature_bug.data.remote

import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query
import okhttp3.MultipartBody

interface ImageUploadApi {
    @Multipart
    @POST("upload")
    suspend fun uploadImage(
        @Query("key") apiKey: String,
        @Part image: MultipartBody.Part
    ): ImageUploadResponse
}

data class ImageUploadResponse(
    val success: Boolean,
    val data: ImageData?
)

data class ImageData(
    val id: String,
    val url: String,
    val display_url: String?,
    val delete_url: String?
)
