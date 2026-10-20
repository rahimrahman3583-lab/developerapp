package com.example.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.QuranTopBar
import com.example.ui.theme.*
import com.example.viewmodel.AppScreen
import com.example.viewmodel.QuranViewModel
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun QiblaScreen(
    viewModel: QuranViewModel,
    modifier: Modifier = Modifier
) {
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    val azimuthDegrees by viewModel.compassManager.azimuthDegrees.collectAsState()
    val hasSensors by viewModel.compassManager.hasSensors.collectAsState()

    // Dhaka Qibla bearing is ~277.5° from True North
    val qiblaBearing = viewModel.qiblaBearing.toFloat()
    val qiblaDistance = viewModel.qiblaDistanceKm

    // Relative angle between device heading and Kaaba
    val relativeAngle = (qiblaBearing - azimuthDegrees + 360) % 360

    val animatedAzimuth by animateFloatAsState(
        targetValue = azimuthDegrees,
        animationSpec = tween(durationMillis = 200),
        label = "compass_rotation"
    )

    Scaffold(
        topBar = {
            QuranTopBar(
                title = "কিবলা কম্পাস (Qibla Compass)",
                subtitle = "কাবার সঠিক দিক নির্ণয়",
                showBackButton = true,
                onBackClick = { viewModel.navigateBack() },
                onSearchClick = { viewModel.navigateTo(AppScreen.SEARCH) },
                onProfileClick = { viewModel.navigateTo(AppScreen.PROFILE) },
                onThemeToggle = { viewModel.toggleTheme() },
                isDarkMode = isDarkMode
            )
        },
        modifier = modifier.testTag("qibla_screen")
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // Info Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = androidx.compose.ui.graphics.SolidColor(IslamicGold.copy(alpha = 0.4f))
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "মক্কা মুকাররমার দূরত্ব:",
                                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                            )
                            Text(
                                text = "${qiblaDistance.toInt()} কি.মি. (ক্বাবা শরীফ)",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    color = TextWarmWhite,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "কিবলা কোণ:",
                                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                            )
                            Text(
                                text = "${qiblaBearing.toInt()}° (পশ্চিম-উত্তর)",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    color = IslamicGold,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Compass Dial
                Box(
                    modifier = Modifier
                        .size(280.dp)
                        .clip(CircleShape)
                        .background(EmeraldSurfaceVariant)
                        .border(3.dp, IslamicGold, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    // Rotating Dial Canvas
                    Canvas(
                        modifier = Modifier
                            .fillMaxSize()
                            .rotate(-animatedAzimuth)
                    ) {
                        val center = Offset(size.width / 2, size.height / 2)
                        val radius = size.width / 2 - 20

                        // Draw Cardinal Directions (N, E, S, W)
                        // North indicator (Gold needle)
                        val northPath = Path().apply {
                            moveTo(center.x, center.y - radius)
                            lineTo(center.x - 12, center.y - 40)
                            lineTo(center.x + 12, center.y - 40)
                            close()
                        }
                        drawPath(northPath, Color(0xFFE5C058))

                        // South indicator (Red needle)
                        val southPath = Path().apply {
                            moveTo(center.x, center.y + radius)
                            lineTo(center.x - 12, center.y + 40)
                            lineTo(center.x + 12, center.y + 40)
                            close()
                        }
                        drawPath(southPath, Color(0xFFE57373))

                        // Draw Qibla pointer on dial at 277.5 degrees
                        val qiblaRad = Math.toRadians((qiblaBearing - 90).toDouble())
                        val qiblaX = center.x + (radius - 30) * cos(qiblaRad).toFloat()
                        val qiblaY = center.y + (radius - 30) * sin(qiblaRad).toFloat()
                        drawCircle(Color(0xFF4CAF50), 10f, Offset(qiblaX, qiblaY))
                    }

                    // Center Hub with Kaaba Icon
                    Box(
                        modifier = Modifier
                            .size(68.dp)
                            .clip(CircleShape)
                            .background(EmeraldDeepBg)
                            .border(2.dp, IslamicGold, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Explore,
                            contentDescription = "Kaaba Qibla",
                            tint = IslamicGold,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Heading Degrees Display
                Text(
                    text = "ডিভাইসের দিক: ${animatedAzimuth.toInt()}°",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )

                // Facing check: within 5 degrees of Qibla
                val isFacingQibla = kotlin.math.abs(relativeAngle) < 6 || kotlin.math.abs(relativeAngle - 360) < 6
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isFacingQibla) StatusSuccess.copy(alpha = 0.2f) else EmeraldSurfaceVariant,
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isFacingQibla) StatusSuccess else EmeraldBorder
                    ),
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Text(
                        text = if (isFacingQibla) "✓ আপনি ঠিক কিবলার দিকে মুখ করে আছেন!" else "কিবলার দিকে ঘুরুন: ${qiblaBearing.toInt()}° তে পৌঁছান",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = if (isFacingQibla) StatusSuccess else TextWarmWhite,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }

            // Sensor Calibration note
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (hasSensors) Icons.Default.CheckCircle else Icons.Default.Info,
                        contentDescription = "Sensor state",
                        tint = if (hasSensors) StatusSuccess else IslamicGold,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = if (hasSensors)
                            "ম্যাগনেটিক সেন্সর সক্রিয়। সঠিক মানের জন্য ফোনটি '8' আকারে বাতাসে ঘোরান।"
                        else
                            "সেন্সর অনুপস্থিত বা এমুলেটর মোড সক্রিয়। বাংলাদেশের জন্য কিবলা দিক ২৭৭° (পশ্চিম-উত্তর)।",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 12.sp
                        )
                    )
                }
            }
        }
    }
}
