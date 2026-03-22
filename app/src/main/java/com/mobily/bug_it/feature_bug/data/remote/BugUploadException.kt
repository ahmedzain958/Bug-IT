package com.mobily.bug_it.feature_bug.data.remote

sealed class BugUploadException(
    override val message: String,
    cause: Throwable? = null
) : Exception(message, cause) {
    object MissingEndpoint : BugUploadException("Upload endpoint is not configured")
    object NetworkUnavailable : BugUploadException("No internet connection. Please try again.")
    object Timeout : BugUploadException("Request timed out. Please try again.")
    data class Http(val code: Int, val body: String?) :
        BugUploadException("Server error ($code)")

    data class Api(val details: String) : BugUploadException(details)
    data class Unknown(val debugMessage: String, val root: Throwable?) :
        BugUploadException("Unexpected upload error", root)
}
