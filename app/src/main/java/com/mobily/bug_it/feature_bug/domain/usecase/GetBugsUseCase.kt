package com.mobily.bug_it.feature_bug.domain.usecase

import com.mobily.bug_it.feature_bug.domain.model.BugReport
import com.mobily.bug_it.feature_bug.domain.repository.BugRepository
import javax.inject.Inject

/**
 * Use case to retrieve the list of bug reports.
 * Domain layer logic: Fetches and potentially filters/sorts data.
 */
class GetBugsUseCase @Inject constructor(
    private val repository: BugRepository
) {
    suspend operator fun invoke(): Result<List<BugReport>> {
        return repository.getBugs()
    }
}
