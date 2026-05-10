package com.example.app.feature.book.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app.feature.book.data.BookRepository
import com.example.app.feature.book.domain.model.BookModel
import com.example.app.feature.book.domain.usecase.DeleteBookUseCase
import com.example.app.feature.book.domain.usecase.GetBooksUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File

private const val TAG = "BookViewModel"

sealed class BookUiState {
    object Loading : BookUiState()
    object Empty : BookUiState()
    data class Success(val tracks: List<BookModel>) : BookUiState()
    data class Error(val message: String) : BookUiState()
}

sealed class BookOpenState {
    object Idle : BookOpenState()
    object Downloading : BookOpenState()
    data class ReadyEpub(val fileName: String) : BookOpenState()
    data class ReadyPdf(val filePath: String) : BookOpenState()
    data class Error(val message: String) : BookOpenState()
}

class BookViewModel(
    private val getBooksUseCase: GetBooksUseCase,
    private val deleteBookUseCase: DeleteBookUseCase,
    private val bookRepository: BookRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<BookUiState>(BookUiState.Loading)
    val uiState: StateFlow<BookUiState> = _uiState.asStateFlow()

    private val _openState = MutableStateFlow<BookOpenState>(BookOpenState.Idle)
    val openState: StateFlow<BookOpenState> = _openState.asStateFlow()

    init { loadBooks() }

    fun loadBooks() {
        viewModelScope.launch {
            _uiState.value = BookUiState.Loading
            getBooksUseCase()
                .onSuccess { books ->
                    _uiState.value = if (books.isEmpty()) BookUiState.Empty else BookUiState.Success(books)
                }
                .onFailure {
                    _uiState.value = BookUiState.Error(it.message ?: "Unknown error")
                }
        }
    }

    fun openBook(id: Int, fileUrl: String, filesDir: File) {
        viewModelScope.launch {
            _openState.value = BookOpenState.Downloading
            Log.d(TAG, "Starting to download book: id=$id, fileUrl=$fileUrl")
            bookRepository.streamBook(id)
                .onSuccess { bytes ->
                    Log.d(TAG, "Downloaded ${bytes.size} bytes for book $id")
                    val ext      = if (fileUrl.contains(".epub")) "epub" else "pdf"
                    val fileName = "book_$id.$ext"
                    val file     = File(filesDir, fileName)
                    file.writeBytes(bytes)
                    Log.d(TAG, "Saved book to: ${file.absolutePath} (${file.length()} bytes)")
                    _openState.value = if (ext == "epub")
                        BookOpenState.ReadyEpub(fileName)
                    else
                        BookOpenState.ReadyPdf(file.absolutePath)
                }
                .onFailure { error ->
                    Log.e(TAG, "Failed to download book: ${error.message}", error)
                    _openState.value = BookOpenState.Error(error.message ?: "Download failed")
                }
        }
    }

    fun resetOpenState() {
        _openState.value = BookOpenState.Idle
    }

    fun deleteTrack(id: Int) {
        viewModelScope.launch {
            deleteBookUseCase(id)
                .onSuccess { loadBooks() }
                .onFailure {
                    _uiState.value = BookUiState.Error(it.message ?: "Unknown error")
                }
        }
    }
}