package com.example.app.feature.music.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app.feature.music.data.MusicPlayerManager
import com.example.app.feature.music.domain.model.TrackModel
import com.example.app.feature.music.domain.usecase.DeleteTrackUseCase
import com.example.app.feature.music.domain.usecase.GetTracksUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class MusicUiState {
    object Loading : MusicUiState()
    object Empty : MusicUiState()
    data class Success(val tracks: List<TrackModel>) : MusicUiState()
    data class Error(val message: String) : MusicUiState()
}

class MusicViewModel(
    private val getTracksUseCase: GetTracksUseCase,
    private val deleteTrackUseCase: DeleteTrackUseCase,
    private val playerManager: MusicPlayerManager
) : ViewModel() {

    private val _uiState = MutableStateFlow<MusicUiState>(MusicUiState.Loading)
    val uiState: StateFlow<MusicUiState> = _uiState.asStateFlow()

    private val _currentTrack = MutableStateFlow<TrackModel?>(null)
    val currentTrack: StateFlow<TrackModel?> = _currentTrack.asStateFlow()

    private val _currentPosition = MutableStateFlow(0L)
    val currentPosition: StateFlow<Long> = _currentPosition.asStateFlow()

    private val _duration = MutableStateFlow(0L)
    val duration: StateFlow<Long> = _duration.asStateFlow()

    val isPlaying: StateFlow<Boolean> = playerManager.isPlaying

    private var allTracks: List<TrackModel> = emptyList()

    init {
        loadTracks()
        viewModelScope.launch {
            while (true) {
                delay(500)
                if (_currentTrack.value != null) {
                    _currentPosition.value = playerManager.getCurrentPosition()
                    _duration.value = playerManager.getDuration()
                }
            }
        }
    }

    fun loadTracks() {
        viewModelScope.launch {
            _uiState.value = MusicUiState.Loading
            getTracksUseCase()
                .onSuccess { tracks ->
                    allTracks = tracks
                    _uiState.value = if (tracks.isEmpty()) MusicUiState.Empty else MusicUiState.Success(tracks)
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

    fun playTrack(track: TrackModel) {
        _currentTrack.value = track
        _currentPosition.value = 0L
        _duration.value = 0L
        viewModelScope.launch {
            playerManager.playTrack(track.id)
        }
    }

    fun togglePlayPause() {
        playerManager.togglePlayPause()
    }

    fun seekTo(positionMs: Long) {
        playerManager.seekTo(positionMs)
        _currentPosition.value = positionMs
    }

    fun nextTrack() {
        val current = _currentTrack.value ?: return
        val idx = allTracks.indexOfFirst { it.id == current.id }
        if (idx != -1 && idx < allTracks.size - 1) playTrack(allTracks[idx + 1])
    }

    fun previousTrack() {
        val current = _currentTrack.value ?: return
        val idx = allTracks.indexOfFirst { it.id == current.id }
        if (idx > 0) playTrack(allTracks[idx - 1])
    }

    fun stopPlayer() {
        playerManager.stop()
        _currentTrack.value = null
        _currentPosition.value = 0L
        _duration.value = 0L
    }
}
