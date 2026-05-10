package com.example.app.feature.music

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.app.core.ui.theme.VelvetTheme
import com.example.app.feature.music.domain.model.TrackModel
import com.example.app.feature.music.presentation.MusicScreen
import com.example.app.feature.music.presentation.TrackItem
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MusicScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val fakeTrack = TrackModel(
        id = 1,
        title = "Bohemian Rhapsody",
        artist = "Queen",
        album = "A Night at the Opera",
        duration = 354,
        fileUrl = "track.mp3",
        coverUrl = null,
        userId = 1
    )

    @Test
    fun displays_my_music_title() {
        composeTestRule.setContent {
            VelvetTheme { MusicScreen() }
        }
        composeTestRule.onNodeWithText("My Music").assertIsDisplayed()
    }

    @Test
    fun displays_search_bar() {
        composeTestRule.setContent {
            VelvetTheme { MusicScreen() }
        }
        composeTestRule.onNodeWithText("Search tracks...").assertIsDisplayed()
    }

    @Test
    fun search_bar_accepts_input() {
        composeTestRule.setContent {
            VelvetTheme { MusicScreen() }
        }
        composeTestRule.onNodeWithText("Search tracks...").performTextInput("Queen")
        composeTestRule.onNodeWithText("Queen").assertIsDisplayed()
    }

    @Test
    fun search_bar_clear_button_clears_input() {
        composeTestRule.setContent {
            VelvetTheme { MusicScreen() }
        }
        composeTestRule.onNodeWithText("Search tracks...").performTextInput("test")
        composeTestRule.onNodeWithContentDescription("Clear search").performClick()
        composeTestRule.onNodeWithText("Search tracks...").assertIsDisplayed()
    }

    @Test
    fun track_item_displays_title() {
        composeTestRule.setContent {
            VelvetTheme {
                TrackItem(track = fakeTrack, isPlaying = false, isSelected = false, onClick = {}, onDelete = {})
            }
        }
        composeTestRule.onNodeWithText("Bohemian Rhapsody").assertIsDisplayed()
    }

    @Test
    fun track_item_displays_artist() {
        composeTestRule.setContent {
            VelvetTheme {
                TrackItem(track = fakeTrack, isPlaying = false, isSelected = false, onClick = {}, onDelete = {})
            }
        }
        composeTestRule.onNodeWithText("Queen").assertIsDisplayed()
    }

    @Test
    fun track_item_displays_duration() {
        composeTestRule.setContent {
            VelvetTheme {
                TrackItem(track = fakeTrack, isPlaying = false, isSelected = false, onClick = {}, onDelete = {})
            }
        }
        composeTestRule.onNodeWithText("5:54").assertIsDisplayed()
    }

    @Test
    fun track_item_click_fires_callback() {
        var clicked = false
        composeTestRule.setContent {
            VelvetTheme {
                TrackItem(track = fakeTrack, isPlaying = false, isSelected = false, onClick = { clicked = true }, onDelete = {})
            }
        }
        composeTestRule.onNodeWithText("Bohemian Rhapsody").performClick()
        assert(clicked) { "onClick не был вызван" }
    }

    @Test
    fun track_item_playing_state_renders_correctly() {
        composeTestRule.setContent {
            VelvetTheme {
                TrackItem(track = fakeTrack, isPlaying = true, isSelected = true, onClick = {}, onDelete = {})
            }
        }
        composeTestRule.onNodeWithText("Bohemian Rhapsody").assertIsDisplayed()
        composeTestRule.onNodeWithText("Queen").assertIsDisplayed()
    }

    @Test
    fun track_item_not_playing_renders_correctly() {
        composeTestRule.setContent {
            VelvetTheme {
                TrackItem(track = fakeTrack, isPlaying = false, isSelected = false, onClick = {}, onDelete = {})
            }
        }
        composeTestRule.onNodeWithText("Bohemian Rhapsody").assertIsDisplayed()
        composeTestRule.onNodeWithText("Queen").assertIsDisplayed()
    }

    @Test
    fun track_item_delete_fires_callback() {
        var deleted = false
        composeTestRule.setContent {
            VelvetTheme {
                TrackItem(track = fakeTrack, isPlaying = false, isSelected = false, onClick = {}, onDelete = { deleted = true })
            }
        }
        composeTestRule.onAllNodes(hasClickAction()).onLast().performClick()
        composeTestRule.onNodeWithText("Delete").performClick()
        assert(deleted) { "onDelete не был вызван" }
    }
}
