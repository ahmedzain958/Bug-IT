package com.mobily.bug_it.feature_bug.presentation.screen

import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.mobily.bug_it.feature_bug.presentation.state.BugUiState
import org.junit.Rule
import org.junit.Test

class BugScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun descriptionInput_updatesState() {
        var description = ""
        composeTestRule.setContent {
            BugScreen(
                state = BugUiState(description = description),
                onDescriptionChange = { description = it },
                onPickImage = {},
                onCaptureScreenshot = {},
                onRemoveImage = {},
                onSubmit = {}
            )
        }

        val testDescription = "Test bug description"
        composeTestRule.onNodeWithText("Description").performTextInput(testDescription)
        assert(description == testDescription)
    }

    @Test
    fun submitButton_isEnabled_whenNotLoading() {
        composeTestRule.setContent {
            BugScreen(
                state = BugUiState(isLoading = false),
                onDescriptionChange = {},
                onPickImage = {},
                onCaptureScreenshot = {},
                onRemoveImage = {},
                onSubmit = {}
            )
        }

        composeTestRule.onNodeWithText("Submit Bug").assertIsEnabled()
    }
}
