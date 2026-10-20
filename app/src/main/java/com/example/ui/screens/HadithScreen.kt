package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.HadithItem
import com.example.data.VerifiedHadithData
import com.example.ui.components.PremiumCircularIconButton
import com.example.ui.components.PremiumIslamicBottomBar
import com.example.ui.components.QuranTopBar
import com.example.ui.theme.*
import com.example.viewmodel.AppScreen
import com.example.viewmodel.QuranViewModel
import java.util.Calendar

@Composable
fun HadithScreen(
    viewModel: QuranViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    val bookmarkedIds by viewModel.bookmarkedHadithIds.collectAsState()
    val textScale by viewModel.hadithTextScale.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("সকল") }
    var selectedBook by remember { mutableStateOf("সকল কিতাব") }
    var showOnlyBookmarked by remember { mutableStateOf(false) }

    val categories = listOf(
        "সকল",
        "ঈমান ও নিয়ত",
        "পবিত্রতা ও সালাত",
        "রোযা ও রমযান",
        "যাকাত ও সাদাকাহ",
        "হজ ও উমরাহ",
        "চরিত্র ও আখলাক",
        "পিতা-মাতা ও আত্মীয়তা",
        "কুরআন ও জ্ঞান",
        "যিকর ও দু'আ",
        "তাওবা ও ক্ষমা",
        "ধৈর্য ও তাওয়াক্কুল",
        "জান্নাত ও জাহান্নাম"
    )

    val books = listOf(
        "সকল কিতাব",
        "সহীহ বুখারী",
        "সহীহ মুসলিম",
        "সুনান আত-তিরমিযী",
        "সুনান আবু দাউদ",
        "সুনান আন-নাসায়ী",
        "সুনান ইবনে মাজাহ",
        "রিয়াযুস স্বা-লিহীন",
        "চল্লিশ হাদিস (ইমাম নববী)"
    )

    val filteredHadiths = remember(searchQuery, selectedCategory, selectedBook, showOnlyBookmarked, bookmarkedIds) {
        VerifiedHadithData.list.filter { item ->
            val matchesBookmark = !showOnlyBookmarked || bookmarkedIds.contains(item.id)
            val matchesSearch = searchQuery.isBlank() ||
                    item.bengali.contains(searchQuery.trim(), ignoreCase = true) ||
                    item.arabic.contains(searchQuery.trim(), ignoreCase = true) ||
                    item.narrator.contains(searchQuery.trim(), ignoreCase = true) ||
                    item.hadithNumber.contains(searchQuery.trim(), ignoreCase = true) ||
                    item.book.contains(searchQuery.trim(), ignoreCase = true) ||
                    item.chapter.contains(searchQuery.trim(), ignoreCase = true)

            val matchesCat = selectedCategory == "সকল" || item.category == selectedCategory
            val matchesBook = selectedBook == "সকল কিতাব" || item.book.contains(selectedBook)

            matchesBookmark && matchesSearch && matchesCat && matchesBook
        }
    }

    // Daily rotating Hadith based on Day of Year
    val dailyHadith = remember {
        val dayOfYear = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
        val index = (dayOfYear - 1) % VerifiedHadithData.list.size
        VerifiedHadithData.list[index]
    }

    Scaffold(
        topBar = {
            QuranTopBar(
                title = "সহীহ হাদিস সম্ভার (Hadith)",
                subtitle = "রাসূলুল্লাহ (সাঃ)-এর বিশুদ্ধ বাণী ও সুন্নাহ",
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
                    currentScreen = AppScreen.HADITH,
                    onNavigate = { screen ->
                        viewModel.navigateTo(screen)
                    }
                )
            }
        },
        modifier = modifier.testTag("hadith_screen")
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
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .testTag("hadith_search_input"),
                placeholder = { Text("হাদিসের অর্থ, রাবী, বিষয় বা নম্বর লিখুন...") },
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
                shape = RoundedCornerShape(12.dp)
            )

            // Primary Control Row: Total stats & Font size controls
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = EmeraldSurfaceVariant,
                    border = androidx.compose.foundation.BorderStroke(1.dp, IslamicGold.copy(alpha = 0.3f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.MenuBook,
                            contentDescription = null,
                            tint = IslamicGold,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "মোট ${VerifiedHadithData.list.size}টি হাদিস • প্রদর্শিত: ${filteredHadiths.size}টি",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = IslamicGoldLight,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        )
                    }
                }

                // Font size controls
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "হরফ:",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        modifier = Modifier.padding(start = 6.dp, end = 2.dp)
                    )
                    IconButton(
                        onClick = { viewModel.decreaseHadithTextScale() },
                        modifier = Modifier.size(26.dp)
                    ) {
                        Text("A-", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = IslamicGold)
                    }
                    IconButton(
                        onClick = { viewModel.increaseHadithTextScale() },
                        modifier = Modifier.size(26.dp)
                    ) {
                        Text("A+", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = IslamicGold)
                    }
                }
            }

            // Quick Bookmark Toggle Chip & Category Chips Row
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 3.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Bookmarked Filter Chip
                item {
                    FilterChip(
                        selected = showOnlyBookmarked,
                        onClick = { showOnlyBookmarked = !showOnlyBookmarked },
                        leadingIcon = {
                            Icon(
                                imageVector = if (showOnlyBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                contentDescription = null,
                                tint = if (showOnlyBookmarked) EmeraldDeepBg else IslamicGold,
                                modifier = Modifier.size(16.dp)
                            )
                        },
                        label = { Text("প্রিয় হাদিস (${bookmarkedIds.size})", fontSize = 12.sp, fontWeight = FontWeight.SemiBold) },
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = EmeraldSurface,
                            labelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            selectedContainerColor = IslamicGold,
                            selectedLabelColor = EmeraldDeepBg
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = showOnlyBookmarked,
                            borderColor = EmeraldBorder,
                            selectedBorderColor = IslamicGold
                        )
                    )
                }

                // Category Chips
                items(categories) { cat ->
                    val isCatSelected = !showOnlyBookmarked && selectedCategory == cat
                    FilterChip(
                        selected = isCatSelected,
                        onClick = {
                            showOnlyBookmarked = false
                            selectedCategory = cat
                        },
                        label = { Text(cat, fontSize = 12.sp, fontWeight = if (isCatSelected) FontWeight.Bold else FontWeight.Normal) },
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = EmeraldSurface,
                            labelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            selectedContainerColor = IslamicGold,
                            selectedLabelColor = EmeraldDeepBg
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isCatSelected,
                            borderColor = EmeraldBorder,
                            selectedBorderColor = IslamicGold
                        )
                    )
                }
            }

            // Book Filter Chips Row
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(books) { bk ->
                    val isBookSelected = selectedBook == bk
                    FilterChip(
                        selected = isBookSelected,
                        onClick = { selectedBook = bk },
                        label = { Text(bk, fontSize = 11.sp, fontWeight = if (isBookSelected) FontWeight.Bold else FontWeight.Normal) },
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = EmeraldSurface,
                            labelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            selectedContainerColor = IslamicGold.copy(alpha = 0.25f),
                            selectedLabelColor = IslamicGoldLight
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isBookSelected,
                            borderColor = EmeraldBorder,
                            selectedBorderColor = IslamicGold
                        )
                    )
                }
            }

            // Hadith List
            if (filteredHadiths.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(EmeraldSurfaceVariant)
                                .border(1.dp, EmeraldBorder, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (showOnlyBookmarked) Icons.Default.BookmarkBorder else Icons.Default.SearchOff,
                                contentDescription = "কোনো হাদিস মেলেনি",
                                tint = IslamicGold,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = if (showOnlyBookmarked) "কোনো সংরক্ষিত প্রিয় হাদিস নেই" else "'$searchQuery'-এর কোনো হাদিস খুঁজে পাওয়া যায়নি",
                            style = MaterialTheme.typography.titleSmall.copy(
                                color = MaterialTheme.colorScheme.onSurface,
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (showOnlyBookmarked) "যেকোনো হাদিসের বুকমার্ক আইকনে চাপ দিয়ে এখানে সংরক্ষণ করুন" else "অনুগ্রহ করে অন্য শব্দ দিয়ে খুঁজুন অথবা ফিল্টার রিসেট করুন",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {
                                searchQuery = ""
                                selectedCategory = "সকল"
                                selectedBook = "সকল কিতাব"
                                showOnlyBookmarked = false
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = IslamicGold,
                                contentColor = EmeraldDeepBg
                            ),
                            shape = RoundedCornerShape(22.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("সকল হাদিস দেখুন (রিসেট)", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Daily Hadith Highlight Card (if no search or custom filter is active)
                    if (searchQuery.isBlank() && selectedCategory == "সকল" && selectedBook == "সকল কিতাব" && !showOnlyBookmarked) {
                        item {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("daily_hadith_banner"),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = EmeraldSurfaceVariant),
                                border = CardDefaults.outlinedCardBorder().copy(
                                    brush = androidx.compose.ui.graphics.SolidColor(IslamicGold)
                                )
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Default.AutoStories,
                                                contentDescription = null,
                                                tint = IslamicGold,
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "আজকের নির্বাচিত হাদিস (Daily Hadith)",
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    color = IslamicGold,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            )
                                        }

                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = IslamicGold.copy(alpha = 0.2f)
                                        ) {
                                            Text(
                                                text = dailyHadith.book,
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    color = IslamicGoldLight,
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold
                                                ),
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    Text(
                                        text = dailyHadith.arabic,
                                        style = MaterialTheme.typography.bodyLarge.copy(
                                            fontSize = (20 * textScale).sp,
                                            lineHeight = (32 * textScale).sp,
                                            textAlign = TextAlign.End,
                                            color = MaterialTheme.colorScheme.onSurface
                                        ),
                                        modifier = Modifier.fillMaxWidth()
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text(
                                        text = dailyHadith.bengali,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            fontSize = (15 * textScale).sp,
                                            lineHeight = (22 * textScale).sp
                                        )
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text(
                                        text = "— ${dailyHadith.narrator} | ${dailyHadith.hadithNumber} (${dailyHadith.grade})",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = IslamicGold,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    )
                                }
                            }
                        }
                    }

                    // Hadith Items
                    items(filteredHadiths, key = { it.id }) { hadith ->
                        val isBookmarked = bookmarkedIds.contains(hadith.id)
                        HadithCard(
                            item = hadith,
                            isBookmarked = isBookmarked,
                            textScale = textScale,
                            onToggleBookmark = {
                                viewModel.toggleHadithBookmark(hadith.id)
                            },
                            onCopy = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText(
                                    "Hadith",
                                    "${hadith.arabic}\n\n${hadith.bengali}\n— ${hadith.narrator} (${hadith.book}, ${hadith.hadithNumber})\n[কুরআনিফাই ইসলামিক অ্যাপ]"
                                )
                                clipboard.setPrimaryClip(clip)
                                viewModel.showMessage("হাদিসটি ক্লিপবোর্ডে কপি করা হয়েছে")
                            },
                            onShare = {
                                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(
                                        Intent.EXTRA_TEXT,
                                        "${hadith.arabic}\n\n${hadith.bengali}\n\n— বর্ণনাকারী: ${hadith.narrator}\nগ্রন্থ: ${hadith.book} (${hadith.hadithNumber})\nমান: ${hadith.grade}\n\n[কুরআনিফাই ইসলামিক অ্যাপ]"
                                    )
                                }
                                context.startActivity(Intent.createChooser(shareIntent, "হাদিস শেয়ার করুন"))
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun HadithCard(
    item: HadithItem,
    isBookmarked: Boolean,
    textScale: Float,
    onToggleBookmark: () -> Unit,
    onCopy: () -> Unit,
    onShare: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("hadith_card_${item.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
        )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header with Book & Grade Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(EmeraldSurfaceVariant)
                            .border(1.dp, IslamicGold.copy(alpha = 0.5f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${item.id}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = IslamicGold
                            )
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = "${item.book} • ${item.hadithNumber}",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = IslamicGold
                            )
                        )
                        Text(
                            text = "${item.chapter} | ${item.category}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 11.sp
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = StatusSuccess.copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(0.5.dp, StatusSuccess)
                ) {
                    Text(
                        text = item.grade,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = StatusSuccess,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Arabic text (Right-to-Left)
            Text(
                text = item.arabic,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = (20 * textScale).sp,
                    lineHeight = (32 * textScale).sp,
                    textAlign = TextAlign.End,
                    color = MaterialTheme.colorScheme.onSurface
                ),
                modifier = Modifier.fillMaxWidth()
            )

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 10.dp),
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
            )

            // Bengali text
            Text(
                text = item.bengali,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = (15 * textScale).sp,
                    lineHeight = (22 * textScale).sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Narrator & Action Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "বর্ণনাকারী: ${item.narrator}",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = IslamicGoldLight,
                        fontWeight = FontWeight.Medium
                    ),
                    modifier = Modifier.weight(1f)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Bookmark Button
                    PremiumCircularIconButton(
                        onClick = onToggleBookmark,
                        icon = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        contentDescription = if (isBookmarked) "সংরক্ষিত" else "বুকমার্ক করুন",
                        size = 34.dp,
                        iconSize = 16.dp,
                        containerColor = ButtonDarkBg,
                        borderColor = if (isBookmarked) IslamicGold else ButtonDarkBorder,
                        tint = if (isBookmarked) IslamicGold else ButtonIconWhite,
                        testTag = "bookmark_hadith_${item.id}"
                    )

                    // Copy Button
                    PremiumCircularIconButton(
                        onClick = onCopy,
                        icon = Icons.Default.ContentCopy,
                        contentDescription = "কপি করুন",
                        size = 34.dp,
                        iconSize = 16.dp,
                        containerColor = ButtonDarkBg,
                        borderColor = ButtonDarkBorder,
                        tint = ButtonIconWhite,
                        testTag = "copy_hadith_${item.id}"
                    )

                    // Share Button
                    PremiumCircularIconButton(
                        onClick = onShare,
                        icon = Icons.Default.Share,
                        contentDescription = "শেয়ার করুন",
                        size = 34.dp,
                        iconSize = 16.dp,
                        containerColor = ButtonDarkBg,
                        borderColor = ButtonDarkBorder,
                        tint = ButtonIconWhite,
                        testTag = "share_hadith_${item.id}"
                    )
                }
            }
        }
    }
}
