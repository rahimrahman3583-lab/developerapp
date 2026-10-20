package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.SurahEntity
import com.example.ui.components.PremiumIslamicBottomBar
import com.example.ui.components.QuranTopBar
import com.example.ui.theme.*
import com.example.viewmodel.AppScreen
import com.example.viewmodel.QuranViewModel

@Composable
fun SurahListScreen(
    viewModel: QuranViewModel,
    modifier: Modifier = Modifier
) {
    val allSurahs by viewModel.allSurahs.collectAsState()
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    val history by viewModel.userHistory.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var filterType by remember { mutableStateOf("All") } // "All", "Makki", "Madani"
    var sortOrder by remember { mutableStateOf("Number") } // "Number", "Name", "Verses"

    val filteredSurahs = remember(allSurahs, searchQuery, filterType, sortOrder) {
        var list = allSurahs.filter { surah ->
            val matchesQuery = searchQuery.isBlank() ||
                    surah.nameBengali.contains(searchQuery.trim(), ignoreCase = true) ||
                    surah.nameArabic.contains(searchQuery.trim(), ignoreCase = true) ||
                    surah.meaningBengali.contains(searchQuery.trim(), ignoreCase = true) ||
                    surah.number.toString() == searchQuery.trim()

            val matchesFilter = when (filterType) {
                "Makki" -> surah.revelationType == "Makki"
                "Madani" -> surah.revelationType == "Madani"
                else -> true
            }
            matchesQuery && matchesFilter
        }

        list = when (sortOrder) {
            "Verses" -> list.sortedByDescending { it.verseCount }
            "Name" -> list.sortedBy { it.nameBengali }
            else -> list.sortedBy { it.number }
        }
        list
    }

    val lastRead = history.firstOrNull()

    Scaffold(
        topBar = {
            QuranTopBar(
                title = "পবিত্র কুরআন (১১৪ সূরা)",
                subtitle = "মাক্কী ও মাদানী সূরাসমূহ",
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
                    currentScreen = AppScreen.SURAH_LIST,
                    onNavigate = { screen ->
                        viewModel.navigateTo(screen)
                    }
                )
            }
        },
        modifier = modifier.testTag("surah_list_screen")
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Search Input Field
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .testTag("surah_search_input"),
                placeholder = { Text("সূরার নাম বা নম্বর লিখে খুঁজুন...") },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Search, contentDescription = "Search", tint = IslamicGold)
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = IslamicGold,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                )
            )

            // Filter Chips & Sort Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Filter Chips
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = filterType == "All",
                        onClick = { filterType = "All" },
                        label = { Text("সকল") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = IslamicGold,
                            selectedLabelColor = EmeraldDeepBg
                        )
                    )
                    FilterChip(
                        selected = filterType == "Makki",
                        onClick = { filterType = "Makki" },
                        label = { Text("মাক্কী") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = IslamicGold,
                            selectedLabelColor = EmeraldDeepBg
                        )
                    )
                    FilterChip(
                        selected = filterType == "Madani",
                        onClick = { filterType = "Madani" },
                        label = { Text("মাদানী") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = IslamicGold,
                            selectedLabelColor = EmeraldDeepBg
                        )
                    )
                }

                // Sort Dropdown Button
                IconButton(
                    onClick = {
                        sortOrder = when (sortOrder) {
                            "Number" -> "Verses"
                            "Verses" -> "Name"
                            else -> "Number"
                        }
                    },
                    modifier = Modifier.testTag("surah_sort_toggle")
                ) {
                    Icon(
                        imageVector = Icons.Default.Sort,
                        contentDescription = "সাজান ($sortOrder)",
                        tint = IslamicGold
                    )
                }
            }

            // Last Read Banner (if available)
            if (lastRead != null && searchQuery.isBlank()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                        .clickable {
                            val surah = allSurahs.find { it.number == lastRead.surahNumber }
                            if (surah != null) {
                                viewModel.openSurah(surah)
                            }
                        }
                        .testTag("last_read_banner"),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = EmeraldSurfaceVariant),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = androidx.compose.ui.graphics.SolidColor(IslamicGold.copy(alpha = 0.5f))
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.BookmarkBorder,
                                contentDescription = "সর্বশেষ পঠিত",
                                tint = IslamicGold,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "সর্বশেষ পঠিত (Last Read)",
                                    style = MaterialTheme.typography.labelSmall.copy(color = IslamicGoldLight)
                                )
                                Text(
                                    text = "${lastRead.surahName} (আয়াত ${lastRead.verseNumber})",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        color = TextWarmWhite,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }

                        Text(
                            text = "পড়ুন →",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = IslamicGold,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }

            // Surah Count Indicator
            Text(
                text = "${filteredSurahs.size} টি সূরা প্রদর্শিত (সাজানো: ${if (sortOrder == "Verses") "আয়াত সংখ্যা" else if (sortOrder == "Name") "নাম" else "ক্রমিক"})",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 12.sp
                ),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )

            // Surah List
            if (filteredSurahs.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.SearchOff,
                            contentDescription = "কোনো সূরা পাওয়া যায়নি",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "'$searchQuery' দিয়ে কোনো সূরা খুঁজে পাওয়া যায়নি",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        TextButton(onClick = { searchQuery = ""; filterType = "All" }) {
                            Text("ফিল্টার রিসেট করুন", color = IslamicGold)
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredSurahs, key = { it.number }) { surah ->
                        SurahListItemCard(
                            surah = surah,
                            onClick = { viewModel.openSurah(surah) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SurahListItemCard(
    surah: SurahEntity,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("surah_card_${surah.number}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Surah Number Diamond Badge & Info
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(EmeraldSurfaceVariant)
                        .border(1.dp, IslamicGold.copy(alpha = 0.6f), RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${surah.number}",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = IslamicGold,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Text(
                        text = surah.nameBengali,
                        style = MaterialTheme.typography.titleMedium.copy(
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
            }

            // Arabic Name & Classification Badge
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = surah.nameArabic,
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = IslamicGold,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                )
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (surah.revelationType == "Makki") EmeraldSurfaceVariant else IslamicGold.copy(alpha = 0.2f),
                    border = androidx.compose.foundation.BorderStroke(
                        0.5.dp,
                        if (surah.revelationType == "Makki") EmeraldBorder else IslamicGold
                    )
                ) {
                    Text(
                        text = if (surah.revelationType == "Makki") "মাক্কী" else "মাদানী",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = if (surah.revelationType == "Makki") TextMutedGreen else IslamicGold,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold
                        ),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}
