package com.example.app.feature.auth

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.app.core.ui.theme.VelvetTheme
import com.example.app.feature.auth.presentation.RegisterScreen
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RegisterScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()
    private fun setScreen(
        onRegisterSuccess: () -> Unit = {},
        onLoginClick: () -> Unit = {}
    ) {
        composeTestRule.setContent {
            VelvetTheme {
                RegisterScreen(
                    onRegisterSuccess = onRegisterSuccess,
                    onLoginClick      = onLoginClick
                )
            }
        }
    }

    @Test
    fun displays_title() {
        setScreen()
        composeTestRule.onAllNodesWithText("Create Account").onFirst().assertIsDisplayed()
    }

    @Test
    fun displays_subtitle() {
        setScreen()
        composeTestRule.onNodeWithText("Join Velvet today").assertIsDisplayed()
    }

    @Test
    fun displays_name_field() {
        setScreen()
        composeTestRule.onNodeWithText("Full Name").assertIsDisplayed()
    }

    @Test
    fun displays_email_field() {
        setScreen()
        composeTestRule.onNodeWithText("Email").assertIsDisplayed()
    }

    @Test
    fun displays_password_field() {
        setScreen()
        composeTestRule.onNodeWithText("Password").assertIsDisplayed()
    }

    @Test
    fun displays_create_account_button() {
        setScreen()
        composeTestRule.onAllNodesWithText("Create Account")
            .filterToOne(hasClickAction())
            .assertIsDisplayed()
    }

    @Test
    fun displays_sign_in_link() {
        setScreen()
        composeTestRule.onNodeWithText("Sign In").assertIsDisplayed()
    }

    @Test
    fun name_field_accepts_input() {
        setScreen()
        composeTestRule.onNodeWithText("Full Name").performTextInput("Иван Иванов")
        composeTestRule.onNodeWithText("Иван Иванов").assertIsDisplayed()
    }

    @Test
    fun email_field_accepts_input() {
        setScreen()
        composeTestRule.onNodeWithText("Email").performTextInput("ivan@test.com")
        composeTestRule.onNodeWithText("ivan@test.com").assertIsDisplayed()
    }

    @Test
    fun click_sign_in_fires_callback() {
        var clicked = false
        setScreen(onLoginClick = { clicked = true })
        composeTestRule.onNodeWithText("Sign In").performClick()
        assert(clicked) { "onLoginClick не был вызван" }
    }

    @Test
    fun empty_name_shows_validation_error() {
        setScreen()
        // email и password валидны, имя пустое
        composeTestRule.onNodeWithText("Email").performTextInput("user@test.com")
        composeTestRule.onNodeWithText("Password").performTextInput("password123")
        composeTestRule.onAllNodesWithText("Create Account")
            .filterToOne(hasClickAction())
            .performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Name cannot be empty").assertIsDisplayed()
    }

    @Test
    fun empty_email_shows_validation_error() {
        setScreen()
        composeTestRule.onAllNodesWithText("Create Account")
            .filterToOne(hasClickAction())
            .performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Email cannot be empty").assertIsDisplayed()
    }

    @Test
    fun short_password_shows_error() {
        setScreen()
        composeTestRule.onNodeWithText("Full Name").performTextInput("Иван")
        composeTestRule.onNodeWithText("Email").performTextInput("ivan@test.com")
        composeTestRule.onNodeWithText("Password").performTextInput("123")
        composeTestRule.onAllNodesWithText("Create Account")
            .filterToOne(hasClickAction())
            .performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Password min 6 characters").assertIsDisplayed()
    }
}
