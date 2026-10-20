package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.PrayerTimeCalculator
import com.example.data.PrayerTimesResult
import com.example.ui.components.QuranTopBar
import com.example.ui.theme.*
import com.example.viewmodel.AppScreen
import com.example.viewmodel.QuranViewModel

@Composable
fun PrayerTimesScreen(
    viewModel: QuranViewModel,
    modifier: Modifier = Modifier
) {
    val prayerTimes by viewModel.prayerTimes.collectAsState()
    val isDarkMode by viewModel.isDarkMode.collectAsState()

    var selectedCity by remember { mutableStateOf("ঢাকা (Dhaka)") }

    val cities = listOf(
        "ঢাকা (Dhaka)" to Pair(23.8103, 90.4125),
        "চট্টগ্রাম (Chittagong)" to Pair(22.3569, 91.7832),
        "সিলেট (Sylhet)" to Pair(24.8949, 91.8687),
        "রাজশাহী (Rajshahi)" to Pair(24.3636, 88.6241),
        "খুলনা (Khulna)" to Pair(22.8456, 89.5403),
        "বরিশাল (Barishal)" to Pair(22.7010, 90.3535),
        "মক্কা মুকাররমা (Mecca)" to Pair(21.4225, 39.8262)
    )

    var currentCalc by remember(selectedCity) {
        val coords = cities.find { it.first == selectedCity }?.second ?: Pair(23.8103, 90.4125)
        val tz = if (selectedCity.contains("Mecca")) 3.0 else 6.0
        mutableStateOf(PrayerTimeCalculator.calculate(coords.first, coords.second, tz, selectedCity))
    }

    Scaffold(
        topBar = {
            QuranTopBar(
                title = "নামাজের সময়সূচী",
                subtitle = currentCalc.locationName,
                showBackButton = true,
                onBackClick = { viewModel.navigateBack() },
                onSearchClick = { viewModel.navigateTo(AppScreen.SEARCH) },
                onProfileClick = { viewModel.navigateTo(AppScreen.PROFILE) },
                onThemeToggle = { viewModel.toggleTheme() },
                isDarkMode = isDarkMode
            )
        },
        modifier = modifier.testTag("prayer_times_screen")
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // City Selector Dropdown Row
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = androidx.compose.ui.graphics.SolidColor(MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                    )
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "অবস্থান বা শহর নির্বাচন করুন:",
                            style = MaterialTheme.typography.labelSmall.copy(color = IslamicGold)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        ScrollableTabRow(
                            selectedTabIndex = cities.indexOfFirst { it.first == selectedCity }.coerceAtLeast(0),
                            edgePadding = 0.dp,
                            containerColor = MaterialTheme.colorScheme.surface,
                            contentColor = IslamicGold
                        ) {
                            cities.forEach { city ->
                                Tab(
                                    selected = selectedCity == city.first,
                                    onClick = { selectedCity = city.first },
                                    text = { Text(city.first.split(" ")[0], fontSize = 12.sp) }
                                )
                            }
                        }
                    }
                }
            }

            // Highlight Next Prayer Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("next_prayer_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = EmeraldSurfaceVariant),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = androidx.compose.ui.graphics.SolidColor(IslamicGold)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "পরবর্তী নামাজ (Next Prayer)",
                            style = MaterialTheme.typography.bodySmall.copy(color = IslamicGoldLight)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = currentCalc.nextPrayerName,
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextWarmWhite
                            )
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = currentCalc.nextPrayerTime,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = IslamicGold
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "তারিখ: ${currentCalc.dateStr} | ইসলামিক ফাউন্ডেশন ভিত্তিক হিসাব",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = TextMutedGreen,
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }

            // 5 Daily Prayers List
            item {
                Text(
                    text = "দৈনিক পাঁচ ওয়াক্ত ও সূর্যোদয়ের সময়:",
                    style = MaterialTheme.typography.titleSmall.copy(
                        color = IslamicGold,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }

            item { PrayerItemRow("ফজর (Fajr)", currentCalc.fajr, Icons.Default.WbTwilight) }
            item { PrayerItemRow("সূর্যোদয় (Sunrise)", currentCalc.sunrise, Icons.Default.WbSunny) }
            item { PrayerItemRow("যোহর (Dhuhr)", currentCalc.dhuhr, Icons.Default.LightMode) }
            item { PrayerItemRow("আসর (Asr)", currentCalc.asr, Icons.Default.WbCloudy) }
            item { PrayerItemRow("মাগরিব (Maghrib)", currentCalc.maghrib, Icons.Default.NightsStay) }
            item { PrayerItemRow("ইশা (Isha)", currentCalc.isha, Icons.Default.Bedtime) }

            // Hadith on Prayer reminder
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Info, contentDescription = "Hadith", tint = IslamicGold, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "হাদিস বাণী",
                                style = MaterialTheme.typography.labelMedium.copy(color = IslamicGold, fontWeight = FontWeight.Bold)
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "\"রাসূলুল্লাহ (সাঃ) বলেছেন: বান্দা ও কুফরের মাঝে পার্থক্য হলো নামাজ পরিত্যাগ করা।\" (সহীহ মুসলিম)",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                lineHeight = 18.sp
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PrayerItemRow(
    name: String,
    time: String,
    icon: ImageVector
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(EmeraldSurfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = icon, contentDescription = name, tint = IslamicGold, modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.width(14.dp))
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }

            Text(
                text = time,
                style = MaterialTheme.typography.titleMedium.copy(
                    color = IslamicGold,
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}
