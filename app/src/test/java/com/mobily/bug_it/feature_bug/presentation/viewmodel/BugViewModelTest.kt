package com.mobily.bug_it.feature_bug.presentation.viewmodel

import android.net.Uri
import com.mobily.bug_it.feature_bug.domain.usecase.UploadBugUseCase
import com.mobily.bug_it.feature_bug.presentation.state.BugEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import app.cash.turbine.test

@OptIn(ExperimentalCoroutinesApi::class)
class BugViewModelTest {

    @Mock
    private lateinit var uploadBugUseCase: UploadBugUseCase
    private lateinit var viewModel: BugViewModel
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
        viewModel = BugViewModel(uploadBugUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `onDescriptionChange updates state correctly`() {
        val newDescription = "App crashes on start"
        viewModel.onDescriptionChange(newDescription)
        
        assertEquals(newDescription, viewModel.state.value.description)
        assertFalse(viewModel.state.value.isDescriptionError)
    }

    @Test
    fun `submit with empty description sets error state`() = runTest {
        viewModel.onDescriptionChange("")
        viewModel.submit()
        
        assertTrue(viewModel.state.value.isDescriptionError)
        assertEquals("Description is required", viewModel.state.value.error)
    }

    @Test
    fun `submit with valid data calls use case and emits success event`() = runTest {
        whenever(uploadBugUseCase(any(), any())).thenReturn(Result.success(Unit))
        
        viewModel.onDescriptionChange("Valid description")
        
        viewModel.events.test {
            viewModel.submit()
            assertEquals(BugEvent.BugSubmitted, awaitItem())
        }
        
        assertEquals("", viewModel.state.value.description)
        assertFalse(viewModel.state.value.isLoading)
    }
    
    @Test
    fun `addImages updates state with new uris`() {
        val uri = mock<Uri>()
        viewModel.addImages(listOf(uri))
        
        assertTrue(viewModel.state.value.imageUris.contains(uri))
    }
}
