package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AuditLogEntity
import com.example.data.PaymentRequestEntity
import com.example.data.Reciters
import com.example.data.UserEntity
import com.example.ui.components.QuranTopBar
import com.example.ui.theme.*
import com.example.viewmodel.AppScreen
import com.example.viewmodel.QuranViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun AdminPanelScreen(
    viewModel: QuranViewModel,
    modifier: Modifier = Modifier
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    val adminStats by viewModel.adminStats.collectAsState()
    val allUsers by viewModel.allUsers.collectAsState()
    val paymentRequests by viewModel.allPaymentRequests.collectAsState()
    val auditLogs by viewModel.allAuditLogs.collectAsState()
    val allSurahs by viewModel.allSurahs.collectAsState()

    var activeTab by remember { mutableStateOf(0) } // 0: Dashboard, 1: Users, 2: Quran Editor, 3: Payments, 4: Audit Logs

    // Dialogs
    var showAddVerseDialog by remember { mutableStateOf(false) }
    var selectedSurahForVerse by remember { mutableStateOf(1) }
    var newVerseNumber by remember { mutableStateOf("") }
    var newVerseArabic by remember { mutableStateOf("") }
    var newVerseBengali by remember { mutableStateOf("") }

    val tabs = listOf("ড্যাশবোর্ড", "ব্যবহারকারী", "কুরআন এডিটর", "পেমেন্ট অনুমোদন", "অডিট লগ")

    fun formatDate(timestamp: Long): String {
        return SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()).format(Date(timestamp))
    }

    // Role Guard: Check if admin
    val isAdmin = currentUser?.role == "admin"

    if (showAddVerseDialog) {
        AlertDialog(
            onDismissRequest = { showAddVerseDialog = false },
            title = { Text("নতুন আয়াত যুক্ত করুন") },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    Text("সূরা: ${allSurahs.find { it.number == selectedSurahForVerse }?.nameBengali ?: selectedSurahForVerse}")
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newVerseNumber,
                        onValueChange = { newVerseNumber = it },
                        label = { Text("আয়াত নম্বর") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newVerseArabic,
                        onValueChange = { newVerseArabic = it },
                        label = { Text("আরবি টেক্সট") },
                        modifier = Modifier.fillMaxWidth().height(100.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newVerseBengali,
                        onValueChange = { newVerseBengali = it },
                        label = { Text("বাংলা অনুবাদ") },
                        modifier = Modifier.fillMaxWidth().height(100.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val vNum = newVerseNumber.toIntOrNull() ?: 1
                        viewModel.adminAddVerse(selectedSurahForVerse, vNum, newVerseArabic, newVerseBengali) { success, msg ->
                            viewModel.showMessage(msg)
                            if (success) {
                                showAddVerseDialog = false
                                newVerseArabic = ""
                                newVerseBengali = ""
                                newVerseNumber = ""
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = IslamicGold, contentColor = EmeraldDeepBg)
                ) {
                    Text("সংরক্ষণ করুন")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddVerseDialog = false }) {
                    Text("বাতিল")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            QuranTopBar(
                title = "এডমিন কন্ট্রোল প্যানেল",
                subtitle = "কুরআনিফাই ওয়েব ও মোবাইল সিস্টেম",
                showBackButton = true,
                onBackClick = { viewModel.navigateBack() },
                onSearchClick = { viewModel.navigateTo(AppScreen.SEARCH) },
                onProfileClick = { viewModel.navigateTo(AppScreen.PROFILE) },
                onThemeToggle = { viewModel.toggleTheme() },
                isDarkMode = isDarkMode
            )
        },
        modifier = modifier.testTag("admin_panel_screen")
    ) { innerPadding ->
        if (!isAdmin) {
            // Access Denied Screen
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.GppBad,
                        contentDescription = "Access Denied",
                        tint = StatusError,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "প্রবেশাধিকার সংরক্ষিত (Access Denied)",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = StatusError
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "এই সেকশনটি শুধুমাত্র সিস্টেম প্রশাসকদের (Administrator) জন্য উন্মুক্ত। অনুগ্রহ করে এডমিন অ্যাকাউন্ট দিয়ে লগইন করুন।",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = { viewModel.navigateTo(AppScreen.AUTH) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = IslamicGold,
                            contentColor = EmeraldDeepBg
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("এডমিন হিসেবে লগইন করুন", fontWeight = FontWeight.Bold)
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // Admin Tabs
                ScrollableTabRow(
                    selectedTabIndex = activeTab,
                    edgePadding = 12.dp,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = IslamicGold
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = activeTab == index,
                            onClick = { activeTab = index },
                            text = { Text(title, fontWeight = if (activeTab == index) FontWeight.Bold else FontWeight.Normal) }
                        )
                    }
                }

                // Tab Content
                when (activeTab) {
                    0 -> AdminDashboardTab(adminStats = adminStats, onNavigateTab = { activeTab = it })
                    1 -> AdminUsersTab(users = allUsers, onTogglePremium = { user, grant -> viewModel.adminGrantPremium(user, grant) })
                    2 -> AdminQuranEditorTab(
                        surahs = allSurahs,
                        onAddVerseClick = { surahNum ->
                            selectedSurahForVerse = surahNum
                            showAddVerseDialog = true
                        }
                    )
                    3 -> AdminPaymentsTab(requests = paymentRequests, onReview = { req, approve -> viewModel.adminReviewPayment(req, approve) })
                    4 -> AdminAuditLogTab(logs = auditLogs)
                }
            }
        }
    }
}

