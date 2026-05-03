package com.example.app.feature.home.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.app.core.storage.TokenStorage
import com.example.app.feature.music.domain.model.TrackModel
import com.example.app.feature.music.presentation.formatDuration
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@Composable
fun HomeScreen(viewModel: HomeViewModel = koinViewModel()) {
    val tokenStorage = koinInject<TokenStorage>()
    val name by tokenStorage.name.collectAsState(initial = "")
    val uiState by viewModel.uiState.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {

        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                                MaterialTheme.colorScheme.background
                            )
                        )
                    )
                    .padding(horizontal = 24.dp, vertical = 48.dp)
            ) {
                Column {
                    Text(
                        text  = "Good evening,",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text  = name ?: "Friend",
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        item {
            Text(
                text     = "Quick Access",
                style    = MaterialTheme.typography.titleLarge,
                color    = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(start = 24.dp, top = 8.dp, bottom = 16.dp)
            )
        }

        item {
            LazyRow(
                contentPadding        = PaddingValues(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(quickAccessItems) { item ->
                    QuickAccessCard(item = item)
                }
            }
        }

        item { Spacer(modifier = Modifier.height(32.dp)) }

        item { SectionHeader(title = "Recently Added Music") }

        item {
            when {
                uiState.isLoadingTracks -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                }
                uiState.recentTracks.isEmpty() -> EmptySection(message = "No tracks yet")
                else -> {
                    LazyRow(
                        contentPadding        = PaddingValues(horizontal = 24.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(uiState.recentTracks, key = { it.id }) { track ->
                            RecentTrackCard(track = track)
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(32.dp)) }

        item { SectionHeader(title = "Recently Added Movies") }

        item { EmptySection(message = "No movies yet") }
    }
}

@Composable
fun RecentTrackCard(track: TrackModel) {
    Card(
        modifier = Modifier
            .width(160.dp)
            .height(160.dp),
        shape  = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                            MaterialTheme.colorScheme.surfaceVariant
                        )
                    )
                )
        ) {
            Icon(
                imageVector        = Icons.Filled.MusicNote,
                contentDescription = null,
                tint               = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                modifier           = Modifier
                    .size(64.dp)
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
            )
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(12.dp)
            ) {
                Text(
                    text     = track.title,
                    style    = MaterialTheme.typography.titleSmall,
                    color    = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text     = track.artist,
                    style    = MaterialTheme.typography.bodySmall,
                    color    = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector        = Icons.Filled.PlayArrow,
                        contentDescription = null,
                        tint               = MaterialTheme.colorScheme.primary,
                        modifier           = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text  = formatDuration(track.duration),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

data class QuickAccessItem(
    val title : String,
    val icon  : ImageVector,
    val color : Color
)

val quickAccessItems = listOf(
    QuickAccessItem("Music",  Icons.Filled.MusicNote, Color(0xFF1DB954)),
    QuickAccessItem("Movies", Icons.Filled.Movie,     Color(0xFFE50914)),
    QuickAccessItem("Books",  Icons.Filled.Book,      Color(0xFF2196F3)),
)

@Composable
fun QuickAccessCard(item: QuickAccessItem) {
    Card(
        modifier = Modifier
            .width(120.dp)
            .height(120.dp),
        shape  = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            item.color.copy(alpha = 0.6f),
                            item.color.copy(alpha = 0.2f)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector        = item.icon,
                    contentDescription = item.title,
                    tint               = Color.White,
                    modifier           = Modifier.size(40.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text  = item.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text     = title,
        style    = MaterialTheme.typography.titleLarge,
        color    = MaterialTheme.colorScheme.onBackground,
        modifier = Modifier.padding(start = 24.dp, bottom = 16.dp)
    )
}

@Composable
fun EmptySection(message: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .height(100.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text  = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
