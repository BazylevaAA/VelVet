package com.example.app.feature.auth

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.app.core.ui.theme.VelvetTheme
import com.example.app.feature.auth.presentation.LoginScreen
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun setScreen(
        onLoginSuccess: () -> Unit = {},
        onRegisterClick: () -> Unit = {}
    ) {
        composeTestRule.setContent {
            VelvetTheme {
                LoginScreen(
                    onLoginSuccess  = onLoginSuccess,
                    onRegisterClick = onRegisterClick
                )
            }
        }
    }

    @Test
    fun displays_velvet_title() {
        setScreen()
        composeTestRule.onNodeWithText("Velvet").assertIsDisplayed()
    }

    @Test
    fun displays_subtitle() {
        setScreen()
        composeTestRule.onNodeWithText("Your personal media library").assertIsDisplayed()
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
    fun displays_sign_in_button() {
        setScreen()
        composeTestRule.onNodeWithText("Sign In").assertIsDisplayed()
    }

    @Test
    fun displays_sign_up_link() {
        setScreen()
        composeTestRule.onNodeWithText("Sign Up").assertIsDisplayed()
    }

    @Test
    fun email_field_accepts_input() {
        setScreen()
        composeTestRule
            .onNodeWithText("Email")
            .performTextInput("test@example.com")
        composeTestRule
            .onNodeWithText("test@example.com")
            .assertIsDisplayed()
    }

    @Test
    fun password_field_accepts_input() {
        setScreen()
        composeTestRule
            .onNodeWithText("Password")
            .performTextInput("secret123")
        composeTestRule
            .onNodeWithText("Password")
            .assertIsDisplayed()
    }

    @Test
    fun click_sign_up_fires_callback() {
        var clicked = false
        setScreen(onRegisterClick = { clicked = true })
        composeTestRule.onNodeWithText("Sign Up").performClick()
        assert(clicked) { "onRegisterClick не был вызван" }
    }

    @Test
    fun empty_email_shows_validation_error() {
        setScreen()
        composeTestRule.onNodeWithText("Sign In").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Email cannot be empty !").assertIsDisplayed()
    }

    @Test
    fun invalid_email_shows_error() {
        setScreen()
        composeTestRule.onNodeWithText("Email").performTextInput("notanemail")
        composeTestRule.onNodeWithText("Password").performTextInput("password123")
        composeTestRule.onNodeWithText("Sign In").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Invalid email").assertIsDisplayed()
    }

    @Test
    fun short_password_shows_error() {
        setScreen()
        composeTestRule.onNodeWithText("Email").performTextInput("user@test.com")
        composeTestRule.onNodeWithText("Password").performTextInput("123")
        composeTestRule.onNodeWithText("Sign In").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Password too short !").assertIsDisplayed()
    }
}
