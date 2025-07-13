package com.rishabh.duressstepss.core.util

import com.rishabh.duressstepss.stepcounter.domain.usecase.SaveStepCountUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * A utility class responsible for executing a save operation at a regular interval.
 * This class is designed to be reusable and is not tied to any specific feature.
 *
 * @property saveStepCountUseCase The use case to be executed periodically.
 * @property externalScope The [CoroutineScope] in which the periodic task will be launched.
 * This allows the lifecycle of the saving task to be managed by an external component like a ViewModel.
 */
class PeriodicSaver @Inject constructor(
    private val saveStepCountUseCase: SaveStepCountUseCase,
    private val externalScope: CoroutineScope
) {
    private var job: Job? = null

    /**
     * Starts the periodic saving process. If a saving process is already running, it will be cancelled
     * and a new one will be started.
     *
     * @param stepCountProvider A function that will be invoked to get the current step count when it's time to save.
     * @param periodMs The interval in milliseconds between save operations. Defaults to 60000ms.
     */
    fun start(stepCountProvider: () -> Int, periodMs: Long = 60_000) {
        job?.cancel()
        job = externalScope.launch {
            while (true) {
                delay(periodMs)
                saveStepCountUseCase(stepCountProvider())
            }
        }
    }

    /**
     * Cancels the currently running save operation, if any.
     */
    fun cancel() {
        job?.cancel()
    }
}