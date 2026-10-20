package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.QuranTopBar
import com.example.ui.theme.*
import com.example.viewmodel.AppScreen
import com.example.viewmodel.QuranViewModel

@Composable
fun HelpAboutScreen(
    viewModel: QuranViewModel,
    modifier: Modifier = Modifier
) {
    val isDarkMode by viewModel.isDarkMode.collectAsState()

    Scaffold(
        topBar = {
            QuranTopBar(
                title = "সহায়িকা ও পরিচিতি (Help & About)",
                subtitle = "কুরআনিফাই ও Termux লোকাল সার্ভার গাইড",
                showBackButton = true,
                onBackClick = { viewModel.navigateBack() },
                onSearchClick = { viewModel.navigateTo(AppScreen.SEARCH) },
                onProfileClick = { viewModel.navigateTo(AppScreen.PROFILE) },
                onThemeToggle = { viewModel.toggleTheme() },
                isDarkMode = isDarkMode
            )
        },
        modifier = modifier.testTag("help_about_screen")
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // App About Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = EmeraldSurfaceVariant),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = androidx.compose.ui.graphics.SolidColor(IslamicGold)
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "কুরআনিফাই ইসলামিক (Quranify Islamic)",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = IslamicGold
                        )
                    )
                    Text(
                        text = "ভার্সন ১.০.০ | ডেভেলপমেন্ট ও প্রকাশনা",
                        style = MaterialTheme.typography.labelSmall.copy(color = IslamicGoldLight)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "কুরআনিফাই ইসলামিক হলো একটি পরিপূর্ণ, পেশাদার ইসলামিক অ্যাপ্লিকেশন যা পবিত্র কুরআন তিলাওয়াত, বিশুদ্ধ বাংলা অনুবাদ (মুহিউদ্দীন খান), পাঁচ ওয়াক্ত নামাজের সঠিক সময়সূচী, কিবলা কম্পাস, বুকমার্ক, ইতিহাস ও ওয়েব ভিত্তিক এডমিন প্যানেল সমন্বিত করে তৈরি করা হয়েছে।",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = TextWarmWhite,
                            lineHeight = 22.sp
                        )
                    )
                }
            }

            // Offline vs Online Features Breakdown
            Text(
                text = "অফলাইন বনাম অনলাইন সেবাসমূহ:",
                style = MaterialTheme.typography.titleSmall.copy(
                    color = IslamicGold,
                    fontWeight = FontWeight.Bold
                )
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    FeatureStatusItem(
                        title = "১১৪টি সূরার আরবি পাঠ ও বাংলা অনুবাদ",
                        status = "১০০% সম্পূর্ণ অফলাইন",
                        isOffline = true
                    )
                    FeatureStatusItem(
                        title = "বুকমার্ক ও পঠন ইতিহাস সংরক্ষণ",
                        status = "অন-ডিভাইস লোকাল SQLite (অফলাইন)",
                        isOffline = true
                    )
                    FeatureStatusItem(
                        title = "নামাজের সময়সূচী ও কিবলা কম্পাস",
                        status = "অ্যাস্ট্রোনমিক্যাল সূত্র (অফলাইন)",
                        isOffline = true
                    )
                    FeatureStatusItem(
                        title = "ক্বারীগণের মধুর অডিও তেলাওয়াত",
                        status = "অনলাইন স্ট্রিমিং (EveryAyah CDN)",
                        isOffline = false
                    )
                    FeatureStatusItem(
                        title = "ম্যানুয়াল পেমেন্ট ট্রানজেকশন সাবমিশন",
                        status = "লোকাল সার্ভার বা ক্লাউড সংযোগ প্রয়োজন",
                        isOffline = false
                    )
                }
            }

            // Termux Setup Guide in Bengali
            Text(
                text = "অ্যান্ড্রয়েড ফোনে Termux + Flask লোকাল সার্ভার চালনা:",
                style = MaterialTheme.typography.titleSmall.copy(
                    color = IslamicGold,
                    fontWeight = FontWeight.Bold
                )
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Termux কমান্ড নির্দেশিকা (Step-by-step):",
                        style = MaterialTheme.typography.labelMedium.copy(color = IslamicGold, fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    TermuxCommandBlock(
                        title = "১. প্যাকেজ আপডেট এবং পাইথন ইনস্টলেশন:",
                        command = "pkg update && pkg upgrade -y\npkg install python git sqlite -y"
                    )

                    TermuxCommandBlock(
                        title = "২. ডিপেনডেন্সি ইনস্টল:",
                        command = "pip install flask werkzeug"
                    )

                    TermuxCommandBlock(
                        title = "৩. লোকাল ব্যাকএন্ড সার্ভার চালু:",
                        command = "cd backend\npython app.py"
                    )

                    TermuxCommandBlock(
                        title = "৪. ওয়েব এডমিন প্যানেল ব্রাউজারে চালু:",
                        command = "অ্যান্ড্রয়েড ব্রাউজারে খুলুন:\nhttp://127.0.0.1:5000/admin\nইউজারনেম: admin\nপাসওয়ার্ড: Admin@123"
                    )

                    TermuxCommandBlock(
                        title = "৫. ব্যাকগ্রাউন্ডে সার্ভার সচল রাখা (Wake Lock):",
                        command = "termux-wake-lock"
                    )
                }
            }

            // Attributions
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "স্বীকৃতি ও কৃতজ্ঞতা (Attributions):",
                        style = MaterialTheme.typography.labelMedium.copy(color = IslamicGold, fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "• বাংলা অনুবাদ: মাওলানা মুহিউদ্দীন খান (রহঃ)\n• আরবি লিপি: তানজিল ও কিং ফাহাদ কমপ্লেক্স উসমানী স্ক্রিপ্ট\n• অডিও তেলাওয়াত: EveryAyah ওপেন অডিও প্রজেক্ট\n• হিসাবনিকাশ: ইসলামিক ফাউন্ডেশন বাংলাদেশ ও করাচি ইউনিভার্সিটি কনভেনশন",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 20.sp
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun FeatureStatusItem(
    title: String,
    status: String,
    isOffline: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium))
            Text(status, style = MaterialTheme.typography.labelSmall.copy(color = if (isOffline) StatusSuccess else IslamicGold))
        }
        Icon(
            imageVector = if (isOffline) Icons.Default.CloudOff else Icons.Default.Wifi,
            contentDescription = null,
            tint = if (isOffline) StatusSuccess else IslamicGold,
            modifier = Modifier.size(18.dp)
        )
    }
}

@Composable
fun TermuxCommandBlock(
    title: String,
    command: String
) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(title, style = MaterialTheme.typography.labelSmall.copy(color = TextWarmWhite, fontWeight = FontWeight.SemiBold))
        Spacer(modifier = Modifier.height(2.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(EmeraldDeepBg, RoundedCornerShape(6.dp))
                .border(0.5.dp, IslamicGold.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                .padding(8.dp)
        ) {
            Text(
                text = command,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = IslamicGoldLight,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp
                )
            )
        }
    }
}
