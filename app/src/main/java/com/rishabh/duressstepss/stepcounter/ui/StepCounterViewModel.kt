package com.rishabh.duressstepss.stepcounter.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rishabh.duressstepss.core.util.PeriodicSaver
import com.rishabh.duressstepss.stepcounter.domain.exception.SensorNotAvailableException
import com.rishabh.duressstepss.stepcounter.domain.usecase.GetLatestSavedStepCountUseCase
import com.rishabh.duressstepss.stepcounter.domain.usecase.GetStepCountUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "StepCounterViewModel"

@HiltViewModel
class StepCounterViewModel @Inject constructor(
    private val getStepCountUseCase: GetStepCountUseCase,
    private val periodicSaver: PeriodicSaver,
    private val getLatestSavedStepCountUseCase: GetLatestSavedStepCountUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(StepCounterUiState())
    val uiState: StateFlow<StepCounterUiState> = _uiState.asStateFlow()

    /**
     * Called when the necessary permissions have been granted by the user.
     * This triggers the observation of the step counter sensor and starts the periodic data saving.
     * This function is idempotent and will not re-initialize the observers if called more than once.
     */
    fun onPermissionGranted() {
        if (uiState.value.hasStartedObserving) return
        _uiState.update { it.copy(hasStartedObserving = true) }

        Log.d(TAG, "Permission granted, starting to observe step count.")
        observeStepCount()
        periodicSaver.start({ uiState.value.stepsSinceLaunch })
    }

    /**
     * Resets the displayed step count. The current raw sensor value is stored as the new baseline,
     * and the displayed count is reset to zero.
     */
    fun onResetClick() {
        Log.d(TAG, "Reset button clicked. Current raw step count is ${uiState.value.rawStepCount}")
        _uiState.update {
            it.copy(
                initialStepCount = it.rawStepCount,
                stepsSinceLaunch = 0
            )
        }
    }

    /**
     * Initializes the collection of step count data from the sensor.
     * It handles the initial state setup and updates the UI state with new step counts.
     * It also handles cases where the step counter sensor is not available on the device.
     */
    private fun observeStepCount() {
        viewModelScope.launch {
            Log.d(TAG, "Starting to observe step count")
            var isFirstEmission = true

            getStepCountUseCase()
                .catch { throwable ->
                    Log.e(TAG, "Error observing step count", throwable)
                    if (throwable is SensorNotAvailableException) {
                        _uiState.update { it.copy(isSensorAvailable = false) }
                    }
                }
                .collect { rawCount ->
                    if (isFirstEmission) {
                        _uiState.update {
                            it.copy(
                                rawStepCount = rawCount,
                                stepsSinceLaunch = 0,
                                isSensorAvailable = true,
                                initialStepCount = rawCount
                            )
                        }
                        isFirstEmission = false
                    } else {
                        val initialStepCount = uiState.value.initialStepCount ?: rawCount
                        val displayedSteps = rawCount - initialStepCount
                        _uiState.update {
                            it.copy(
                                rawStepCount = rawCount,
                                stepsSinceLaunch = displayedSteps
                            )
                        }
                    }
                }
        }
    }

    /**
     * Cancels the periodic saver when the ViewModel is cleared, preventing memory leaks.
     */
    override fun onCleared() {
        super.onCleared()
        periodicSaver.cancel()
    }
}


