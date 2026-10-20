package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.VerseEntity
import com.example.ui.theme.*

@Composable
fun PremiumCircularIconButton(
    onClick: () -> Unit,
    icon: ImageVector,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    size: Dp = 42.dp,
    iconSize: Dp = 20.dp,
    containerColor: Color = ButtonDarkBg,
    borderColor: Color = ButtonDarkBorder,
    tint: Color = ButtonIconWhite,
    enabled: Boolean = true,
    testTag: String? = null
) {
    val buttonModifier = if (testTag != null) {
        modifier.size(size).testTag(testTag)
    } else {
        modifier.size(size)
    }

    Surface(
        onClick = onClick,
        enabled = enabled,
        modifier = buttonModifier,
        shape = CircleShape,
        color = containerColor,
        border = androidx.compose.foundation.BorderStroke(1.2.dp, borderColor),
        tonalElevation = 2.dp,
        shadowElevation = 4.dp
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = tint,
                modifier = Modifier.size(iconSize)
            )
        }
    }
}

@Composable
fun PremiumPillButton(
    onClick: () -> Unit,
    text: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    iconSize: Dp = 16.dp,
    containerColor: Color = ButtonDarkBg,
    borderColor: Color = ButtonDarkBorder,
    textColor: Color = TextWarmWhite,
    iconColor: Color = IslamicGold,
    isSelected: Boolean = false,
    selectedContainerColor: Color = Color(0xFF262A32),
    selectedBorderColor: Color = IslamicGold,
    enabled: Boolean = true,
    testTag: String? = null
) {
    val actualContainerColor = if (isSelected) selectedContainerColor else containerColor
    val actualBorderColor = if (isSelected) selectedBorderColor else borderColor
    val actualTextColor = if (isSelected) IslamicGold else textColor
    val actualIconColor = if (isSelected) IslamicGold else iconColor

    val buttonModifier = if (testTag != null) {
        modifier.testTag(testTag)
    } else {
        modifier
    }

    Surface(
        onClick = onClick,
        enabled = enabled,
        modifier = buttonModifier,
        shape = RoundedCornerShape(20.dp),
        color = actualContainerColor,
        border = androidx.compose.foundation.BorderStroke(1.2.dp, actualBorderColor),
        tonalElevation = 2.dp,
        shadowElevation = 3.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = text,
                    tint = actualIconColor,
                    modifier = Modifier.size(iconSize)
                )
                Spacer(modifier = Modifier.width(6.dp))
            }
            Text(
                text = text,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = actualTextColor
                )
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuranTopBar(
    title: String,
    subtitle: String? = null,
    showBackButton: Boolean = false,
    showProfileButton: Boolean = false,
    showSearchButton: Boolean = true,
    showThemeToggle: Boolean = true,
    onBackClick: () -> Unit = {},
    onSearchClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onThemeToggle: () -> Unit = {},
    isDarkMode: Boolean = true
) {
    TopAppBar(
        title = {
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = IslamicGold
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        maxLines = 1
                    )
                }
            }
        },
        navigationIcon = {
            if (showBackButton) {
                Box(modifier = Modifier.padding(start = 10.dp, end = 6.dp)) {
                    PremiumCircularIconButton(
                        onClick = onBackClick,
                        icon = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "ফিরে যান",
                        size = 42.dp,
                        iconSize = 22.dp,
                        containerColor = ButtonDarkBg,
                        borderColor = ButtonDarkBorder,
                        tint = ButtonIconWhite,
                        testTag = "topbar_back_button"
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .padding(start = 12.dp, end = 4.dp)
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(ButtonDarkBg)
                        .border(1.2.dp, ButtonDarkBorder, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.MenuBook,
                        contentDescription = "Quranify Logo",
                        tint = IslamicGold,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        },
        actions = {
            if (showSearchButton) {
                Box(modifier = Modifier.padding(end = 4.dp)) {
                    PremiumCircularIconButton(
                        onClick = onSearchClick,
                        icon = Icons.Default.Search,
                        contentDescription = "অনুসন্ধান",
                        size = 38.dp,
                        iconSize = 19.dp,
                        containerColor = ButtonDarkBg,
                        borderColor = ButtonDarkBorder,
                        tint = ButtonIconWhite,
                        testTag = "topbar_search_button"
                    )
                }
            }
            if (showThemeToggle) {
                Box(modifier = Modifier.padding(end = 4.dp)) {
                    PremiumCircularIconButton(
                        onClick = onThemeToggle,
                        icon = if (isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                        contentDescription = "থিম পরিবর্তন",
                        size = 38.dp,
                        iconSize = 19.dp,
                        containerColor = ButtonDarkBg,
                        borderColor = ButtonDarkBorder,
                        tint = if (isDarkMode) IslamicGold else ButtonIconWhite,
                        testTag = "topbar_theme_toggle"
                    )
                }
            }
            if (showProfileButton) {
                Box(modifier = Modifier.padding(end = 8.dp)) {
                    PremiumCircularIconButton(
                        onClick = onProfileClick,
                        icon = Icons.Default.AccountCircle,
                        contentDescription = "প্রোফাইল",
                        size = 38.dp,
                        iconSize = 20.dp,
                        containerColor = ButtonDarkBg,
                        borderColor = ButtonDarkBorder,
                        tint = ButtonIconWhite,
                        testTag = "topbar_profile_button"
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = IslamicGold
        )
    )
}

@Composable
fun IslamicVerseCard(
    verse: VerseEntity,
    surahName: String,
    arabicFontSize: Float,
    showTranslation: Boolean,
    isPlaying: Boolean,
    isBookmarked: Boolean,
    onPlayClick: () -> Unit,
    onBookmarkClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .testTag("verse_card_${verse.surahNumber}_${verse.verseNumber}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(
                if (isPlaying) IslamicGold else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
            )
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Verse Header: Number Badge and Action Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Verse Number Badge
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(EmeraldSurfaceVariant)
                        .border(1.dp, IslamicGold, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${verse.verseNumber}",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = IslamicGold
                        )
                    )
                }

                // Quick Action Icons
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Play Button
                    PremiumCircularIconButton(
                        onClick = onPlayClick,
                        icon = if (isPlaying) Icons.Default.PauseCircleFilled else Icons.Default.PlayCircleFilled,
                        contentDescription = if (isPlaying) "থামান" else "তেলাওয়াত শুনুন",
                        size = 34.dp,
                        iconSize = 17.dp,
                        containerColor = ButtonDarkBg,
                        borderColor = if (isPlaying) StatusSuccess else ButtonDarkBorder,
                        tint = if (isPlaying) StatusSuccess else IslamicGold,
                        testTag = "play_verse_${verse.verseNumber}"
                    )

                    // Bookmark Button
                    PremiumCircularIconButton(
                        onClick = onBookmarkClick,
                        icon = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        contentDescription = "বুকমার্ক",
                        size = 34.dp,
                        iconSize = 16.dp,
                        containerColor = ButtonDarkBg,
                        borderColor = if (isBookmarked) IslamicGold else ButtonDarkBorder,
                        tint = if (isBookmarked) IslamicGold else ButtonIconWhite,
                        testTag = "bookmark_verse_${verse.verseNumber}"
                    )

                    // Copy Button
                    PremiumCircularIconButton(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText(
                                "Quran Verse",
                                "${verse.textArabic}\n\n${verse.textBengali}\n— সূরা $surahName (${verse.surahNumber}:${verse.verseNumber})"
                            )
                            clipboard.setPrimaryClip(clip)
                        },
                        icon = Icons.Default.ContentCopy,
                        contentDescription = "কপি করুন",
                        size = 34.dp,
                        iconSize = 16.dp,
                        containerColor = ButtonDarkBg,
                        borderColor = ButtonDarkBorder,
                        tint = ButtonIconWhite,
                        testTag = "copy_verse_${verse.verseNumber}"
                    )

                    // Share Button
                    PremiumCircularIconButton(
                        onClick = {
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(
                                    Intent.EXTRA_TEXT,
                                    "${verse.textArabic}\n\n${verse.textBengali}\n— সূরা $surahName (${verse.surahNumber}:${verse.verseNumber})\n[কুরআনিফাই ইসলামিক অ্যাপ]"
                                )
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "আয়াত শেয়ার করুন"))
                        },
                        icon = Icons.Default.Share,
                        contentDescription = "শেয়ার করুন",
                        size = 34.dp,
                        iconSize = 16.dp,
                        containerColor = ButtonDarkBg,
                        borderColor = ButtonDarkBorder,
                        tint = ButtonIconWhite,
                        testTag = "share_verse_${verse.verseNumber}"
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Arabic Text (Right-to-Left)
            Text(
                text = verse.textArabic,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = arabicFontSize.sp,
                    lineHeight = (arabicFontSize * 1.7f).sp,
                    textAlign = TextAlign.End,
                    color = MaterialTheme.colorScheme.onSurface
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            )

            if (verse.textTransliteration.isNotBlank()) {
                Text(
                    text = verse.textTransliteration,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = IslamicGoldLight,
                        fontSize = 13.sp
                    ),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            // Bengali Translation (conditional)
            if (showTranslation && verse.textBengali.isNotBlank()) {
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 8.dp),
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                )
                Text(
                    text = verse.textBengali,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 15.sp,
                        lineHeight = 22.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
                Text(
                    text = "অনুবাদ: ${verse.translationSource}",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                        fontSize = 11.sp
                    ),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

@Composable
fun IslamicFeatureCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    badgeText: String? = null
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(112.dp)
            .clickable(onClick = onClick)
            .testTag("feature_card_${title.replace(" ", "_")}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = EmeraldSurface
        ),
        border = androidx.compose.foundation.BorderStroke(1.dp, ButtonDarkBorder.copy(alpha = 0.6f))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(ButtonDarkBg)
                            .border(1.2.dp, ButtonDarkBorder, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = title,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    if (badgeText != null) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = ButtonDarkBg,
                            border = androidx.compose.foundation.BorderStroke(0.8.dp, ButtonDarkBorder)
                        ) {
                            Text(
                                text = badgeText,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = IslamicGold,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                            )
                        }
                    }
                }

                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextWarmWhite
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.sp
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

data class BottomNavConfig(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val screen: com.example.viewmodel.AppScreen,
    val testTag: String
)

@Composable
fun PremiumIslamicBottomBar(
    currentScreen: com.example.viewmodel.AppScreen,
    onNavigate: (com.example.viewmodel.AppScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    val items = listOf(
        BottomNavConfig("home", Icons.Default.Home, Icons.Outlined.Home, com.example.viewmodel.AppScreen.HOME, "nav_btn_home"),
        BottomNavConfig("Quran", Icons.Default.MenuBook, Icons.Outlined.MenuBook, com.example.viewmodel.AppScreen.SURAH_LIST, "nav_btn_quran"),
        BottomNavConfig("hadees", Icons.Default.AutoStories, Icons.Outlined.AutoStories, com.example.viewmodel.AppScreen.HADITH, "nav_btn_hadees"),
        BottomNavConfig("profile", Icons.Default.Person, Icons.Outlined.Person, com.example.viewmodel.AppScreen.PROFILE, "nav_btn_profile")
    )

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .testTag("premium_bottom_navigation_bar"),
        shape = RoundedCornerShape(32.dp),
        color = Color(0xFF090A0D),
        tonalElevation = 8.dp,
        shadowElevation = 16.dp,
        border = androidx.compose.foundation.BorderStroke(
            1.2.dp,
            ButtonDarkBorder
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    androidx.compose.ui.graphics.Brush.verticalGradient(
                        listOf(
                            Color(0xFF14161A),
                            Color(0xFF07080A)
                        )
                    )
                )
                .padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { item ->
                val isSelected = when (item.screen) {
                    com.example.viewmodel.AppScreen.HOME -> currentScreen == com.example.viewmodel.AppScreen.HOME
                    com.example.viewmodel.AppScreen.SURAH_LIST -> currentScreen == com.example.viewmodel.AppScreen.SURAH_LIST || currentScreen == com.example.viewmodel.AppScreen.SURAH_READING
                    com.example.viewmodel.AppScreen.HADITH -> currentScreen == com.example.viewmodel.AppScreen.HADITH
                    com.example.viewmodel.AppScreen.PROFILE -> currentScreen == com.example.viewmodel.AppScreen.PROFILE || currentScreen == com.example.viewmodel.AppScreen.AUTH
                    else -> false
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(54.dp)
                        .clip(RoundedCornerShape(22.dp))
                        .background(
                            if (isSelected) {
                                ButtonDarkBg
                            } else {
                                Color.Transparent
                            }
                        )
                        .border(
                            width = if (isSelected) 1.2.dp else 0.dp,
                            color = if (isSelected) ButtonDarkBorder else Color.Transparent,
                            shape = RoundedCornerShape(22.dp)
                        )
                        .clickable { onNavigate(item.screen) }
                        .testTag(item.testTag),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                            contentDescription = item.title,
                            tint = if (isSelected) IslamicGold else TextMutedGreen,
                            modifier = Modifier.size(if (isSelected) 24.dp else 22.dp)
                        )

                        Spacer(modifier = Modifier.height(2.dp))

                        Text(
                            text = item.title,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                fontSize = if (isSelected) 11.sp else 10.5.sp,
                                color = if (isSelected) IslamicGold else TextMutedGreen
                            ),
                            maxLines = 1
                        )

                        if (isSelected) {
                            Spacer(modifier = Modifier.height(2.dp))
                            Box(
                                modifier = Modifier
                                    .size(4.dp)
                                    .clip(CircleShape)
                                    .background(IslamicGold)
                            )
                        }
                    }
                }
            }
        }
    }
}

