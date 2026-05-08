package com.example.app.feature.video.data

import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import com.example.app.core.network.BASE_URL
import com.example.app.core.storage.TokenStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first

class VideoPlayerManager(
    private val context: Context,
    private val tokenStorage: TokenStorage
) {
    private val _player = MutableStateFlow<ExoPlayer?>(null)
    val player: StateFlow<ExoPlayer?> = _player.asStateFlow()

    private val _currentVideoId = MutableStateFlow<Int?>(null)
    val currentVideoId: StateFlow<Int?> = _currentVideoId.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    @OptIn(UnstableApi::class)
    suspend fun playVideo(videoId: Int) {
        val token = tokenStorage.token.first() ?: ""

        _player.value?.release()

        val dataSourceFactory = DefaultHttpDataSource.Factory()
            .setDefaultRequestProperties(mapOf("Authorization" to "Bearer $token"))

        val exoPlayer = ExoPlayer.Builder(context)
            .setMediaSourceFactory(ProgressiveMediaSource.Factory(dataSourceFactory))
            .build()
            .also { p ->
                p.setMediaItem(MediaItem.fromUri("$BASE_URL:8083/api/v1/videos/$videoId/stream"))
                p.prepare()
                p.playWhenReady = true
            }

        _player.value = exoPlayer
        _currentVideoId.value = videoId
        _isPlaying.value = true
    }

    fun togglePlayPause() {
        val p = _player.value ?: return
        if (p.isPlaying) {
            p.pause()
            _isPlaying.value = false
        } else {
            p.play()
            _isPlaying.value = true
        }
    }

    fun stop() {
        _player.value?.release()
        _player.value = null
        _currentVideoId.value = null
        _isPlaying.value = false
    }
}
