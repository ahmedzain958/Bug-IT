package com.mobily.bug_it.feature_bug.presentation.components

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun ImagePickerButton(onClick: () -> Unit) {
    Button(onClick = onClick) {
        Text("Pick Image")
    }
}