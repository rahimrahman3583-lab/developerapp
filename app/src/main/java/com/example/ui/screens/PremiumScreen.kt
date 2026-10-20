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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.PaymentRequestEntity
import com.example.ui.components.QuranTopBar
import com.example.ui.theme.*
import com.example.viewmodel.AppScreen
import com.example.viewmodel.QuranViewModel

@Composable
fun PremiumScreen(
    viewModel: QuranViewModel,
    modifier: Modifier = Modifier
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    val allRequests by viewModel.allPaymentRequests.collectAsState()

    var selectedPlan by remember { mutableStateOf("বাৎসরিক প্ল্যান (Yearly)") }
    var paymentMethod by remember { mutableStateOf("bKash") }
    var transactionId by remember { mutableStateOf("") }
    var submitStatus by remember { mutableStateOf<String?>(null) }
    var isSubmitting by remember { mutableStateOf(false) }

    val userRequests = remember(allRequests, currentUser) {
        val uid = currentUser?.id ?: 1L
        allRequests.filter { it.userId == uid }
    }

    Scaffold(
        topBar = {
            QuranTopBar(
                title = "কুরআনিফাই প্রিমিয়াম (Premium)",
                subtitle = "অফলাইন তেলাওয়াত ও বিশেষ সুবিধা",
                showBackButton = true,
                onBackClick = { viewModel.navigateBack() },
                onSearchClick = { viewModel.navigateTo(AppScreen.SEARCH) },
                onProfileClick = { viewModel.navigateTo(AppScreen.PROFILE) },
                onThemeToggle = { viewModel.toggleTheme() },
                isDarkMode = isDarkMode
            )
        },
        modifier = modifier.testTag("premium_screen")
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Hero Banner
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = EmeraldSurfaceVariant),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = androidx.compose.ui.graphics.SolidColor(IslamicGold)
                )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Premium Star",
                        tint = IslamicGold,
                        modifier = Modifier.size(42.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "কুরআনিফাই প্রিমিয়াম মেম্বারশিপ",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = IslamicGold
                        ),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "পবিত্র কুরআন তিলাওয়াত ও উপলব্ধির এক পরিপূর্ণ অভিজ্ঞতা",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextWarmWhite,
                            textAlign = TextAlign.Center
                        ),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Premium Features List
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "প্রিমিয়াম মেম্বারদের বিশেষ সুবিধাসমূহ:",
                        style = MaterialTheme.typography.titleSmall.copy(
                            color = IslamicGold,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    PremiumFeatureRow("১১৪টি সূরার উচ্চমানের অফলাইন অডিও প্যাক")
                    PremiumFeatureRow("বিশ্বখ্যাত ৪ জন প্রধান ক্বারীর নিরবচ্ছিন্ন তেলাওয়াত")
                    PremiumFeatureRow("বিস্তারিত আয়াতভিত্তিক তাফসীর ও শানে নুযূল")
                    PremiumFeatureRow("সম্পূর্ণ বিজ্ঞাপনমুক্ত ও দ্রুতগতির ইন্টারফেস")
                    PremiumFeatureRow("২৪/৭ বিশেষ ইসলামিক হেল্পডেস্ক সহায়তা")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Plan Options
            Text(
                text = "সাবস্ক্রিপশন প্ল্যান নির্বাচন করুন:",
                style = MaterialTheme.typography.titleSmall.copy(
                    color = IslamicGold,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                PlanCard(
                    title = "মাসিক",
                    price = "৳৯৯",
                    duration = "১ মাস",
                    isSelected = selectedPlan.contains("মাসিক"),
                    onClick = { selectedPlan = "মাসিক প্ল্যান (Monthly ৳৯৯)" },
                    modifier = Modifier.weight(1f)
                )
                PlanCard(
                    title = "বাৎসরিক",
                    price = "৳৭৯৯",
                    duration = "১২ মাস",
                    isPopular = true,
                    isSelected = selectedPlan.contains("বাৎসরিক"),
                    onClick = { selectedPlan = "বাৎসরিক প্ল্যান (Yearly ৳৭৯৯)" },
                    modifier = Modifier.weight(1f)
                )
                PlanCard(
                    title = "আজীবন",
                    price = "৳১৯৯৯",
                    duration = "লাইফটাইম",
                    isSelected = selectedPlan.contains("আজীবন"),
                    onClick = { selectedPlan = "আজীবন প্ল্যান (Lifetime ৳১৯৯৯)" },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Manual Payment Submission Form
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = androidx.compose.ui.graphics.SolidColor(MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "ম্যানুয়াল পেমেন্ট যাচাইকরণ ফর্ম",
                        style = MaterialTheme.typography.titleSmall.copy(
                            color = IslamicGold,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = "বিকাশ/নগদ/রকেটে 'Send Money' করুন: 01712-345678 (মার্চেন্ট/পার্সোনাল)। এরপর ট্রানজেকশন আইডি নিচে প্রদান করুন।",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 18.sp
                        ),
                        modifier = Modifier.padding(vertical = 6.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Method Selection
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("bKash", "Nagad", "Rocket", "Card").forEach { m ->
                            FilterChip(
                                selected = paymentMethod == m,
                                onClick = { paymentMethod = m },
                                label = { Text(m, fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = IslamicGold,
                                    selectedLabelColor = EmeraldDeepBg
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = transactionId,
                        onValueChange = { transactionId = it },
                        label = { Text("ট্রানজেকশন আইডি (TxID যেমন 9G4K8L)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("premium_txid_field"),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            isSubmitting = true
                            val amount = when {
                                selectedPlan.contains("মাসিক") -> "৳৯৯"
                                selectedPlan.contains("বাৎসরিক") -> "৳৭৯৯"
                                else -> "৳১৯৯৯"
                            }
                            viewModel.submitPaymentRequest(selectedPlan, amount, paymentMethod, transactionId) { success, msg ->
                                isSubmitting = false
                                submitStatus = msg
                                if (success) {
                                    transactionId = ""
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("submit_payment_ref_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = IslamicGold,
                            contentColor = EmeraldDeepBg
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("পেমেন্ট রেফারেন্স জমা দিন", fontWeight = FontWeight.Bold)
                    }

                    if (submitStatus != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = submitStatus ?: "",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = StatusSuccess,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // User Submitted Payment Requests Status List
            if (userRequests.isNotEmpty()) {
                Text(
                    text = "আপনার সাম্প্রতিক পেমেন্ট রিকোয়েস্টের অবস্থা:",
                    style = MaterialTheme.typography.titleSmall.copy(
                        color = IslamicGold,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.align(Alignment.Start)
                )

                Spacer(modifier = Modifier.height(8.dp))

                userRequests.forEach { req ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
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
                                    text = "${req.planName} (${req.amount})",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = TextWarmWhite,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                                Text(
                                    text = "পদ্ধতি: ${req.method} | TxID: ${req.transactionId}",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = when (req.status) {
                                    "Approved" -> StatusSuccess.copy(alpha = 0.2f)
                                    "Rejected" -> StatusError.copy(alpha = 0.2f)
                                    else -> IslamicGold.copy(alpha = 0.2f)
                                }
                            ) {
                                Text(
                                    text = when (req.status) {
                                        "Approved" -> "অনুমোদিত (Active)"
                                        "Rejected" -> "বাতিলকৃত"
                                        else -> "যাচাই প্রক্রিয়াধীন (Pending)"
                                    },
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = when (req.status) {
                                            "Approved" -> StatusSuccess
                                            "Rejected" -> StatusError
                                            else -> IslamicGold
                                        },
                                        fontWeight = FontWeight.Bold
                                    ),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PlanCard(
    title: String,
    price: String,
    duration: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isPopular: Boolean = false
) {
    Card(
        modifier = modifier
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) IslamicGold else EmeraldBorder,
                shape = RoundedCornerShape(12.dp)
            ),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) EmeraldSurfaceVariant else MaterialTheme.colorScheme.surface
        ),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (isPopular) {
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = IslamicGold,
                    modifier = Modifier.padding(bottom = 4.dp)
                ) {
                    Text(
                        text = "সেরা অফার",
                        color = EmeraldDeepBg,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }
            }
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            )
            Text(
                text = price,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = IslamicGold
                )
            )
            Text(
                text = duration,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 10.sp
                )
            )
        }
    }
}

@Composable
fun PremiumFeatureRow(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = StatusSuccess,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )
    }
}
