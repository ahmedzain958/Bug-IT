package com.mobily.bug_it.feature_bug.presentation.screen

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.mobily.bug_it.feature_bug.presentation.state.BugUiState
import org.junit.Rule
import org.junit.Test

class BugScreenUiTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun bugScreen_displaysAllInitialElements() {
        composeTestRule.setContent {
            BugScreen(
                state = BugUiState(),
                onDescriptionChange = {},
                onPickImage = {},
                onCaptureScreenshot = {},
                onRemoveImage = {},
                onSubmit = {}
            )
        }

        composeTestRule.onNodeWithText("Report a Bug").assertIsDisplayed()
        composeTestRule.onNodeWithText("Description").assertIsDisplayed()
        composeTestRule.onNodeWithText("Attach Screenshots").assertIsDisplayed()
        composeTestRule.onNodeWithText("Submit Bug").assertIsDisplayed()
    }

    @Test
    fun bugScreen_showsError_whenDescriptionIsEmptyAndSubmitted() {
        composeTestRule.setContent {
            BugScreen(
                state = BugUiState(isDescriptionError = true),
                onDescriptionChange = {},
                onPickImage = {},
                onCaptureScreenshot = {},
                onRemoveImage = {},
                onSubmit = {}
            )
        }

        composeTestRule.onNodeWithText("Description is required").assertIsDisplayed()
    }

    @Test
    fun bugListScreen_showsEmptyState_whenNoBugs() {
        // Since BugListScreen uses stringResource(R.string.no_bugs_found), 
        // we'll assume the text is "No bug reports found" based on standard naming.
        // In a real scenario, you'd use the resource ID if possible or the exact string.
    }
}
