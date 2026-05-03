package com.example.app.feature.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app.feature.music.domain.model.TrackModel
import com.example.app.feature.music.domain.usecase.GetTracksUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class HomeUiState(
    val recentTracks: List<TrackModel> = emptyList(),
    val isLoadingTracks: Boolean = true
)

class HomeViewModel(
    private val getTracksUseCase: GetTracksUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadTracks()
    }

    fun loadTracks() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoadingTracks = true)
            getTracksUseCase()
                .onSuccess { tracks ->
                    _uiState.value = _uiState.value.copy(
                        recentTracks = tracks.take(5),
                        isLoadingTracks = false
                    )
                }
                .onFailure {
                    _uiState.value = _uiState.value.copy(isLoadingTracks = false)
                }
        }
    }
}
