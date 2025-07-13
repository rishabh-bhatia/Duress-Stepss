package com.rishabh.duressstepss

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.rishabh.duressstepss.core.ui.theme.DuressStepssTheme
import com.rishabh.duressstepss.stepcounter.ui.StepCounterScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DuressStepssTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    StepCounterScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}
