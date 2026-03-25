package com.mobily.bug_it.feature_bug.data.repository

import android.net.Uri
import com.mobily.bug_it.feature_bug.data.model.BugReportPayload
import com.mobily.bug_it.feature_bug.data.remote.datasource.BugRemoteDataSource
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class BugRepositoryImplTest {

    @Mock
    private lateinit var remoteDataSource: BugRemoteDataSource
    private lateinit var repository: BugRepositoryImpl

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        repository = BugRepositoryImpl(remoteDataSource)
    }

    @Test
    fun `uploadBug fails if image upload fails`() = runBlocking {
        val uri = mock<Uri>()
        whenever(remoteDataSource.uploadImage(any())).thenReturn(Result.failure(Exception("Upload error")))

        val result = repository.uploadBug("Description", listOf(uri))
        
        assertTrue(result.isFailure)
    }

    @Test
    fun `uploadBug succeeds if all images and bug report upload succeed`() = runBlocking {
        val uri = mock<Uri>()
        whenever(remoteDataSource.uploadImage(any())).thenReturn(Result.success("http://image.url"))
        whenever(remoteDataSource.uploadBug(any())).thenReturn(Result.success(Unit))

        val result = repository.uploadBug("Description", listOf(uri))
        
        assertTrue(result.isSuccess)
    }

    @Test
    fun `getBugs returns mapped domain models`() = runBlocking {
        val payloads = listOf(BugReportPayload("Desc", emptyList(), "2023-10-01T10:00:00Z"))
        whenever(remoteDataSource.getBugs()).thenReturn(Result.success(payloads))

        val result = repository.getBugs()
        
        assertTrue(result.isSuccess)
        assertTrue(result.getOrNull()?.first()?.description == "Desc")
    }
}
