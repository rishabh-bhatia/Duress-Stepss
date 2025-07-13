package com.rishabh.duressstepss.stepcounter.domain.repository

import kotlinx.coroutines.flow.Flow

interface StepRepository {
    fun getStepCount(): Flow<Int>
    suspend fun saveStepCount(count: Int)
    fun getLatestSavedStepCount(): Flow<Int?>
}
