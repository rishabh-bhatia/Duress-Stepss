package com.rishabh.duressstepss.stepcounter.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.rule.GrantPermissionRule
import com.rishabh.duressstepss.core.ui.theme.DuressStepssTheme
import com.rishabh.duressstepss.core.util.TestTags
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test

class StepCounterScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @get:Rule
    val permissionRule: GrantPermissionRule = GrantPermissionRule.grant(
        android.Manifest.permission.ACTIVITY_RECOGNITION
    )

    private val mockViewModel: StepCounterViewModel = mockk(relaxed = true)

    @Test
    fun stepCounterContent_displaysCorrectly() {
        // Given the ViewModel provides a specific UI state
        val uiState = StepCounterUiState(stepsSinceLaunch = 1234, isSensorAvailable = true)
        every { mockViewModel.uiState } returns MutableStateFlow(uiState)

        // When the StepCounterScreen is composed
        composeTestRule.setContent {
            DuressStepssTheme {
                StepCounterScreen(viewModel = mockViewModel)
            }
        }

        // Then the permission is granted and the ViewModel is notified
        verify { mockViewModel.onPermissionGranted() }

        // And the step count and reset button are displayed
        composeTestRule.onNodeWithTag(TestTags.STEP_COUNT_VALUE).assertIsDisplayed()
        composeTestRule.onNodeWithTag(TestTags.RESET_BUTTON).assertIsDisplayed()
    }

    @Test
    fun resetButton_callsViewModel() {
        // Given the ViewModel provides a specific UI state
        val uiState = StepCounterUiState(stepsSinceLaunch = 1234, isSensorAvailable = true)
        every { mockViewModel.uiState } returns MutableStateFlow(uiState)

        // When the StepCounterScreen is composed
        composeTestRule.setContent {
            DuressStepssTheme {
                StepCounterScreen(viewModel = mockViewModel)
            }
        }

        // And the reset button is clicked
        composeTestRule.onNodeWithTag(TestTags.RESET_BUTTON).performClick()

        // Then the onResetClick function on the ViewModel is called
        verify { mockViewModel.onResetClick() }
    }

    @Test
    fun sensorNotAvailable_displaysErrorMessage() {
        // Given the ViewModel indicates the sensor is not available
        val uiState = StepCounterUiState(isSensorAvailable = false)
        every { mockViewModel.uiState } returns MutableStateFlow(uiState)

        // When the StepCounterScreen is composed
        composeTestRule.setContent {
            DuressStepssTheme {
                StepCounterScreen(viewModel = mockViewModel)
            }
        }

        // Then the permission is granted and the ViewModel is notified
        verify { mockViewModel.onPermissionGranted() }

        // And the error message is displayed
        composeTestRule.onNodeWithTag(TestTags.SENSOR_UNAVAILABLE_ERROR).assertIsDisplayed()
    }
}
