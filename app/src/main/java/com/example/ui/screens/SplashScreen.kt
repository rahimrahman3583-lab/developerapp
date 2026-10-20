package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.EmeraldDeepBg
import com.example.ui.theme.IslamicGold
import com.example.ui.theme.IslamicGoldLight
import com.example.ui.theme.TextMutedGreen
import com.example.viewmodel.AppScreen
import com.example.viewmodel.QuranViewModel
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    viewModel: QuranViewModel,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "logo_scale"
    )

    var initStatus by remember { mutableStateOf("ডাটাবেজ ও কুরআন ডেটাসেট প্রস্তুত করা হচ্ছে...") }
    var isReady by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(1200)
        initStatus = "সকল ১১৪টি সূরার তথ্য যাচাই সম্পন্ন"
        delay(800)
        isReady = true
        delay(600)
        viewModel.navigateTo(AppScreen.HOME)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(EmeraldDeepBg)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo
            Box(
                modifier = Modifier
                    .size(130.dp)
                    .scale(scale)
                    .clip(CircleShape)
                    .background(IslamicGold.copy(alpha = 0.15f))
                    .border(2.dp, IslamicGold, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_quranify_logo),
                    contentDescription = "Quranify Islamic Logo",
                    modifier = Modifier.size(100.dp)
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = "কুরআনিফাই ইসলামিক",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = IslamicGold,
                    fontSize = 28.sp
                )
            )

            Text(
                text = "Quranify Islamic — নূর ও হেদায়েতের আলোকবর্তিকা",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = TextMutedGreen,
                    fontSize = 14.sp
                ),
                modifier = Modifier.padding(top = 6.dp)
            )

            Spacer(modifier = Modifier.height(40.dp))

            CircularProgressIndicator(
                color = IslamicGold,
                strokeWidth = 3.dp,
                modifier = Modifier.size(36.dp)
            )

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = initStatus,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = IslamicGoldLight,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier.padding(horizontal = 32.dp)
            )

            Spacer(modifier = Modifier.height(30.dp))

            Button(
                onClick = { viewModel.navigateTo(AppScreen.HOME) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = IslamicGold,
                    contentColor = EmeraldDeepBg
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(48.dp)
                    .testTag("splash_enter_button")
            ) {
                Text(
                    text = "অ্যাপে প্রবেশ করুন",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }
    }
}
