package com.mobily.bug_it

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.mobily.bug_it.feature_bug.presentation.screen.BugScreenContainer

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BugApp()
        }
    }
}
@Composable
fun BugApp() {
    MaterialTheme {
        BugScreenContainer()
    }
}