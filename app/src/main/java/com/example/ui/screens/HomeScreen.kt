package com.example.ui.screens

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.audio.AudioPlaybackState
import com.example.data.Reciters
import com.example.data.SurahEntity
import com.example.data.VerseEntity
import com.example.data.VerifiedHadithData
import com.example.ui.components.IslamicFeatureCard
import com.example.ui.components.PremiumCircularIconButton
import com.example.ui.components.PremiumIslamicBottomBar
import com.example.ui.components.PremiumPillButton
import com.example.ui.components.QuranTopBar
import com.example.ui.theme.*
import com.example.viewmodel.AppScreen
import com.example.viewmodel.QuranViewModel

@Composable
fun HomeScreen(
    viewModel: QuranViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val currentUser by viewModel.currentUser.collectAsState()
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    val playbackState by viewModel.audioPlayer.playbackState.collectAsState()
    val prayerTimes by viewModel.prayerTimes.collectAsState()
    val bookmarks by viewModel.userBookmarks.collectAsState()
    val allSurahs by viewModel.allSurahs.collectAsState()

    // Daily Verse: Ayatul Kursi (2:255)
    val dailyVerse = remember {
        VerseEntity(
            id = 2255,
            surahNumber = 2,
            verseNumber = 255,
            textArabic = "اللَّهُ لَا إِلَٰهَ إِلَّا هُوَ الْحَيُّ الْقَيُّومُ ۚ لَا تَأْخُذُهُ سِنَةٌ وَلَا نَوْمٌ",
            textBengali = "আল্লাহ, যিনি ব্যতীত কোন উপাস্য নেই; তিনি চিরঞ্জীব, সর্বসত্তার ধারক। তন্দ্রা ও নিদ্রা তাঁকে স্পর্শ করতে পারে না।",
            translationSource = "মুহিউদ্দীন খান"
        )
    }

    val isDailyBookmarked = bookmarks.any { it.surahNumber == 2 && it.verseNumber == 255 }
    val isDailyPlaying = playbackState.isPlaying && playbackState.currentSurah == 2 && playbackState.currentVerse == 255

    Scaffold(
        topBar = {
            QuranTopBar(
                title = "কুরআনিফাই ইসলামিক",
                subtitle = "নূর ও হেদায়েতের আলোকবর্তিকা",
                showBackButton = false,
                showProfileButton = false,
                onSearchClick = { viewModel.navigateTo(AppScreen.SEARCH) },
                onThemeToggle = { viewModel.toggleTheme() },
                isDarkMode = isDarkMode
            )
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
            ) {
                PremiumIslamicBottomBar(
                    currentScreen = AppScreen.HOME,
                    onNavigate = { screen ->
                        if (screen == AppScreen.PROFILE && currentUser == null) {
                            viewModel.navigateTo(AppScreen.AUTH)
                        } else {
                            viewModel.navigateTo(screen)
                        }
                    }
                )
            }
        },
        modifier = modifier.testTag("home_screen")
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // Welcome & Next Prayer Banner
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                        .testTag("prayer_banner_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = EmeraldSurfaceVariant
                    ),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = androidx.compose.ui.graphics.SolidColor(IslamicGold.copy(alpha = 0.4f))
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "আসসালামু আলাইকুম!",
                                style = MaterialTheme.typography.bodySmall.copy(color = IslamicGoldLight)
                            )
                            Text(
                                text = currentUser?.displayName ?: "সম্মানিত মুসলিম",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextWarmWhite
                                )
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.AccessTime,
                                    contentDescription = "ওয়াক্ত",
                                    tint = IslamicGold,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "পরবর্তী নামাজ: ${prayerTimes.nextPrayerName} (${prayerTimes.nextPrayerTime})",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = TextWarmWhite,
                                        fontWeight = FontWeight.Medium
                                    )
                                )
                            }
                        }

                        PremiumPillButton(
                            onClick = { viewModel.navigateTo(AppScreen.PRAYER_TIMES) },
                            text = "সময়সূচী",
                            icon = Icons.Default.AccessTime,
                            iconSize = 14.dp,
                            testTag = "home_prayer_button"
                        )
                    }
                }
            }

            // Daily Verse Card
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "আজকের আয়াত (Daily Verse)",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = IslamicGold
                            )
                        )
                        Text(
                            text = "সূরা আল-বাকারা (২:২৫৫)",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                allSurahs.find { it.number == 2 }?.let { viewModel.openSurah(it) }
                            }
                            .testTag("daily_verse_card"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        border = CardDefaults.outlinedCardBorder().copy(
                            brush = androidx.compose.ui.graphics.SolidColor(IslamicGold.copy(alpha = 0.5f))
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = dailyVerse.textArabic,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontSize = 22.sp,
                                    lineHeight = 36.sp,
                                    textAlign = TextAlign.End,
                                    color = MaterialTheme.colorScheme.onSurface
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = dailyVerse.textBengali,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    lineHeight = 22.sp
                                )
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // Action Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                TextButton(
                                    onClick = {
                                        allSurahs.find { it.number == 2 }?.let { viewModel.openSurah(it) }
                                    },
                                    contentPadding = PaddingValues(0.dp)
                                ) {
                                    Text("পূর্ণাঙ্গ আয়াত দেখুন →", color = IslamicGold, fontSize = 13.sp)
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    PremiumCircularIconButton(
                                        onClick = {
                                            if (isDailyPlaying) {
                                                viewModel.audioPlayer.togglePlayPause()
                                            } else {
                                                viewModel.playVerseAudio(dailyVerse, "আল-বাকারা")
                                            }
                                        },
                                        icon = if (isDailyPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                        contentDescription = "তেলাওয়াত",
                                        size = 36.dp,
                                        iconSize = 18.dp,
                                        tint = if (isDailyPlaying) StatusSuccess else IslamicGold,
                                        testTag = "home_daily_play_button"
                                    )

                                    PremiumCircularIconButton(
                                        onClick = {
                                            viewModel.toggleBookmark(dailyVerse, "আল-বাকারা")
                                        },
                                        icon = if (isDailyBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                        contentDescription = "বুকমার্ক",
                                        size = 36.dp,
                                        iconSize = 18.dp,
                                        tint = if (isDailyBookmarked) IslamicGold else ButtonIconWhite,
                                        testTag = "home_daily_bookmark_button"
                                    )

                                    PremiumCircularIconButton(
                                        onClick = {
                                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                                type = "text/plain"
                                                putExtra(
                                                    Intent.EXTRA_TEXT,
                                                    "${dailyVerse.textArabic}\n\n${dailyVerse.textBengali}\n— সূরা আল-বাকারা (২:২৫৫)\n[কুরআনিফাই ইসলামিক]"
                                                )
                                            }
                                            context.startActivity(Intent.createChooser(shareIntent, "আয়াত শেয়ার করুন"))
                                        },
                                        icon = Icons.Default.Share,
                                        contentDescription = "শেয়ার",
                                        size = 36.dp,
                                        iconSize = 18.dp,
                                        tint = ButtonIconWhite,
                                        testTag = "home_daily_share_button"
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Quick Surahs Row
            item {
                Column(modifier = Modifier.padding(top = 16.dp)) {
                    Text(
                        text = "জনপ্রিয় সূরাসমূহ (Quick Surahs)",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = IslamicGold
                        ),
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                    )

                    val quickSurahNumbers = listOf(1, 36, 55, 67, 112)
                    val quickSurahs = allSurahs.filter { it.number in quickSurahNumbers }

                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(quickSurahs) { surah ->
                            Card(
                                modifier = Modifier
                                    .width(135.dp)
                                    .clickable { viewModel.openSurah(surah) }
                                    .testTag("quick_surah_${surah.number}"),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                border = CardDefaults.outlinedCardBorder().copy(
                                    brush = androidx.compose.ui.graphics.SolidColor(IslamicGold.copy(alpha = 0.3f))
                                )
                            ) {
                                Column(
                                    modifier = Modifier.padding(10.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = surah.nameArabic,
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            color = IslamicGold,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                    Text(
                                        text = surah.nameBengali,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = MaterialTheme.colorScheme.onSurface,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    )
                                    Text(
                                        text = "${surah.verseCount} আয়াত",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            fontSize = 10.sp
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Feature Grid Header
            item {
                Text(
                    text = "প্রধান সেবাসমূহ (Main Features)",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = IslamicGold
                    ),
                    modifier = Modifier.padding(start = 16.dp, top = 20.dp, bottom = 8.dp)
                )
            }

            // Grid Items (Row by Row)
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Row 1: Quran & Hadith
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        IslamicFeatureCard(
                            title = "পবিত্র কুরআন",
                            subtitle = "১১৪টি সম্পূর্ণ সূরা",
                            icon = Icons.Default.MenuBook,
                            onClick = { viewModel.navigateTo(AppScreen.SURAH_LIST) },
                            modifier = Modifier.weight(1f),
                            badgeText = "১১৪ সূরা"
                        )
                        IslamicFeatureCard(
                            title = "সহীহ হাদিস",
                            subtitle = "সিহাহ সিত্তাহ সমগ্র সংগ্রহ",
                            icon = Icons.Default.AutoStories,
                            onClick = { viewModel.navigateTo(AppScreen.HADITH) },
                            modifier = Modifier.weight(1f),
                            badgeText = "${VerifiedHadithData.list.size}+ হাদিস"
                        )
                    }

                    // Row 2: Audio & History
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        IslamicFeatureCard(
                            title = "তেলাওয়াত",
                            subtitle = "মধুর অডিও প্লেয়ার",
                            icon = Icons.Default.Headphones,
                            onClick = { viewModel.navigateTo(AppScreen.AUDIO_PLAYER) },
                            modifier = Modifier.weight(1f),
                            badgeText = if (playbackState.isPlaying) "চলছে..." else null
                        )
                        IslamicFeatureCard(
                            title = "পড়ার ইতিহাস",
                            subtitle = "পূর্বে পঠিত আয়াত",
                            icon = Icons.Default.History,
                            onClick = { viewModel.navigateTo(AppScreen.READING_HISTORY) },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Row 3: Bookmarks & Prayer Times
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        IslamicFeatureCard(
                            title = "বুকমার্ক",
                            subtitle = "সংরক্ষিত আয়াতসমূহ",
                            icon = Icons.Default.Bookmark,
                            onClick = { viewModel.navigateTo(AppScreen.BOOKMARKS) },
                            modifier = Modifier.weight(1f),
                            badgeText = "${bookmarks.size} টি"
                        )
                        IslamicFeatureCard(
                            title = "নামাজের সময়",
                            subtitle = "৫ ওয়াক্তের সময়সূচী",
                            icon = Icons.Default.AccessTime,
                            onClick = { viewModel.navigateTo(AppScreen.PRAYER_TIMES) },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Row 4: Qibla & Premium
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        IslamicFeatureCard(
                            title = "কিবলা কম্পাস",
                            subtitle = "সঠিক কিবলার দিক",
                            icon = Icons.Default.Explore,
                            onClick = { viewModel.navigateTo(AppScreen.QIBLA) },
                            modifier = Modifier.weight(1f)
                        )
                        IslamicFeatureCard(
                            title = "প্রিমিয়াম মেম্বারশিপ",
                            subtitle = "অফলাইন ও বিশেষ ফিচার",
                            icon = Icons.Default.Star,
                            onClick = { viewModel.navigateTo(AppScreen.PREMIUM) },
                            modifier = Modifier.weight(1f),
                            badgeText = if (currentUser?.isPremium == true) "সক্রিয়" else "নতুন"
                        )
                    }

                    // Row 5: Settings & Admin Panel
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        IslamicFeatureCard(
                            title = "অ্যাপ সেটিংস",
                            subtitle = "ফন্ট, ক্বারী ও থিম",
                            icon = Icons.Default.Settings,
                            onClick = { viewModel.navigateTo(AppScreen.SETTINGS) },
                            modifier = Modifier.weight(1f)
                        )
                        IslamicFeatureCard(
                            title = "এডমিন প্যানেল",
                            subtitle = "ওয়েব ও অ্যাপ ব্যবস্থাপনা",
                            icon = Icons.Default.AdminPanelSettings,
                            onClick = { viewModel.navigateTo(AppScreen.ADMIN_PANEL) },
                            modifier = Modifier.weight(1f),
                            badgeText = "ম্যানেজার"
                        )
                    }

                    // Row 6: Help & Guide
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        IslamicFeatureCard(
                            title = "নির্দেশিকা ও সাহায্য",
                            subtitle = "Termux ও ব্যবহারবিধি",
                            icon = Icons.Default.HelpOutline,
                            onClick = { viewModel.navigateTo(AppScreen.HELP_ABOUT) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}
