package com.example.app.feature.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app.feature.book.domain.model.BookModel
import com.example.app.feature.book.domain.usecase.GetBooksUseCase
import com.example.app.feature.music.domain.model.TrackModel
import com.example.app.feature.music.domain.usecase.GetTracksUseCase
import com.example.app.feature.video.domain.model.VideoModel
import com.example.app.feature.video.domain.usecase.GetVideoUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class HomeUiState(
    val recentTracks: List<TrackModel> = emptyList(),
    val recentVideos: List<VideoModel> = emptyList(),
    val recentBooks:  List<BookModel>  = emptyList(),
    val isLoadingTracks: Boolean = true,
    val isLoadingVideos: Boolean = true,
    val isLoadingBooks:  Boolean = true
)

class HomeViewModel(
    private val getTracksUseCase: GetTracksUseCase,
    private val getVideosUseCase: GetVideoUseCase,
    private val getBooksUseCase:  GetBooksUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadTracks()
        loadVideos()
        loadBooks()
    }

    fun loadTracks() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoadingTracks = true)
            getTracksUseCase()
                .onSuccess { tracks ->
                    _uiState.value = _uiState.value.copy(
                        recentTracks    = tracks.take(5),
                        isLoadingTracks = false
                    )
                }
                .onFailure {
                    _uiState.value = _uiState.value.copy(isLoadingTracks = false)
                }
        }
    }

    fun loadVideos() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoadingVideos = true)
            getVideosUseCase()
                .onSuccess { videos ->
                    _uiState.value = _uiState.value.copy(
                        recentVideos    = videos.take(5),
                        isLoadingVideos = false
                    )
                }
                .onFailure {
                    _uiState.value = _uiState.value.copy(isLoadingVideos = false)
                }
        }
    }

    fun loadBooks() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoadingBooks = true)
            getBooksUseCase()
                .onSuccess { books ->
                    _uiState.value = _uiState.value.copy(
                        recentBooks    = books.take(5),
                        isLoadingBooks = false
                    )
                }
                .onFailure {
                    _uiState.value = _uiState.value.copy(isLoadingBooks = false)
                }
        }
    }
}
