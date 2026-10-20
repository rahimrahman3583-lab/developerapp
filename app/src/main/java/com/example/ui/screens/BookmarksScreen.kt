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
import com.example.data.BookmarkEntity
import com.example.ui.components.QuranTopBar
import com.example.ui.theme.*
import com.example.viewmodel.AppScreen
import com.example.viewmodel.QuranViewModel

@Composable
fun BookmarksScreen(
    viewModel: QuranViewModel,
    modifier: Modifier = Modifier
) {
    val bookmarks by viewModel.userBookmarks.collectAsState()
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    val allSurahs by viewModel.allSurahs.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var sortBySurah by remember { mutableStateOf(false) }

    val filteredBookmarks = remember(bookmarks, searchQuery, sortBySurah) {
        var list = bookmarks.filter {
            searchQuery.isBlank() ||
                    it.surahName.contains(searchQuery.trim(), ignoreCase = true) ||
                    it.textBengali.contains(searchQuery.trim(), ignoreCase = true) ||
                    it.textArabic.contains(searchQuery.trim(), ignoreCase = true)
        }
        if (sortBySurah) {
            list = list.sortedWith(compareBy({ it.surahNumber }, { it.verseNumber }))
        }
        list
    }

    Scaffold(
        topBar = {
            QuranTopBar(
                title = "সংরক্ষিত আয়াত (Bookmarks)",
                subtitle = "${bookmarks.size} টি আয়াত সংরক্ষিত",
                showBackButton = true,
                onBackClick = { viewModel.navigateBack() },
                onSearchClick = { viewModel.navigateTo(AppScreen.SEARCH) },
                onProfileClick = { viewModel.navigateTo(AppScreen.PROFILE) },
                onThemeToggle = { viewModel.toggleTheme() },
                isDarkMode = isDarkMode
            )
        },
        modifier = modifier.testTag("bookmarks_screen")
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Search & Sort Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("bookmark_search_input"),
                    placeholder = { Text("সংরক্ষিত আয়াতে খুঁজুন...") },
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

                IconButton(
                    onClick = { sortBySurah = !sortBySurah },
                    modifier = Modifier.testTag("bookmark_sort_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Sort,
                        contentDescription = if (sortBySurah) "সময় অনুযায়ী" else "সূরা অনুযায়ী",
                        tint = if (sortBySurah) IslamicGold else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (filteredBookmarks.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .background(EmeraldSurfaceVariant)
                                .border(1.dp, IslamicGold.copy(alpha = 0.5f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.BookmarkBorder,
                                contentDescription = "বুকমার্ক খালি",
                                tint = IslamicGold,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = if (searchQuery.isNotBlank()) "কোনো মিল পাওয়া যায়নি" else "কোনো আয়াত এখনো বুকমার্ক করা হয়নি",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "যেকোনো সূরা পড়ার সময় আয়াতের পাশের বুকমার্ক আইকনটিতে ট্যাপ করে আয়াতটি এখানে সংরক্ষণ করুন।",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            ),
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Button(
                            onClick = { viewModel.navigateTo(AppScreen.SURAH_LIST) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = IslamicGold,
                                contentColor = EmeraldDeepBg
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("সূরা তালিকা দেখুন", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredBookmarks, key = { it.id }) { bookmark ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    val surah = allSurahs.find { it.number == bookmark.surahNumber }
                                    if (surah != null) {
                                        viewModel.openSurah(surah)
                                    }
                                }
                                .testTag("bookmark_item_${bookmark.id}"),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = CardDefaults.outlinedCardBorder().copy(
                                brush = androidx.compose.ui.graphics.SolidColor(MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                            )
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "${bookmark.surahName} (${bookmark.surahNumber}:${bookmark.verseNumber})",
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            color = IslamicGold,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )

                                    IconButton(
                                        onClick = { viewModel.removeBookmark(bookmark) },
                                        modifier = Modifier.size(32.dp).testTag("delete_bookmark_${bookmark.id}")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.DeleteOutline,
                                            contentDescription = "বুকমার্ক মুছুন",
                                            tint = StatusError
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = bookmark.textArabic,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontSize = 18.sp,
                                        textAlign = TextAlign.End,
                                        color = MaterialTheme.colorScheme.onSurface
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = bookmark.textBengali,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    ),
                                    maxLines = 3,
                                    overflow = TextOverflow.Ellipsis
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = "পড়ুন →",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = IslamicGold,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
