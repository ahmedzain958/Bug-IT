package com.mobily.bug_it.feature_bug.data.remote

import com.mobily.bug_it.feature_bug.data.model.BugReportPayload
import com.mobily.bug_it.feature_bug.data.model.BugUploadResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Url

interface BugUploadApi {
    @POST
    suspend fun uploadBug(
        @Url endpointUrl: String,
        @Body payload: BugReportPayload
    ): Response<BugUploadResponse>

    @GET
    suspend fun getBugs(
        @Url endpointUrl: String
    ): Response<BugListResponse>
}

data class BugListResponse(
    val ok: Boolean,
    val bugs: List<BugReportPayload>?
)