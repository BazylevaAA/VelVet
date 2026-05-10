package com.example.app.feature.home

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.app.core.ui.theme.VelvetTheme
import com.example.app.feature.home.presentation.HomeScreen
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HomeScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun setScreen(
        onNavigateToMusic: () -> Unit = {},
        onNavigateToMovies: () -> Unit = {},
        onNavigateToBooks: () -> Unit = {},
        onLogout: () -> Unit = {}
    ) {
        composeTestRule.setContent {
            VelvetTheme {
                HomeScreen(
                    onNavigateToMusic = onNavigateToMusic,
                    onNavigateToMovies = onNavigateToMovies,
                    onNavigateToBooks = onNavigateToBooks,
                    onLogout = onLogout
                )
            }
        }
    }

    @Test
    fun displays_greeting_text() {
        setScreen()
        composeTestRule.onNodeWithText("Good evening,").assertIsDisplayed()
    }

    @Test
    fun displays_quick_access_section() {
        setScreen()
        composeTestRule.onNodeWithText("Quick Access").assertIsDisplayed()
    }

    @Test
    fun displays_music_card() {
        setScreen()
        composeTestRule.onNodeWithText("Music").assertIsDisplayed()
    }

    @Test
    fun displays_movies_card() {
        setScreen()
        composeTestRule.onNodeWithText("Movies").assertIsDisplayed()
    }

    @Test
    fun displays_books_card() {
        setScreen()
        composeTestRule.onNodeWithText("Books").assertIsDisplayed()
    }

    @Test
    fun displays_logout_button() {
        setScreen()
        composeTestRule.onNodeWithContentDescription("Logout").assertIsDisplayed()
    }

    @Test
    fun click_logout_fires_callback() {
        var logoutCalled = false
        setScreen(onLogout = { logoutCalled = true })
        composeTestRule.onNodeWithContentDescription("Logout").performClick()
        assert(logoutCalled) { "onLogout не был вызван" }
    }

    @Test
    fun click_music_card_fires_callback() {
        var navigated = false
        setScreen(onNavigateToMusic = { navigated = true })
        composeTestRule.onNodeWithText("Music").performClick()
        assert(navigated) { "onNavigateToMusic не был вызван" }
    }

    @Test
    fun click_movies_card_fires_callback() {
        var navigated = false
        setScreen(onNavigateToMovies = { navigated = true })
        composeTestRule.onNodeWithText("Movies").performClick()
        assert(navigated) { "onNavigateToMovies не был вызван" }
    }

    @Test
    fun click_books_card_fires_callback() {
        var navigated = false
        setScreen(onNavigateToBooks = { navigated = true })
        composeTestRule.onNodeWithText("Books").performClick()
        assert(navigated) { "onNavigateToBooks не был вызван" }
    }

    @Test
    fun displays_recently_added_music_section() {
        setScreen()
        composeTestRule.onNodeWithText("Recently Added Music").assertIsDisplayed()
    }

    @Test
    fun displays_recently_added_movies_section() {
        setScreen()
        composeTestRule.onNodeWithText("Recently Added Movies").assertIsDisplayed()
    }

    @Test
    fun displays_recently_added_books_section() {
        setScreen()
        composeTestRule.onNodeWithText("Recently Added Books").assertIsDisplayed()
    }
}
