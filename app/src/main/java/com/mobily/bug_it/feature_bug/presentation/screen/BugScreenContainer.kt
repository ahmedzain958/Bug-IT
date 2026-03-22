package com.mobily.bug_it.feature_bug.presentation.screen

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mobily.bug_it.feature_bug.presentation.state.BugEvent
import com.mobily.bug_it.feature_bug.presentation.viewmodel.BugViewModel

@Composable
fun BugScreenContainer(
    viewModel: BugViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        viewModel.addImages(uris)
    }

    BugScreen(
        state = state,
        onDescriptionChange = viewModel::onDescriptionChange,
        onPickImage = { launcher.launch("image/*") },
        onRemoveImage = viewModel::removeImage,
        onSubmit = viewModel::submit
    )
    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is BugEvent.BugSubmitted -> {
                    Toast.makeText(context, "Bug submitted ✅", Toast.LENGTH_SHORT).show()
                }

                is BugEvent.ShowError -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}