package com.example.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.VerseEntity
import com.example.ui.components.QuranTopBar
import com.example.ui.theme.*
import com.example.viewmodel.AppScreen
import com.example.viewmodel.QuranViewModel

@Composable
fun SearchScreen(
    viewModel: QuranViewModel,
    modifier: Modifier = Modifier
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()
    val allSurahs by viewModel.allSurahs.collectAsState()
    val isDarkMode by viewModel.isDarkMode.collectAsState()

    var activeFilter by remember { mutableStateOf("All") } // "All", "Surah", "Verse"

    val matchedSurahs = remember(searchQuery, allSurahs) {
        if (searchQuery.isBlank()) emptyList()
        else allSurahs.filter {
            it.nameBengali.contains(searchQuery.trim(), ignoreCase = true) ||
            it.nameArabic.contains(searchQuery.trim(), ignoreCase = true) ||
            it.meaningBengali.contains(searchQuery.trim(), ignoreCase = true) ||
            it.number.toString() == searchQuery.trim()
        }
    }

    Scaffold(
        topBar = {
            QuranTopBar(
                title = "অনুসন্ধান (Search)",
                subtitle = "কুরআনের সূরা ও অনুবাদে খুঁজুন",
                showBackButton = true,
                onBackClick = { viewModel.navigateBack() },
                onSearchClick = {},
                onProfileClick = { viewModel.navigateTo(AppScreen.PROFILE) },
                onThemeToggle = { viewModel.toggleTheme() },
                isDarkMode = isDarkMode
            )
        },
        modifier = modifier.testTag("search_screen")
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Search Input Field
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.updateSearchQuery(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .testTag("main_search_text_field"),
                placeholder = { Text("আরবি, বাংলা অনুবাদ বা সূরার নাম...") },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Search, contentDescription = "Search", tint = IslamicGold)
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.clearSearch() }) {
                            Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            // Filter Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = activeFilter == "All",
                    onClick = { activeFilter = "All" },
                    label = { Text("সব মিলিয়ে") },
                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = IslamicGold, selectedLabelColor = EmeraldDeepBg)
                )
                FilterChip(
                    selected = activeFilter == "Surah",
                    onClick = { activeFilter = "Surah" },
                    label = { Text("সূরাসমূহ (${matchedSurahs.size})") },
                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = IslamicGold, selectedLabelColor = EmeraldDeepBg)
                )
                FilterChip(
                    selected = activeFilter == "Verse",
                    onClick = { activeFilter = "Verse" },
                    label = { Text("আয়াতসমূহ (${searchResults.size})") },
                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = IslamicGold, selectedLabelColor = EmeraldDeepBg)
                )
            }

            // Results List
            if (searchQuery.isBlank()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search placeholder",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(54.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "পবিত্র কুরআনে যেকোনো বিষয় অনুসন্ধান করুন",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            ),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "উদাহরণ: 'ফাতিহা', 'রহমান', 'নামাজ', 'রোজা', 'দান', 'ধৈর্য', অথবা সূরার নম্বর (যেমন '৩৬')",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                        )
                    }
                }
            } else if (matchedSurahs.isEmpty() && searchResults.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.SearchOff,
                            contentDescription = "No results",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "'$searchQuery'-এর জন্য কোনো ফলাফল খুঁজে পাওয়া যায়নি",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Matched Surahs Section
                    if ((activeFilter == "All" || activeFilter == "Surah") && matchedSurahs.isNotEmpty()) {
                        item {
                            Text(
                                text = "মিলিত সূরাসমূহ (${matchedSurahs.size}):",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    color = IslamicGold,
                                    fontWeight = FontWeight.Bold
                                ),
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                        }

                        items(matchedSurahs) { surah ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { viewModel.openSurah(surah) }
                                    .testTag("search_surah_${surah.number}"),
                                shape = RoundedCornerShape(10.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                border = CardDefaults.outlinedCardBorder().copy(
                                    brush = androidx.compose.ui.graphics.SolidColor(IslamicGold.copy(alpha = 0.3f))
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = "${surah.number}. সূরা ${surah.nameBengali}",
                                            style = MaterialTheme.typography.titleSmall.copy(
                                                color = MaterialTheme.colorScheme.onSurface,
                                                fontWeight = FontWeight.Bold
                                            )
                                        )
                                        Text(
                                            text = "${surah.meaningBengali} • ${surah.verseCount} আয়াত",
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        )
                                    }
                                    Text(
                                        text = surah.nameArabic,
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            color = IslamicGold,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }
                            }
                        }
                    }

                    // Matched Verses Section
                    if ((activeFilter == "All" || activeFilter == "Verse") && searchResults.isNotEmpty()) {
                        item {
                            Text(
                                text = "মিলিত আয়াতসমূহ (${searchResults.size}):",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    color = IslamicGold,
                                    fontWeight = FontWeight.Bold
                                ),
                                modifier = Modifier.padding(top = 10.dp, bottom = 4.dp)
                            )
                        }

                        items(searchResults) { verse ->
                            val surahName = allSurahs.find { it.number == verse.surahNumber }?.nameBengali ?: "সূরা"
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        allSurahs.find { it.number == verse.surahNumber }?.let {
                                            viewModel.openSurah(it)
                                        }
                                    }
                                    .testTag("search_verse_${verse.surahNumber}_${verse.verseNumber}"),
                                shape = RoundedCornerShape(10.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                border = CardDefaults.outlinedCardBorder().copy(
                                    brush = androidx.compose.ui.graphics.SolidColor(MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                                )
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "সূরা $surahName (${verse.surahNumber}:${verse.verseNumber})",
                                            style = MaterialTheme.typography.titleSmall.copy(
                                                color = IslamicGold,
                                                fontWeight = FontWeight.Bold
                                            )
                                        )
                                        Text(
                                            text = "পড়ুন →",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = IslamicGold
                                            )
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(4.dp))

                                    Text(
                                        text = verse.textArabic,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontSize = 17.sp,
                                            textAlign = TextAlign.End,
                                            color = MaterialTheme.colorScheme.onSurface
                                        ),
                                        modifier = Modifier.fillMaxWidth()
                                    )

                                    Spacer(modifier = Modifier.height(4.dp))

                                    Text(
                                        text = verse.textBengali,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        ),
                                        maxLines = 3,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
