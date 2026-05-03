package com.example.app.feature.movie.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app.feature.video.domain.model.VideoModel
import com.example.app.feature.video.domain.usecase.DeleteVideoUseCase
import com.example.app.feature.video.domain.usecase.GetVideoUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class VideoUiState {
    object Loading: VideoUiState()
    object Empty : VideoUiState()
    data class Success(val videos: List<VideoModel>) : VideoUiState()
    data class Error(val message: String): VideoUiState()
}

class VideoViewModel(
    private val getVideosUseCase  : GetVideoUseCase,
    private val deleteVideoUseCase: DeleteVideoUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<VideoUiState>(VideoUiState.Loading)
    val uiState: StateFlow<VideoUiState> = _uiState.asStateFlow()

    init {
        loadVideos()
    }

    fun loadVideos() {
        viewModelScope.launch {
            _uiState.value = VideoUiState.Loading
            getVideosUseCase()
                .onSuccess { videos ->
                    _uiState.value = if (videos.isEmpty())
                        VideoUiState.Empty
                    else
                        VideoUiState.Success(videos)
                }
                .onFailure {
                    _uiState.value = VideoUiState.Error(it.message ?: "Unknown error")
                }
        }
    }

    fun deleteVideo(id: Int) {
        viewModelScope.launch {
            deleteVideoUseCase(id)
                .onSuccess { loadVideos() }
                .onFailure {
                    _uiState.value = VideoUiState.Error(it.message ?: "Unknown error")
                }
        }
    }
}