package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.PremiumIslamicBottomBar
import com.example.ui.components.QuranTopBar
import com.example.ui.theme.*
import com.example.viewmodel.AppScreen
import com.example.viewmodel.QuranViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ProfileScreen(
    viewModel: QuranViewModel,
    modifier: Modifier = Modifier
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    val bookmarks by viewModel.userBookmarks.collectAsState()
    val history by viewModel.userHistory.collectAsState()

    var showLogoutDialog by remember { mutableStateOf(false) }

    fun formatDate(timestamp: Long): String {
        return SimpleDateFormat("dd MMMM, yyyy", Locale.getDefault()).format(Date(timestamp))
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("লগআউট নিশ্চিতকরণ") },
            text = { Text("আপনি কি আপনার বর্তমান অ্যাকাউন্ট থেকে লগআউট করতে চান?") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.logout()
                        showLogoutDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = StatusError)
                ) {
                    Text("লগআউট")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("বাতিল")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            QuranTopBar(
                title = "প্রোফাইল (User Profile)",
                subtitle = "অ্যাকাউন্ট ও সাবস্ক্রিপশন তথ্য",
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
                    currentScreen = AppScreen.PROFILE,
                    onNavigate = { screen ->
                        viewModel.navigateTo(screen)
                    }
                )
            }
        },
        modifier = modifier.testTag("profile_screen")
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // User Avatar Box
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(CircleShape)
                    .background(EmeraldSurfaceVariant)
                    .border(2.dp, IslamicGold, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Avatar",
                    tint = IslamicGold,
                    modifier = Modifier.size(52.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = currentUser?.displayName ?: "অতিথি ব্যবহারকারী (Guest)",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            )

            Text(
                text = "@${currentUser?.username ?: "guest"}",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Premium Status Badge
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (currentUser?.isPremium == true) IslamicGold.copy(alpha = 0.2f) else EmeraldSurfaceVariant,
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (currentUser?.isPremium == true) IslamicGold else EmeraldBorder
                )
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (currentUser?.isPremium == true) Icons.Default.Verified else Icons.Default.StarBorder,
                        contentDescription = "Status",
                        tint = if (currentUser?.isPremium == true) IslamicGold else TextMutedGreen,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (currentUser?.isPremium == true)
                            "প্রিমিয়াম মেম্বার (${currentUser?.premiumPlan})"
                        else
                            "ফ্রি মেম্বার (সাধারণ প্ল্যান)",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = if (currentUser?.isPremium == true) IslamicGold else TextMutedGreen,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Stat Cards Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "${bookmarks.size}",
                            style = MaterialTheme.typography.titleLarge.copy(
                                color = IslamicGold,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Text(
                            text = "বুকমার্ক আয়াত",
                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                    }
                }

                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "${history.size}",
                            style = MaterialTheme.typography.titleLarge.copy(
                                color = IslamicGold,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Text(
                            text = "পঠিত ইতিহাস",
                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Quick Navigation List
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = androidx.compose.ui.graphics.SolidColor(MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                )
            ) {
                Column {
                    ProfileMenuRow(
                        title = "সংরক্ষিত আয়াতসমূহ (Bookmarks)",
                        icon = Icons.Default.Bookmark,
                        onClick = { viewModel.navigateTo(AppScreen.BOOKMARKS) }
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                    ProfileMenuRow(
                        title = "তিলাওয়াতের ইতিহাস (Reading History)",
                        icon = Icons.Default.History,
                        onClick = { viewModel.navigateTo(AppScreen.READING_HISTORY) }
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                    ProfileMenuRow(
                        title = "প্রিমিয়াম সাবস্ক্রিপশন (Upgrade)",
                        icon = Icons.Default.Star,
                        onClick = { viewModel.navigateTo(AppScreen.PREMIUM) }
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                    ProfileMenuRow(
                        title = "অ্যাপ সেটিংস (Settings)",
                        icon = Icons.Default.Settings,
                        onClick = { viewModel.navigateTo(AppScreen.SETTINGS) }
                    )
                    if (currentUser?.role == "admin") {
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                        ProfileMenuRow(
                            title = "এডমিন কন্ট্রোল প্যানেল (Admin Panel)",
                            icon = Icons.Default.AdminPanelSettings,
                            onClick = { viewModel.navigateTo(AppScreen.ADMIN_PANEL) },
                            tint = IslamicGold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (currentUser != null) {
                OutlinedButton(
                    onClick = { showLogoutDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("profile_logout_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = StatusError),
                    border = androidx.compose.foundation.BorderStroke(1.dp, StatusError.copy(alpha = 0.6f))
                ) {
                    Icon(imageVector = Icons.Default.Logout, contentDescription = "Logout")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("অ্যাকাউন্ট থেকে লগআউট করুন", fontWeight = FontWeight.Bold)
                }
            } else {
                Button(
                    onClick = { viewModel.navigateTo(AppScreen.AUTH) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = IslamicGold, contentColor = EmeraldDeepBg),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("লগইন বা রেজিস্টার করুন", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun ProfileMenuRow(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    tint: androidx.compose.ui.graphics.Color = IslamicGold
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = icon, contentDescription = title, tint = tint, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(14.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            )
        }
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
