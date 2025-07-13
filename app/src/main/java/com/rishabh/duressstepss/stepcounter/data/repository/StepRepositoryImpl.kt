package com.rishabh.duressstepss.stepcounter.data.repository

import android.util.Log
import com.rishabh.duressstepss.core.data.local.StepCountDao
import com.rishabh.duressstepss.stepcounter.data.local.entity.StepCountEntity
import com.rishabh.duressstepss.stepcounter.data.source.StepSensorSource
import com.rishabh.duressstepss.stepcounter.domain.repository.StepRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private const val TAG = "StepRepositoryImpl"

/**
 * Implementation of the [StepRepository].
 * This class is the single source of truth for all step-related data.
 *
 * @property stepSensorSource The data source for real-time step count data from the device sensor.
 * @property stepCountDao The Data Access Object for persisting and retrieving step count data from the local database.
 */
class StepRepositoryImpl @Inject constructor(
    private val stepSensorSource: StepSensorSource,
    private val stepCountDao: StepCountDao
) : StepRepository {

    /**
     * Provides a [Flow] of real-time step counts from the device sensor.
     * The flow will emit a new value each time the sensor detects a new step.
     */
    override fun getStepCount(): Flow<Int> {
        return stepSensorSource.stepCount.map { it.getOrThrow() }
    }

    /**
     * Saves the given step count to the local database, along with a current timestamp.
     *
     * @param count The total step count to be saved.
     */
    override suspend fun saveStepCount(count: Int) {
        val stepCountEntity = StepCountEntity(
            timestamp = System.currentTimeMillis(),
            count = count
        )
        try {
            stepCountDao.insert(stepCountEntity)
            Log.d(TAG, "Successfully saved step count: $count")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to save step count: $count", e)
        }
    }

    /**
     * Retrieves the most recently saved step count from the local database.
     * @return A [Flow] that emits the latest saved step count, or null if no data exists or an error occurs.
     */
    override fun getLatestSavedStepCount(): Flow<Int?> {
        return stepCountDao.getLatestStepCount()
            .map { it?.count }
            .catch { e ->
                Log.e(TAG, "Failed to get latest saved step count", e)
                emit(null)
            }
    }
}
