package com.rishabh.duressstepss.stepcounter.ui

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.rishabh.duressstepss.core.util.PeriodicSaver
import com.rishabh.duressstepss.stepcounter.domain.exception.SensorNotAvailableException
import com.rishabh.duressstepss.stepcounter.domain.usecase.GetLatestSavedStepCountUseCase
import com.rishabh.duressstepss.stepcounter.domain.usecase.GetStepCountUseCase
import com.rishabh.duressstepss.util.MainCoroutineRule
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class StepCounterViewModelTest {

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    private lateinit var getStepCountUseCase: GetStepCountUseCase
    private lateinit var periodicSaver: PeriodicSaver
    private lateinit var getLatestSavedStepCountUseCase: GetLatestSavedStepCountUseCase
    private lateinit var viewModel: StepCounterViewModel

    private val stepFlow = MutableSharedFlow<Int>()

    @Before
    fun setUp() {
        getStepCountUseCase = mockk()
        periodicSaver = mockk(relaxed = true)
        getLatestSavedStepCountUseCase = mockk(relaxed = true)
        every { getStepCountUseCase() } returns stepFlow
    }

    private fun createViewModel() {
        viewModel = StepCounterViewModel(
            getStepCountUseCase = getStepCountUseCase,
            periodicSaver = periodicSaver,
            getLatestSavedStepCountUseCase = getLatestSavedStepCountUseCase
        )
    }

    @Test
    fun `state - initial state is correct`() = runTest {
        every { getStepCountUseCase() } returns flowOf()
        createViewModel()

        // When the initial state is collected
        val initialState = viewModel.uiState.value

        // Then the state should be the default initial state
        assertThat(initialState.stepsSinceLaunch).isEqualTo(0)
        assertThat(initialState.rawStepCount).isNull()
        assertThat(initialState.initialStepCount).isNull()
        assertThat(initialState.isSensorAvailable).isTrue()
        assertThat(initialState.hasStartedObserving).isFalse()
    }

    @Test
    fun `onPermissionGranted - starts observing and updates step count correctly`() = runTest {
        createViewModel()

        // When observing the uiState flow
        viewModel.uiState.test {
            // Then the initial state should not be observing
            assertThat(awaitItem().hasStartedObserving).isFalse()

            // When permission is granted
            viewModel.onPermissionGranted()
            runCurrent()

            // Then the state should reflect that observation has started
            assertThat(awaitItem().hasStartedObserving).isTrue()

            // When the sensor emits its first value
            stepFlow.emit(1000)
            runCurrent()

            // Then the steps since launch should be 0 and the initial count should be set
            val state1 = awaitItem()
            assertThat(state1.rawStepCount).isEqualTo(1000)
            assertThat(state1.stepsSinceLaunch).isEqualTo(0)

            // When the sensor emits a new value
            stepFlow.emit(1010)
            runCurrent()

            // Then the steps since launch should be the difference from the initial count
            val state2 = awaitItem()
            assertThat(state2.stepsSinceLaunch).isEqualTo(10)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onPermissionGranted - does nothing if already observing`() = runTest {
        // Given the ViewModel has already started observing
        createViewModel()
        viewModel.onPermissionGranted()
        runCurrent()

        // When permission is granted again
        viewModel.onPermissionGranted()
        runCurrent()

        // Then the use case and saver should only have been started once
        verify(exactly = 1) { getStepCountUseCase() }
        verify(exactly = 1) { periodicSaver.start(any()) }
    }

    @Test
    fun `observeStepCount - handles sensor not available error`() = runTest {
        // Given the step count use case will return an error
        val exception = SensorNotAvailableException()
        every { getStepCountUseCase() } returns flow { throw exception }
        createViewModel()

        // When observing the uiState flow
        viewModel.uiState.test {
            // And permission is granted
            viewModel.onPermissionGranted()
            runCurrent()

            // Then the state should eventually reflect that the sensor is unavailable
            skipItems(2) // Skip initial and hasStartedObserving states
            val errorState = awaitItem()
            assertThat(errorState.isSensorAvailable).isFalse()
        }
    }
}
