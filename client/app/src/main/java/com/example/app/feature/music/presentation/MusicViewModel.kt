package com.example.app.feature.music.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app.feature.music.domain.model.TrackModel
import com.example.app.feature.music.domain.usecase.DeleteTrackUseCase
import com.example.app.feature.music.domain.usecase.GetTracksUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class MusicUiState {
    object Loading: MusicUiState()
    object Empty: MusicUiState()
    data class Success(val tracks: List<TrackModel>) : MusicUiState()
    data class Error(val message: String): MusicUiState()
}

class MusicViewModel(
    private val getTracksUseCase  : GetTracksUseCase,
    private val deleteTrackUseCase: DeleteTrackUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<MusicUiState>(MusicUiState.Loading)
    val uiState: StateFlow<MusicUiState> = _uiState.asStateFlow()

    init {
        loadTracks()
    }

    fun loadTracks() {
        viewModelScope.launch {
            _uiState.value = MusicUiState.Loading
            getTracksUseCase()
                .onSuccess { tracks ->
                    _uiState.value = if (tracks.isEmpty())
                        MusicUiState.Empty
                    else
                        MusicUiState.Success(tracks)
                }
                .onFailure {
                    _uiState.value = MusicUiState.Error(it.message ?: "Unknown error")
                }
        }
    }

    fun deleteTrack(id: Int) {
        viewModelScope.launch {
            deleteTrackUseCase(id)
                .onSuccess { loadTracks() }
                .onFailure {
                    _uiState.value = MusicUiState.Error(it.message ?: "Unknown error")
                }
        }
    }
}