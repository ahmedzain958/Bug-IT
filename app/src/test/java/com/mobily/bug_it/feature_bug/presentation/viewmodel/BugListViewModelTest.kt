package com.mobily.bug_it.feature_bug.presentation.viewmodel

import com.mobily.bug_it.feature_bug.domain.model.BugReport
import com.mobily.bug_it.feature_bug.domain.usecase.GetBugsUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class BugListViewModelTest {

    @Mock
    private lateinit var getBugsUseCase: GetBugsUseCase
    private lateinit var viewModel: BugListViewModel
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
        viewModel = BugListViewModel(getBugsUseCase)
    }

    @Test
    fun `loadBugs failure updates state with error message`() = runTest {
        val errorMessage = "Network Error"
        whenever(getBugsUseCase()).thenReturn(Result.failure(Exception(errorMessage)))

        viewModel.loadBugs()

        assertEquals(errorMessage, viewModel.state.value.error)
        assertFalse(viewModel.state.value.isLoading)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

}
