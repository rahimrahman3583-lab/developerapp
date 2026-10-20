package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.QuranTopBar
import com.example.ui.theme.*
import com.example.viewmodel.AppScreen
import com.example.viewmodel.QuranViewModel

@Composable
fun AuthScreen(
    viewModel: QuranViewModel,
    modifier: Modifier = Modifier
) {
    val isDarkMode by viewModel.isDarkMode.collectAsState()

    var isLoginTab by remember { mutableStateOf(true) }

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var displayName by remember { mutableStateOf("") }

    var passwordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            QuranTopBar(
                title = if (isLoginTab) "লগইন (Login)" else "রেজিস্ট্রেশন (Register)",
                subtitle = "কুরআনিফাই অ্যাকাউন্টে প্রবেশ",
                showBackButton = true,
                onBackClick = { viewModel.navigateBack() },
                onSearchClick = { viewModel.navigateTo(AppScreen.SEARCH) },
                onProfileClick = {},
                onThemeToggle = { viewModel.toggleTheme() },
                isDarkMode = isDarkMode
            )
        },
        modifier = modifier.testTag("auth_screen")
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(EmeraldSurfaceVariant)
                    .border(2.dp, IslamicGold, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Authentication",
                    tint = IslamicGold,
                    modifier = Modifier.size(36.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Tab Switcher
            TabRow(
                selectedTabIndex = if (isLoginTab) 0 else 1,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = IslamicGold,
                modifier = Modifier.clip(RoundedCornerShape(12.dp))
            ) {
                Tab(
                    selected = isLoginTab,
                    onClick = { isLoginTab = true; errorMessage = null; successMessage = null },
                    text = { Text("লগইন", fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = !isLoginTab,
                    onClick = { isLoginTab = false; errorMessage = null; successMessage = null },
                    text = { Text("নতুন অ্যাকাউন্ট", fontWeight = FontWeight.Bold) }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Error or Success Banner
            if (errorMessage != null) {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                ) {
                    Text(
                        text = errorMessage ?: "",
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(12.dp),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            if (successMessage != null) {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                    colors = CardDefaults.cardColors(containerColor = StatusSuccess.copy(alpha = 0.2f))
                ) {
                    Text(
                        text = successMessage ?: "",
                        color = StatusSuccess,
                        modifier = Modifier.padding(12.dp),
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Input Fields
            if (!isLoginTab) {
                OutlinedTextField(
                    value = displayName,
                    onValueChange = { displayName = it },
                    label = { Text("পূর্ণ নাম (Display Name)") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = IslamicGold) },
                    modifier = Modifier.fillMaxWidth().testTag("auth_name_field"),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(10.dp))
            }

            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("ইউজারনেম (Username)") },
                leadingIcon = { Icon(Icons.Default.AccountCircle, contentDescription = null, tint = IslamicGold) },
                modifier = Modifier.fillMaxWidth().testTag("auth_username_field"),
                shape = RoundedCornerShape(10.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("পাসওয়ার্ড (Password)") },
                leadingIcon = { Icon(Icons.Default.VpnKey, contentDescription = null, tint = IslamicGold) },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = "পাসওয়ার্ড দেখুন",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth().testTag("auth_password_field"),
                shape = RoundedCornerShape(10.dp),
                singleLine = true
            )

            if (!isLoginTab) {
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("পাসওয়ার্ড নিশ্চিত করুন (Confirm)") },
                    leadingIcon = { Icon(Icons.Default.Check, contentDescription = null, tint = IslamicGold) },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier.fillMaxWidth().testTag("auth_confirm_password_field"),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Action Button
            Button(
                onClick = {
                    errorMessage = null
                    successMessage = null
                    if (isLoginTab) {
                        viewModel.login(username, password) { success, msg ->
                            if (success) {
                                successMessage = msg
                                viewModel.navigateTo(AppScreen.HOME)
                            } else {
                                errorMessage = msg
                            }
                        }
                    } else {
                        viewModel.register(username, password, confirmPassword, displayName) { success, msg ->
                            if (success) {
                                successMessage = msg
                                viewModel.navigateTo(AppScreen.HOME)
                            } else {
                                errorMessage = msg
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("auth_submit_button"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = IslamicGold,
                    contentColor = EmeraldDeepBg
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = if (isLoginTab) "লগইন করুন" else "অ্যাকাউন্ট তৈরি করুন",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Demo Account Autofill for seamless testing
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = androidx.compose.ui.graphics.SolidColor(MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                )
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "টেস্টিং ডেমো অ্যাকাউন্ট (One-click Demo Fill):",
                        style = MaterialTheme.typography.labelSmall.copy(color = IslamicGold, fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                isLoginTab = true
                                username = "admin"
                                password = "Admin@123"
                            },
                            modifier = Modifier.weight(1f).testTag("demo_fill_admin"),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("এডমিন (admin)", fontSize = 11.sp, color = IslamicGold)
                        }

                        OutlinedButton(
                            onClick = {
                                isLoginTab = true
                                username = "quran_lover"
                                password = "user123"
                            },
                            modifier = Modifier.weight(1f).testTag("demo_fill_user"),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("ইউজার (user)", fontSize = 11.sp, color = IslamicGold)
                        }
                    }
                }
            }
        }
    }
}
