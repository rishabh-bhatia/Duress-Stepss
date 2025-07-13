package com.rishabh.duressstepss.core.di

import com.rishabh.duressstepss.stepcounter.data.repository.StepRepositoryImpl
import com.rishabh.duressstepss.stepcounter.domain.repository.StepRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindStepRepository(
        stepRepositoryImpl: StepRepositoryImpl
    ): StepRepository
}