@Composable
fun AdminDashboardTab(
    adminStats: com.example.viewmodel.AdminStats,
    onNavigateTab: (Int) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = EmeraldSurfaceVariant),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = androidx.compose.ui.graphics.SolidColor(IslamicGold.copy(alpha = 0.5f))
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = "Server State",
                        tint = IslamicGold,
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text("লোকাল সার্ভার ও ডাটাবেজ স্ট্যাটাস", style = MaterialTheme.typography.titleMedium.copy(color = TextWarmWhite, fontWeight = FontWeight.Bold))
                        Text("হোস্ট: 127.0.0.1:5000 | SQLite: সংযুক্ত ও সক্রিয়", style = MaterialTheme.typography.bodySmall.copy(color = StatusSuccess))
                    }
                }
            }
        }

        item {
            Text("সিস্টেম লাইভ পরিসংখ্যান (Live Stats):", style = MaterialTheme.typography.titleSmall.copy(color = IslamicGold, fontWeight = FontWeight.Bold))
        }

        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                AdminStatCard("মোট ব্যবহারকারী", "${adminStats.totalUsers}", Icons.Default.People, { onNavigateTab(1) }, Modifier.weight(1f))
                AdminStatCard("প্রিমিয়াম গ্রাহক", "${adminStats.premiumUsers}", Icons.Default.Star, { onNavigateTab(1) }, Modifier.weight(1f))
            }
        }

        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                AdminStatCard("মোট সূরা", "${adminStats.totalSurahs}", Icons.Default.MenuBook, { onNavigateTab(2) }, Modifier.weight(1f))
                AdminStatCard("মোট আয়াত", "${adminStats.totalVerses}", Icons.Default.FormatQuote, { onNavigateTab(2) }, Modifier.weight(1f))
            }
        }

        item {
            AdminStatCard(
                "অপেক্ষমান পেমেন্ট রিকোয়েস্ট",
                "${adminStats.pendingPayments} টি",
                Icons.Default.PendingActions,
                { onNavigateTab(3) },
                Modifier.fillMaxWidth(),
                highlightColor = if (adminStats.pendingPayments > 0) StatusWarning else IslamicGold
            )
        }
    }
}

