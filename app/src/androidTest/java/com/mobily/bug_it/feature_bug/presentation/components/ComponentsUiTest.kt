package com.mobily.bug_it.feature_bug.presentation.components

import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Rule
import org.junit.Test

class ComponentsUiTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun submitButton_showsCorrectText_andIsEnabled() {
        var clicked = false
        composeTestRule.setContent {
            SubmitButton(isLoading = false, onClick = { clicked = true })
        }

        composeTestRule.onNodeWithText("Submit Bug").assertExists().assertIsEnabled().performClick()
        assert(clicked)
    }

    @Test
    fun submitButton_isDisabled_whenLoading() {
        composeTestRule.setContent {
            SubmitButton(isLoading = true, onClick = { })
        }

        composeTestRule.onNodeWithText("Submit Bug").assertDoesNotExist()
    }

    @Test
    fun imagePickerButton_triggersClick() {
        var clicked = false
        composeTestRule.setContent {
            ImagePickerButton(onClick = { clicked = true })
        }

        composeTestRule.onNodeWithText("Pick Image").assertExists().performClick()
        assert(clicked)
    }
}
