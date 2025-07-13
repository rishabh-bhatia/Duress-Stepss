package com.rishabh.duressstepss.stepcounter.ui

data class StepCounterUiState(
    val rawStepCount: Int? = null,
    val stepsSinceLaunch: Int = 0,
    val isSensorAvailable: Boolean = true,
    val initialStepCount: Int? = null,
    val hasStartedObserving: Boolean = false
)