@Composable
fun AdminStatCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    highlightColor: androidx.compose.ui.graphics.Color = IslamicGold
) {
    Card(
        modifier = modifier.clickable(onClick = onClick),
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
                Text(title, style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
                Icon(imageVector = icon, contentDescription = null, tint = highlightColor, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(value, style = MaterialTheme.typography.headlineMedium.copy(color = highlightColor, fontWeight = FontWeight.Bold))
        }
    }
}

@Composable
fun AdminUsersTab(
    users: List<UserEntity>,
    onTogglePremium: (UserEntity, Boolean) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }

    val filtered = users.filter {
        searchQuery.isBlank() ||
                it.username.contains(searchQuery.trim(), ignoreCase = true) ||
                it.displayName.contains(searchQuery.trim(), ignoreCase = true)
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("ইউজারনেম দিয়ে খুঁজুন...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = IslamicGold) },
            shape = RoundedCornerShape(10.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(filtered) { user ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(user.displayName, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.width(6.dp))
                                if (user.role == "admin") {
                                    Surface(color = IslamicGold, shape = RoundedCornerShape(4.dp)) {
                                        Text("ADMIN", color = EmeraldDeepBg, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 4.dp))
                                    }
                                }
                            }
                            Text("@${user.username} • ${if (user.isPremium) "প্রিমিয়াম (${user.premiumPlan})" else "ফ্রি"}", style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
                        }

                        Button(
                            onClick = { onTogglePremium(user, !user.isPremium) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (user.isPremium) StatusError else StatusSuccess
                            ),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(if (user.isPremium) "বাতিল" else "অনুমোদন", fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdminQuranEditorTab(
    surahs: List<com.example.data.SurahEntity>,
    onAddVerseClick: (Int) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Text("কুরআনের বিষয়বস্তু ও আয়াত সংযোজন:", style = MaterialTheme.typography.titleSmall.copy(color = IslamicGold, fontWeight = FontWeight.Bold))
        }

        items(surahs) { surah ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("${surah.number}. সূরা ${surah.nameBengali} (${surah.nameArabic})", fontWeight = FontWeight.Bold)
                        Text("${surah.revelationType} • ${surah.verseCount} আয়াত", style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
                    }

                    Button(
                        onClick = { onAddVerseClick(surah.number) },
                        colors = ButtonDefaults.buttonColors(containerColor = IslamicGold, contentColor = EmeraldDeepBg),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("+ আয়াত যোগ", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun AdminPaymentsTab(
    requests: List<PaymentRequestEntity>,
    onReview: (PaymentRequestEntity, Boolean) -> Unit
) {
    if (requests.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
            Text("কোনো পেমেন্ট রিকোয়েস্ট জমা নেই", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(requests) { req ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("${req.planName} (${req.amount})", fontWeight = FontWeight.Bold, color = IslamicGold)
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = when (req.status) {
                                    "Approved" -> StatusSuccess.copy(alpha = 0.2f)
                                    "Rejected" -> StatusError.copy(alpha = 0.2f)
                                    else -> StatusWarning.copy(alpha = 0.2f)
                                }
                            ) {
                                Text(
                                    text = req.status,
                                    color = when (req.status) {
                                        "Approved" -> StatusSuccess
                                        "Rejected" -> StatusError
                                        else -> StatusWarning
                                    },
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Text("ব্যবহারকারী: @${req.username} | পদ্ধতি: ${req.method}", style = MaterialTheme.typography.bodySmall)
                        Text("TxID: ${req.transactionId}", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold))

                        if (req.status == "Pending") {
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(
                                    onClick = { onReview(req, true) },
                                    colors = ButtonDefaults.buttonColors(containerColor = StatusSuccess),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("অনুমোদন (Approve)")
                                }
                                Button(
                                    onClick = { onReview(req, false) },
                                    colors = ButtonDefaults.buttonColors(containerColor = StatusError),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("বাতিল (Reject)")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdminAuditLogTab(
    logs: List<AuditLogEntity>
) {
    fun formatDate(timestamp: Long): String {
        return SimpleDateFormat("dd MMM yyyy, hh:mm:ss a", Locale.getDefault()).format(Date(timestamp))
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(logs) { log ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(log.action, fontWeight = FontWeight.Bold, color = IslamicGold)
                        Text(formatDate(log.timestamp), style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
                    }
                    Text("এডমিন: ${log.adminUsername} • লক্ষ্য: ${log.target}", style = MaterialTheme.typography.bodySmall)
                    Text("বিবরণ: ${log.details}", style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
                }
            }
        }
    }
}
