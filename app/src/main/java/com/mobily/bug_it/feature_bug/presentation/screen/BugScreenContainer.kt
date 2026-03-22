package com.mobily.bug_it.feature_bug.presentation.screen

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.view.PixelCopy
import android.view.View
import android.view.Window
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mobily.bug_it.R
import com.mobily.bug_it.feature_bug.presentation.state.BugEvent
import com.mobily.bug_it.feature_bug.presentation.viewmodel.BugViewModel
import java.io.File
import java.io.FileOutputStream

@Composable
fun BugScreenContainer(
    viewModel: BugViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val view = LocalView.current

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        viewModel.addImages(uris)
    }

    fun captureScreenshot(view: View, onBitmapCaptured: (Bitmap) -> Unit) {
        val window = (context as? Activity)?.window ?: return
        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val locationOfViewInWindow = IntArray(2)
            view.getLocationInWindow(locationOfViewInWindow)
            try {
                PixelCopy.request(
                    window,
                    android.graphics.Rect(
                        locationOfViewInWindow[0],
                        locationOfViewInWindow[1],
                        locationOfViewInWindow[0] + view.width,
                        locationOfViewInWindow[1] + view.height
                    ),
                    bitmap,
                    { copyResult ->
                        if (copyResult == PixelCopy.SUCCESS) {
                            onBitmapCaptured(bitmap)
                        }
                    },
                    Handler(Looper.getMainLooper())
                )
            } catch (e: IllegalArgumentException) {
                e.printStackTrace()
            }
        } else {
            val canvas = Canvas(bitmap)
            view.draw(canvas)
            onBitmapCaptured(bitmap)
        }
    }

    fun saveBitmapAndAdd(bitmap: Bitmap) {
        val file = File(context.cacheDir, "screenshots").apply { if (!exists()) mkdirs() }
        val screenshotFile = File(file, "screenshot_${System.currentTimeMillis()}.png")
        try {
            FileOutputStream(screenshotFile).use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            }
            viewModel.addImages(listOf(Uri.fromFile(screenshotFile)))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    BugScreen(
        state = state,
        onDescriptionChange = viewModel::onDescriptionChange,
        onPickImage = { launcher.launch("image/*") },
        onCaptureScreenshot = {
            captureScreenshot(view) { bitmap ->
                saveBitmapAndAdd(bitmap)
            }
        },
        onRemoveImage = viewModel::removeImage,
        onSubmit = viewModel::submit
    )

    // One-time events
    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is BugEvent.BugSubmitted -> {
                    Toast.makeText(context, context.getString(R.string.bug_submitted_success), Toast.LENGTH_SHORT).show()
                }

                is BugEvent.ShowError -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}