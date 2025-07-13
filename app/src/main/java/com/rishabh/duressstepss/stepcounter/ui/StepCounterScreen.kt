package com.rishabh.duressstepss.stepcounter.ui

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale

import androidx.compose.ui.tooling.preview.Preview
import com.rishabh.duressstepss.core.ui.theme.DuressStepssTheme
import com.rishabh.duressstepss.core.util.TestTags

private const val TAG = "StepCounterScreen"

private const val LEGACY_ACTIVITY_RECOGNITION_PERMISSION = "com.google.android.gms.permission.ACTIVITY_RECOGNITION"

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun StepCounterScreen(
    modifier: Modifier = Modifier,
    viewModel: StepCounterViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    val permissionState = rememberPermissionState(
        permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            Manifest.permission.ACTIVITY_RECOGNITION
        } else {
            LEGACY_ACTIVITY_RECOGNITION_PERMISSION
        }
    )

    LaunchedEffect(Unit) {
        if (!permissionState.status.isGranted) {
            Log.d(TAG, "Permission not granted, launching request.")
            permissionState.launchPermissionRequest()
        }
    }

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        when {
            permissionState.status.isGranted -> {
                LaunchedEffect(Unit) {
                    viewModel.onPermissionGranted()
                }

                Log.d(TAG, "Permission is granted.")
                if (uiState.isSensorAvailable) {
                    StepCounterContent(
                        steps = uiState.stepsSinceLaunch,
                        onResetClick = viewModel::onResetClick
                    )
                } else {
                    ErrorMessage(
                        text = "This device does not have a step counter sensor.",
                        modifier = Modifier.testTag(TestTags.SENSOR_UNAVAILABLE_ERROR)
                    )
                }
            }
            permissionState.status.shouldShowRationale -> {
                Log.d(TAG, "Permission rationale should be shown.")
                PermissionRationale(
                    onGrantClick = { permissionState.launchPermissionRequest() }
                )
            }
            else -> {
                Log.w(TAG, "Permission is permanently denied.")
                PermissionDenied(
                    onSettingsClick = {
                        val intent = Intent(
                            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                            Uri.fromParts("package", context.packageName, null)
                        )
                        context.startActivity(intent)
                    }
                )
            }
        }
    }
}

@Composable
private fun StepCounterContent(steps: Int, onResetClick: () -> Unit) {
    Text(
        text = "Steps since launch/reset:",
        style = MaterialTheme.typography.titleMedium
    )
    Spacer(modifier = Modifier.height(16.dp))
    Text(
        text = "$steps",
        style = MaterialTheme.typography.displayLarge,
        modifier = Modifier.testTag(TestTags.STEP_COUNT_VALUE)
    )
    Spacer(modifier = Modifier.height(32.dp))
    Button(
        onClick = onResetClick,
        modifier = Modifier.testTag(TestTags.RESET_BUTTON)
    ) {
        Text("Reset")
    }
}

@Composable
private fun PermissionRationale(onGrantClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.padding(16.dp)
    ) {
        ErrorMessage(text = "Activity Recognition permission is required to count steps.")
        Button(onClick = onGrantClick) {
            Text("Grant Permission")
        }
    }
}

@Composable
private fun PermissionDenied(onSettingsClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.padding(16.dp)
    ) {
        ErrorMessage(text = "Permission was permanently denied. You can grant it in the app settings.")
        Button(onClick = onSettingsClick) {
            Text("Open Settings")
        }
    }
}

@Composable
private fun ErrorMessage(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyLarge,
        textAlign = TextAlign.Center,
        modifier = modifier.padding(16.dp)
    )
}


@Preview(showBackground = true, name = "Permission Rationale")
@Composable
private fun PermissionRationalePreview() {
    DuressStepssTheme {
        PermissionRationale(onGrantClick = {})
    }
}
