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
import com.example.data.HistoryEntity
import com.example.ui.components.QuranTopBar
import com.example.ui.theme.*
import com.example.viewmodel.AppScreen
import com.example.viewmodel.QuranViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ReadingHistoryScreen(
    viewModel: QuranViewModel,
    modifier: Modifier = Modifier
) {
    val history by viewModel.userHistory.collectAsState()
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    val allSurahs by viewModel.allSurahs.collectAsState()

    var showClearConfirmDialog by remember { mutableStateOf(false) }

    fun formatDate(timestamp: Long): String {
        return SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()).format(Date(timestamp))
    }

    if (showClearConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showClearConfirmDialog = false },
            title = { Text("ইতিহাস পরিষ্কার নিশ্চিতকরণ") },
            text = { Text("আপনি কি নিশ্চিতভাবে সমস্ত পড়ার ইতিহাস মুছে ফেলতে চান? এটি আর ফিরিয়ে আনা যাবে না।") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.clearAllHistory()
                        showClearConfirmDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = StatusError)
                ) {
                    Text("মুছে ফেলুন")
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearConfirmDialog = false }) {
                    Text("বাতিল")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            QuranTopBar(
                title = "পড়ার ইতিহাস (History)",
                subtitle = "${history.size} টি সাম্প্রতিক কার্যক্রম",
                showBackButton = true,
                onBackClick = { viewModel.navigateBack() },
                onSearchClick = { viewModel.navigateTo(AppScreen.SEARCH) },
                onProfileClick = { viewModel.navigateTo(AppScreen.PROFILE) },
                onThemeToggle = { viewModel.toggleTheme() },
                isDarkMode = isDarkMode
            )
        },
        modifier = modifier.testTag("reading_history_screen")
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Header with Clear All Button
            if (history.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "সম্প্রতি পঠিত আয়াতসমূহ:",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                    TextButton(
                        onClick = { showClearConfirmDialog = true },
                        modifier = Modifier.testTag("clear_history_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteSweep,
                            contentDescription = "সকল ইতিহাস মুছুন",
                            tint = StatusError,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("সকল মুছুন", color = StatusError, fontSize = 12.sp)
                    }
                }
            }

            if (history.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = "ইতিহাস খালি",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(54.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "কোনো পড়ার ইতিহাস পাওয়া যায়নি",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "আপনি যখন কুরআন তিলাওয়াত করবেন, তখন আপনার পঠিত স্থানসমূহ এখানে স্বয়ংক্রিয়ভাবে সংরক্ষিত হবে।",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            ),
                            modifier = Modifier.padding(horizontal = 24.dp)
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
                            Text("তিলাওয়াত শুরু করুন", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(history, key = { it.id }) { item ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    val surah = allSurahs.find { it.number == item.surahNumber }
                                    if (surah != null) {
                                        viewModel.openSurah(surah)
                                    }
                                }
                                .testTag("history_item_${item.id}"),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = CardDefaults.outlinedCardBorder().copy(
                                brush = androidx.compose.ui.graphics.SolidColor(MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = item.surahName,
                                            style = MaterialTheme.typography.titleSmall.copy(
                                                color = IslamicGold,
                                                fontWeight = FontWeight.Bold
                                            )
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "আয়াত ${item.verseNumber}",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(4.dp))

                                    Text(
                                        text = item.textBengali,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        ),
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )

                                    Spacer(modifier = Modifier.height(4.dp))

                                    Text(
                                        text = formatDate(item.readAt),
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                            fontSize = 10.sp
                                        )
                                    )
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Button(
                                        onClick = {
                                            val surah = allSurahs.find { it.number == item.surahNumber }
                                            if (surah != null) {
                                                viewModel.openSurah(surah)
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = IslamicGold,
                                            contentColor = EmeraldDeepBg
                                        ),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text("পড়ুন", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }

                                    IconButton(
                                        onClick = { viewModel.deleteHistoryItem(item) },
                                        modifier = Modifier.size(32.dp).testTag("delete_history_${item.id}")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "মুছুন",
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(16.dp)
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
}
