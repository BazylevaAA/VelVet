package com.example.app.feature.book

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.app.core.ui.theme.VelvetTheme
import com.example.app.feature.book.domain.model.BookModel
import com.example.app.feature.book.presentation.BookItem
import com.example.app.feature.book.presentation.BookScreen
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BookScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val fakeBook = BookModel(
        id = 1,
        title = "Мастер и Маргарита",
        description = "Классика русской литературы",
        author = "Булгаков",
        year = 1967,
        fileUrl = "book.epub",
        coverUrl = null,
        userId = 1
    )

    @Test
    fun displays_my_books_title() {
        composeTestRule.setContent {
            VelvetTheme { BookScreen() }
        }
        composeTestRule.onNodeWithText("My Books").assertIsDisplayed()
    }

    @Test
    fun displays_search_bar() {
        composeTestRule.setContent {
            VelvetTheme { BookScreen() }
        }
        composeTestRule.onNodeWithText("Search books...").assertIsDisplayed()
    }

    @Test
    fun search_bar_accepts_input() {
        composeTestRule.setContent {
            VelvetTheme { BookScreen() }
        }
        composeTestRule.onNodeWithText("Search books...").performTextInput("Булгаков")
        composeTestRule.onNodeWithText("Булгаков").assertIsDisplayed()
    }

    @Test
    fun search_bar_clear_button_clears_input() {
        composeTestRule.setContent {
            VelvetTheme { BookScreen() }
        }
        composeTestRule.onNodeWithText("Search books...").performTextInput("test")
        composeTestRule.onNodeWithContentDescription("Clear search").performClick()
        composeTestRule.onNodeWithText("Search books...").assertIsDisplayed()
    }


    @Test
    fun book_item_displays_title() {
        composeTestRule.setContent {
            VelvetTheme {
                BookItem(book = fakeBook, onOpen = {}, onDelete = {})
            }
        }
        composeTestRule.onNodeWithText("Мастер и Маргарита").assertIsDisplayed()
    }

    @Test
    fun book_item_displays_author() {
        composeTestRule.setContent {
            VelvetTheme {
                BookItem(book = fakeBook, onOpen = {}, onDelete = {})
            }
        }
        composeTestRule.onNodeWithText("Булгаков").assertIsDisplayed()
    }

    @Test
    fun book_item_displays_description() {
        composeTestRule.setContent {
            VelvetTheme {
                BookItem(book = fakeBook, onOpen = {}, onDelete = {})
            }
        }
        composeTestRule.onNodeWithText("Классика русской литературы").assertIsDisplayed()
    }

    @Test
    fun book_item_displays_year() {
        composeTestRule.setContent {
            VelvetTheme {
                BookItem(book = fakeBook, onOpen = {}, onDelete = {})
            }
        }
        composeTestRule.onNodeWithText("1967").assertIsDisplayed()
    }

    @Test
    fun book_item_displays_epub_badge() {
        composeTestRule.setContent {
            VelvetTheme {
                BookItem(book = fakeBook, onOpen = {}, onDelete = {})
            }
        }
        composeTestRule.onNodeWithText("EPUB").assertIsDisplayed()
    }

    @Test
    fun book_item_displays_pdf_badge() {
        val pdfBook = fakeBook.copy(fileUrl = "book.pdf")
        composeTestRule.setContent {
            VelvetTheme {
                BookItem(book = pdfBook, onOpen = {}, onDelete = {})
            }
        }
        composeTestRule.onNodeWithText("PDF").assertIsDisplayed()
    }

    @Test
    fun book_item_click_fires_open_callback() {
        var opened = false
        composeTestRule.setContent {
            VelvetTheme {
                BookItem(book = fakeBook, onOpen = { opened = true }, onDelete = {})
            }
        }
        composeTestRule.onNodeWithText("Мастер и Маргарита").performClick()
        assert(opened) { "onOpen не был вызван" }
    }

    @Test
    fun book_item_menu_shows_delete_option() {
        composeTestRule.setContent {
            VelvetTheme {
                BookItem(book = fakeBook, onOpen = {}, onDelete = {})
            }
        }
        composeTestRule.onAllNodes(hasClickAction()).onLast().performClick()
        composeTestRule.onNodeWithText("Delete").assertIsDisplayed()
    }

    @Test
    fun book_item_delete_fires_callback() {
        var deleted = false
        composeTestRule.setContent {
            VelvetTheme {
                BookItem(book = fakeBook, onOpen = {}, onDelete = { deleted = true })
            }
        }
        composeTestRule.onAllNodes(hasClickAction()).onLast().performClick()
        composeTestRule.onNodeWithText("Delete").performClick()
        assert(deleted) { "onDelete не был вызван" }
    }
}
