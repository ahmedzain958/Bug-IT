package com.mobily.bug_it.feature_bug.domain.usecase

import com.mobily.bug_it.feature_bug.domain.model.BugReport
import com.mobily.bug_it.feature_bug.domain.repository.BugRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.whenever

class GetBugsUseCaseTest {

    @Mock
    private lateinit var repository: BugRepository
    private lateinit var getBugsUseCase: GetBugsUseCase

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        getBugsUseCase = GetBugsUseCase(repository)
    }

    @Test
    fun `invoke returns success when repository succeeds`() = runBlocking {
        val bugs = listOf(BugReport("2023-10-01", emptyList(), "Test"))
        whenever(repository.getBugs()).thenReturn(Result.success(bugs))

        val result = getBugsUseCase()
        assertTrue(result.isSuccess)
        assertEquals(bugs, result.getOrNull())
    }

    @Test
    fun `invoke returns failure when repository fails`() = runBlocking {
        whenever(repository.getBugs()).thenReturn(Result.failure(Exception("Error")))

        val result = getBugsUseCase()
        assertTrue(result.isFailure)
    }
}
