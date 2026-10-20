package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.Reciters
import com.example.ui.components.PremiumCircularIconButton
import com.example.ui.components.PremiumPillButton
import com.example.ui.components.QuranTopBar
import com.example.ui.theme.*
import com.example.viewmodel.AppScreen
import com.example.viewmodel.QuranViewModel

@Composable
fun AudioPlayerScreen(
    viewModel: QuranViewModel,
    modifier: Modifier = Modifier
) {
    val playbackState by viewModel.audioPlayer.playbackState.collectAsState()
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    val reciterId by viewModel.selectedReciterId.collectAsState()
    val allSurahs by viewModel.allSurahs.collectAsState()

    val currentReciter = Reciters.getById(reciterId)

    fun formatTime(ms: Int): String {
        val totalSecs = ms / 1000
        val mins = totalSecs / 60
        val secs = totalSecs % 60
        return String.format("%02d:%02d", mins, secs)
    }

    Scaffold(
        topBar = {
            QuranTopBar(
                title = "কুরআন তেলাওয়াত প্লেয়ার",
                subtitle = "মধুর কণ্ঠে আল-কুরআনের সুর",
                showBackButton = true,
                onBackClick = { viewModel.navigateBack() },
                onSearchClick = { viewModel.navigateTo(AppScreen.SEARCH) },
                onProfileClick = { viewModel.navigateTo(AppScreen.PROFILE) },
                onThemeToggle = { viewModel.toggleTheme() },
                isDarkMode = isDarkMode
            )
        },
        modifier = modifier.testTag("audio_player_screen")
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // Qari Selector Dropdown Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("qari_selector_card"),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = androidx.compose.ui.graphics.SolidColor(IslamicGold.copy(alpha = 0.4f))
                    )
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "ক্বারী নির্বাচন করুন (Qari Selection):",
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = IslamicGold,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Reciters.list.forEach { r ->
                                val isSelected = r.id == reciterId
                                PremiumPillButton(
                                    onClick = { viewModel.setReciter(r.id) },
                                    text = r.nameBengali.split(" ").take(2).joinToString(" "),
                                    isSelected = isSelected,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Artwork Disk
                Box(
                    modifier = Modifier
                        .size(190.dp)
                        .clip(CircleShape)
                        .background(EmeraldSurfaceVariant)
                        .border(3.dp, IslamicGold, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_quranify_logo),
                        contentDescription = "Quranify Player Disc",
                        modifier = Modifier.size(140.dp)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Track Title & Info
                Text(
                    text = playbackState.currentTitle,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )
                )

                Text(
                    text = "ক্বারী: ${currentReciter.nameBengali} (${currentReciter.bitrate})",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = IslamicGold,
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier.padding(top = 4.dp)
                )

                // Error alert with retry button if any
                if (playbackState.errorMessage != null) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 14.dp)
                            .testTag("audio_error_card"),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = playbackState.errorMessage ?: "",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.onErrorContainer,
                                    textAlign = TextAlign.Center
                                )
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Button(
                                onClick = {
                                    viewModel.audioPlayer.playVerse(
                                        playbackState.currentSurah,
                                        playbackState.currentVerse,
                                        playbackState.currentTitle,
                                        playbackState.currentUrl
                                    )
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = IslamicGold,
                                    contentColor = EmeraldDeepBg
                                ),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("পুনরায় চেষ্টা করুন (Retry)")
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Progress Bar & Times
                Column(modifier = Modifier.fillMaxWidth()) {
                    val maxVal = if (playbackState.durationMs > 0) playbackState.durationMs.toFloat() else 1f
                    val currentVal = playbackState.currentPositionMs.toFloat().coerceIn(0f, maxVal)

                    Slider(
                        value = currentVal,
                        onValueChange = { newVal ->
                            viewModel.audioPlayer.seekTo(newVal.toInt())
                        },
                        valueRange = 0f..maxVal,
                        colors = SliderDefaults.colors(
                            thumbColor = IslamicGold,
                            activeTrackColor = IslamicGold,
                            inactiveTrackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("audio_seek_bar")
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = formatTime(playbackState.currentPositionMs),
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                        Text(
                            text = if (playbackState.durationMs > 0) formatTime(playbackState.durationMs) else "--:--",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Main Playback Controls
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Shuffle Toggle
                    PremiumCircularIconButton(
                        onClick = { viewModel.audioPlayer.toggleShuffle() },
                        icon = Icons.Default.Shuffle,
                        contentDescription = "শাফল",
                        size = 46.dp,
                        iconSize = 22.dp,
                        tint = if (playbackState.isShuffle) IslamicGold else ButtonIconWhite.copy(alpha = 0.7f),
                        borderColor = if (playbackState.isShuffle) IslamicGold else ButtonDarkBorder,
                        testTag = "audio_shuffle_button"
                    )

                    // Previous Track / Verse
                    PremiumCircularIconButton(
                        onClick = {
                            val prevVerse = (playbackState.currentVerse - 1).coerceAtLeast(1)
                            val surahName = allSurahs.find { it.number == playbackState.currentSurah }?.nameBengali ?: "সূরা"
                            val title = "$surahName (${playbackState.currentSurah}:$prevVerse)"
                            val url = currentReciter.getVerseAudioUrl(playbackState.currentSurah, prevVerse)
                            viewModel.audioPlayer.playVerse(playbackState.currentSurah, prevVerse, title, url)
                        },
                        icon = Icons.Default.SkipPrevious,
                        contentDescription = "পূর্ববর্তী",
                        size = 52.dp,
                        iconSize = 28.dp,
                        tint = Color.White,
                        testTag = "audio_prev_button"
                    )

                    // Play/Pause Big Circular Button
                    Surface(
                        onClick = {
                            if (playbackState.currentUrl.isBlank()) {
                                val url = currentReciter.getVerseAudioUrl(1, 1)
                                viewModel.audioPlayer.playVerse(1, 1, "আল-ফাতিহা (১:১)", url)
                            } else {
                                viewModel.audioPlayer.togglePlayPause()
                            }
                        },
                        modifier = Modifier
                            .size(72.dp)
                            .testTag("audio_play_pause_button"),
                        shape = CircleShape,
                        color = ButtonDarkBg,
                        border = androidx.compose.foundation.BorderStroke(2.dp, IslamicGold),
                        tonalElevation = 4.dp,
                        shadowElevation = 8.dp
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            if (playbackState.isBuffering) {
                                CircularProgressIndicator(
                                    color = IslamicGold,
                                    strokeWidth = 3.dp,
                                    modifier = Modifier.size(32.dp)
                                )
                            } else {
                                Icon(
                                    imageVector = if (playbackState.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                    contentDescription = if (playbackState.isPlaying) "বিরতি" else "চালু করুন",
                                    tint = IslamicGold,
                                    modifier = Modifier.size(40.dp)
                                )
                            }
                        }
                    }

                    // Next Track / Verse
                    PremiumCircularIconButton(
                        onClick = {
                            val nextVerse = playbackState.currentVerse + 1
                            val surahName = allSurahs.find { it.number == playbackState.currentSurah }?.nameBengali ?: "সূরা"
                            val title = "$surahName (${playbackState.currentSurah}:$nextVerse)"
                            val url = currentReciter.getVerseAudioUrl(playbackState.currentSurah, nextVerse)
                            viewModel.audioPlayer.playVerse(playbackState.currentSurah, nextVerse, title, url)
                        },
                        icon = Icons.Default.SkipNext,
                        contentDescription = "পরবর্তী",
                        size = 52.dp,
                        iconSize = 28.dp,
                        tint = Color.White,
                        testTag = "audio_next_button"
                    )

                    // Repeat Toggle
                    PremiumCircularIconButton(
                        onClick = { viewModel.audioPlayer.toggleRepeat() },
                        icon = Icons.Default.RepeatOne,
                        contentDescription = "পুনরাবৃত্তি",
                        size = 46.dp,
                        iconSize = 22.dp,
                        tint = if (playbackState.isRepeatOne) IslamicGold else ButtonIconWhite.copy(alpha = 0.7f),
                        borderColor = if (playbackState.isRepeatOne) IslamicGold else ButtonDarkBorder,
                        testTag = "audio_repeat_button"
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Playback Speed Selector (Pills)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "গতি:",
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    listOf(0.75f, 1.0f, 1.25f, 1.5f).forEach { speed ->
                        val isSpeed = playbackState.playbackSpeed == speed
                        Box(modifier = Modifier.padding(horizontal = 3.dp)) {
                            PremiumPillButton(
                                onClick = { viewModel.setPlaybackSpeed(speed) },
                                text = "${speed}x",
                                isSelected = isSpeed
                            )
                        }
                    }
                }
            }
        }
    }
}
