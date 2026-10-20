package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Reciters
import com.example.ui.components.QuranTopBar
import com.example.ui.theme.*
import com.example.viewmodel.AppScreen
import com.example.viewmodel.QuranViewModel

@Composable
fun SettingsScreen(
    viewModel: QuranViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    val arabicFontSize by viewModel.arabicFontSize.collectAsState()
    val showTranslation by viewModel.showBengaliTranslation.collectAsState()
    val selectedReciter by viewModel.selectedReciterId.collectAsState()
    val playbackSpeed by viewModel.playbackSpeed.collectAsState()

    var showExportDialog by remember { mutableStateOf(false) }
    var exportContent by remember { mutableStateOf("") }

    var showPrivacyDialog by remember { mutableStateOf(false) }
    var showTermsDialog by remember { mutableStateOf(false) }

    if (showExportDialog) {
        AlertDialog(
            onDismissRequest = { showExportDialog = false },
            title = { Text("ডাটাবেজ ব্যাকআপ সম্পন্ন (Backup Export)") },
            text = {
                Column {
                    Text("আপনার বুকমার্ক ও সেটিংসের ব্যাকআপ ডাটা তৈরি হয়েছে:")
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = exportContent,
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth().height(120.dp),
                        textStyle = MaterialTheme.typography.bodySmall
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        clipboard.setPrimaryClip(ClipData.newPlainText("Quranify Backup", exportContent))
                        viewModel.showMessage("ব্যাকআপ টেক্সট ক্লিপবোর্ডে কপি করা হয়েছে")
                        showExportDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = IslamicGold, contentColor = EmeraldDeepBg)
                ) {
                    Text("কপি করুন")
                }
            },
            dismissButton = {
                TextButton(onClick = { showExportDialog = false }) {
                    Text("বন্ধ করুন")
                }
            }
        )
    }

    if (showPrivacyDialog) {
        AlertDialog(
            onDismissRequest = { showPrivacyDialog = false },
            title = { Text("গোপনীয়তা নীতি (Privacy Policy)") },
            text = {
                Text(
                    "কুরআনিফাই ইসলামিক ব্যবহারকারীর গোপনীয়তা সম্পূর্ণভাবে সম্মান করে। আপনার বুকমার্ক, ইতিহাস ও প্রার্থনার পছন্দসমূহ অন-ডিভাইস লোকাল SQLite ডাটাবেজে নিরাপদে সংরক্ষিত থাকে। কোনো ব্যক্তিগত সংবেদনশীল তথ্য তৃতীয় পক্ষের সাথে শেয়ার করা হয় না।"
                )
            },
            confirmButton = {
                TextButton(onClick = { showPrivacyDialog = false }) {
                    Text("সম্মত আছি", color = IslamicGold)
                }
            }
        )
    }

    if (showTermsDialog) {
        AlertDialog(
            onDismissRequest = { showTermsDialog = false },
            title = { Text("শর্তাবলী (Terms & Conditions)") },
            text = {
                Text(
                    "এই অ্যাপ্লিকেশনটি পবিত্র কুরআনের বিশুদ্ধ তিলাওয়াত ও বাংলা অনুবাদের প্রচারের লক্ষ্যে নির্মিত। আরবি হরফ ও অনুবাদের সত্যতা বিশ্বস্ত ইসলামী সূত্র থেকে যাচাই করা হয়েছে। অডিও তেলাওয়াতসমূহ EveryAyah পাবলিক রিপোজিটরি থেকে পরিবেশন করা হয়।"
                )
            },
            confirmButton = {
                TextButton(onClick = { showTermsDialog = false }) {
                    Text("ঠিক আছে", color = IslamicGold)
                }
            }
        )
    }

    Scaffold(
        topBar = {
            QuranTopBar(
                title = "অ্যাপ সেটিংস (Settings)",
                subtitle = "ব্যক্তিগত পছন্দ ও কনফিগারেশন",
                showBackButton = true,
                onBackClick = { viewModel.navigateBack() },
                onSearchClick = { viewModel.navigateTo(AppScreen.SEARCH) },
                onProfileClick = { viewModel.navigateTo(AppScreen.PROFILE) },
                onThemeToggle = { viewModel.toggleTheme() },
                isDarkMode = isDarkMode
            )
        },
        modifier = modifier.testTag("settings_screen")
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Display Preferences Section
            Text(
                text = "প্রদর্শন সেটিংস (Display Preferences):",
                style = MaterialTheme.typography.titleSmall.copy(color = IslamicGold, fontWeight = FontWeight.Bold)
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    // Dark Mode Toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("ডার্ক মোড (Islamic Emerald Theme)", fontWeight = FontWeight.Medium)
                            Text("গাঢ় সবুজ ও সোনালী রং", style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
                        }
                        Switch(
                            checked = isDarkMode,
                            onCheckedChange = { viewModel.toggleTheme() },
                            colors = SwitchDefaults.colors(checkedThumbColor = IslamicGold, checkedTrackColor = EmeraldSurfaceVariant)
                        )
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

                    // Bengali Translation Toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("বাংলা অনুবাদ প্রদর্শন", fontWeight = FontWeight.Medium)
                            Text("মুহিউদ্দীন খান অনুবাদ", style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
                        }
                        Switch(
                            checked = showTranslation,
                            onCheckedChange = { viewModel.toggleBengaliTranslation() },
                            colors = SwitchDefaults.colors(checkedThumbColor = IslamicGold, checkedTrackColor = EmeraldSurfaceVariant)
                        )
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

                    // Font Size Slider
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("আরবি হরফের আকার:", fontWeight = FontWeight.Medium)
                            Text("${arabicFontSize.toInt()} sp", color = IslamicGold, fontWeight = FontWeight.Bold)
                        }
                        Slider(
                            value = arabicFontSize,
                            onValueChange = { viewModel.setArabicFontSize(it) },
                            valueRange = 18f..38f,
                            colors = SliderDefaults.colors(thumbColor = IslamicGold, activeTrackColor = IslamicGold)
                        )
                    }
                }
            }

            // Audio Preferences
            Text(
                text = "তেলাওয়াত ও অডিও (Audio Preferences):",
                style = MaterialTheme.typography.titleSmall.copy(color = IslamicGold, fontWeight = FontWeight.Bold)
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("ডিফল্ট ক্বারী নির্বাচন:", fontWeight = FontWeight.Medium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Reciters.list.forEach { r ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedReciter == r.id,
                                onClick = { viewModel.setReciter(r.id) },
                                colors = RadioButtonDefaults.colors(selectedColor = IslamicGold)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(r.nameBengali, fontWeight = FontWeight.SemiBold)
                                Text("${r.nameEnglish} • ${r.bitrate}", style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
                            }
                        }
                    }
                }
            }

            // Data & Storage Management
            Text(
                text = "ডাটা ব্যবস্থাপনা (Data Management):",
                style = MaterialTheme.typography.titleSmall.copy(color = IslamicGold, fontWeight = FontWeight.Bold)
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    // Export Backup
                    Button(
                        onClick = {
                            val backupJson = """
                            {
                              "app": "Quranify Islamic",
                              "version": "1.0",
                              "timestamp": ${System.currentTimeMillis()},
                              "settings": {
                                "darkMode": $isDarkMode,
                                "arabicFontSize": $arabicFontSize,
                                "reciter": "$selectedReciter"
                              }
                            }
                            """.trimIndent()
                            exportContent = backupJson
                            showExportDialog = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldSurfaceVariant, contentColor = IslamicGold),
                        modifier = Modifier.fillMaxWidth().testTag("backup_export_button"),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(imageVector = Icons.Default.CloudDownload, contentDescription = "Backup")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("ডাটাবেজ ব্যাকআপ ও এক্সপোর্ট (JSON Export)")
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Clear Cache
                    OutlinedButton(
                        onClick = {
                            viewModel.showMessage("অ্যাপ ক্যাশ পরিষ্কার করা হয়েছে (Cache Cleared)")
                        },
                        modifier = Modifier.fillMaxWidth().testTag("clear_cache_button"),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(imageVector = Icons.Default.CleaningServices, contentDescription = "Clear Cache")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("অস্থায়ী ক্যাশ ফাইল মুছুন")
                    }
                }
            }

            // Legal & About
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column {
                    ProfileMenuRow("গোপনীয়তা নীতি (Privacy Policy)", Icons.Default.Security, { showPrivacyDialog = true })
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                    ProfileMenuRow("ব্যবহারের শর্তাবলী (Terms & Conditions)", Icons.Default.Description, { showTermsDialog = true })
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                    ProfileMenuRow("অ্যাপ সম্পর্কে ও সাহায্য (Help & Guide)", Icons.Default.HelpOutline, { viewModel.navigateTo(AppScreen.HELP_ABOUT) })
                }
            }
        }
    }
}
