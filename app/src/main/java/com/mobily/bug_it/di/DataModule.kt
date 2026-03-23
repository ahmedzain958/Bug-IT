package com.mobily.bug_it.di

import android.content.ContentResolver
import android.content.Context
import com.mobily.bug_it.feature_bug.data.remote.datasource.BugRemoteDataSource
import com.mobily.bug_it.feature_bug.data.remote.datasource.BugRemoteDataSourceImpl
import com.mobily.bug_it.feature_bug.data.repository.BugRepositoryImpl
import com.mobily.bug_it.feature_bug.domain.repository.BugRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for Data-layer dependencies.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    @Singleton
    abstract fun bindBugRemoteDataSource(
        impl: BugRemoteDataSourceImpl
    ): BugRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindBugRepository(
        impl: BugRepositoryImpl
    ): BugRepository

    companion object {
        @Provides
        @Singleton
        fun provideContentResolver(@ApplicationContext context: Context): ContentResolver {
            return context.contentResolver
        }
    }
}
