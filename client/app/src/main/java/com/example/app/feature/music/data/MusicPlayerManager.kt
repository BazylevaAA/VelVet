package com.example.app.feature.music.data

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

class MusicPlayerManager(
    private val context: Context,
    private val tokenStorage: TokenStorage
) {
    private var player: ExoPlayer? = null

    private val _currentTrackId = MutableStateFlow<Int?>(null)
    val currentTrackId: StateFlow<Int?> = _currentTrackId.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    @OptIn(UnstableApi::class)
    suspend fun playTrack(trackId: Int) {
        val token = tokenStorage.token.first() ?: ""

        player?.release()

        val dataSourceFactory = DefaultHttpDataSource.Factory()
            .setDefaultRequestProperties(mapOf("Authorization" to "Bearer $token"))

        player = ExoPlayer.Builder(context)
            .setMediaSourceFactory(ProgressiveMediaSource.Factory(dataSourceFactory))
            .build()
            .also { p ->
                p.setMediaItem(MediaItem.fromUri("$BASE_URL:8082/api/v1/tracks/$trackId/stream"))
                p.prepare()
                p.playWhenReady = true
            }

        _currentTrackId.value = trackId
        _isPlaying.value = true
    }

    fun togglePlayPause() {
        val p = player ?: return
        if (p.isPlaying) {
            p.pause()
            _isPlaying.value = false
        } else {
            p.play()
            _isPlaying.value = true
        }
    }

    fun seekTo(positionMs: Long) {
        player?.seekTo(positionMs)
    }

    fun getCurrentPosition(): Long = player?.currentPosition ?: 0L

    fun getDuration(): Long = player?.duration?.takeIf { it > 0 } ?: 0L

    fun stop() {
        player?.release()
        player = null
        _currentTrackId.value = null
        _isPlaying.value = false
    }
}
