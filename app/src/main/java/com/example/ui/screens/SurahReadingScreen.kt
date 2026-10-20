package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Reciters
import com.example.data.SurahEntity
import com.example.data.VerseEntity
import com.example.ui.components.IslamicVerseCard
import com.example.ui.components.PremiumCircularIconButton
import com.example.ui.components.PremiumPillButton
import com.example.ui.components.QuranTopBar
import com.example.ui.theme.*
import com.example.viewmodel.AppScreen
import com.example.viewmodel.QuranViewModel

@Composable
fun SurahReadingScreen(
    viewModel: QuranViewModel,
    modifier: Modifier = Modifier
) {
    val surah by viewModel.selectedSurah.collectAsState()
    val verses by viewModel.currentVerses.collectAsState()
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    val arabicFontSize by viewModel.arabicFontSize.collectAsState()
    val showTranslation by viewModel.showBengaliTranslation.collectAsState()
    val playbackState by viewModel.audioPlayer.playbackState.collectAsState()
    val bookmarks by viewModel.userBookmarks.collectAsState()
    val allSurahs by viewModel.allSurahs.collectAsState()
    val reciterId by viewModel.selectedReciterId.collectAsState()

    val listState = rememberLazyListState()

    val currentSurah = surah ?: return

    // Auto-log reading progress for the first verse
    LaunchedEffect(currentSurah) {
        if (verses.isNotEmpty()) {
            viewModel.logVerseRead(verses.first(), currentSurah.nameBengali)
        }
    }

    Scaffold(
        topBar = {
            QuranTopBar(
                title = "সূরা ${currentSurah.nameBengali} (${currentSurah.nameArabic})",
                subtitle = "${currentSurah.revelationType} • ${currentSurah.verseCount} আয়াত",
                showBackButton = true,
                onBackClick = { viewModel.navigateBack() },
                onSearchClick = { viewModel.navigateTo(AppScreen.SEARCH) },
                onProfileClick = { viewModel.navigateTo(AppScreen.PROFILE) },
                onThemeToggle = { viewModel.toggleTheme() },
                isDarkMode = isDarkMode
            )
        },
        modifier = modifier.testTag("surah_reading_screen")
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Control Bar: Font size adjustment and translation toggle
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .testTag("reading_controls_card"),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = androidx.compose.ui.graphics.SolidColor(MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Font Size Adjuster
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "ফন্ট সাইজ:",
                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                        IconButton(
                            onClick = { viewModel.setArabicFontSize(arabicFontSize - 2f) },
                            modifier = Modifier.size(36.dp).testTag("font_size_decrease")
                        ) {
                            Text("A-", color = IslamicGold, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                        Text(
                            text = "${arabicFontSize.toInt()}sp",
                            style = MaterialTheme.typography.bodySmall.copy(color = IslamicGold, fontWeight = FontWeight.Bold)
                        )
                        IconButton(
                            onClick = { viewModel.setArabicFontSize(arabicFontSize + 2f) },
                            modifier = Modifier.size(36.dp).testTag("font_size_increase")
                        ) {
                            Text("A+", color = IslamicGold, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    }

                    // Translation Toggle
                    TextButton(
                        onClick = { viewModel.toggleBengaliTranslation() },
                        modifier = Modifier.testTag("toggle_translation_button")
                    ) {
                        Icon(
                            imageVector = if (showTranslation) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = "অনুবাদ",
                            tint = IslamicGold,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (showTranslation) "অনুবাদ অন" else "অনুবাদ অফ",
                            color = IslamicGold,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            // Bismillah Banner (except for Surah At-Tawbah #9)
            if (currentSurah.number != 9) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(EmeraldSurfaceVariant)
                        .border(1.dp, IslamicGold.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
                            style = MaterialTheme.typography.titleLarge.copy(
                                color = IslamicGold,
                                fontWeight = FontWeight.Bold,
                                fontSize = 24.sp,
                                textAlign = TextAlign.Center
                            )
                        )
                        if (showTranslation) {
                            Text(
                                text = "শুরু করছি আল্লাহর নামে যিনি পরম করুণাময়, অতি দয়ালু",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = TextWarmWhite,
                                    fontSize = 12.sp
                                ),
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }
            }

            // Verses LazyColumn
            if (verses.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = IslamicGold)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "সূরা ${currentSurah.nameBengali}-এর আয়াত লোড হচ্ছে...",
                            style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                    }
                }
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(verses, key = { it.verseNumber }) { verse ->
                        val isPlaying = playbackState.isPlaying &&
                                playbackState.currentSurah == verse.surahNumber &&
                                playbackState.currentVerse == verse.verseNumber

                        val isBookmarked = bookmarks.any {
                            it.surahNumber == verse.surahNumber && it.verseNumber == verse.verseNumber
                        }

                        IslamicVerseCard(
                            verse = verse,
                            surahName = currentSurah.nameBengali,
                            arabicFontSize = arabicFontSize,
                            showTranslation = showTranslation,
                            isPlaying = isPlaying,
                            isBookmarked = isBookmarked,
                            onPlayClick = {
                                if (isPlaying) {
                                    viewModel.audioPlayer.togglePlayPause()
                                } else {
                                    viewModel.playVerseAudio(verse, currentSurah.nameBengali)
                                }
                            },
                            onBookmarkClick = {
                                viewModel.toggleBookmark(verse, currentSurah.nameBengali)
                            }
                        )
                    }

                    // Next/Previous Surah Navigation at end of list
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            val prevSurah = allSurahs.find { it.number == currentSurah.number - 1 }
                            val nextSurah = allSurahs.find { it.number == currentSurah.number + 1 }

                            if (prevSurah != null) {
                                Button(
                                    onClick = { viewModel.openSurah(prevSurah) },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = EmeraldSurfaceVariant,
                                        contentColor = IslamicGold
                                    ),
                                    modifier = Modifier.testTag("prev_surah_button")
                                ) {
                                    Text("← পূর্ববর্তী সূরা")
                                }
                            } else {
                                Spacer(modifier = Modifier.width(1.dp))
                            }

                            if (nextSurah != null) {
                                Button(
                                    onClick = { viewModel.openSurah(nextSurah) },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = IslamicGold,
                                        contentColor = EmeraldDeepBg
                                    ),
                                    modifier = Modifier.testTag("next_surah_button")
                                ) {
                                    Text("পরবর্তী সূরা →")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
