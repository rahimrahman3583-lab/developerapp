package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val username: String,
    val passwordHash: String,
    val displayName: String,
    val isPremium: Boolean = false,
    val premiumPlan: String = "Free",
    val premiumExpiry: Long = 0L,
    val role: String = "user", // "user" or "admin"
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "surahs")
data class SurahEntity(
    @PrimaryKey val number: Int,
    val nameArabic: String,
    val nameBengali: String,
    val meaningBengali: String,
    val verseCount: Int,
    val revelationType: String // "Makki" or "Madani"
)

@Entity(tableName = "verses")
data class VerseEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val surahNumber: Int,
    val verseNumber: Int,
    val textArabic: String,
    val textBengali: String,
    val textTransliteration: String = "",
    val translationSource: String = "মুহিউদ্দীন খান (Mohiuddin Khan)",
    val audioUrl: String = ""
)

@Entity(tableName = "bookmarks")
data class BookmarkEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    val surahNumber: Int,
    val verseNumber: Int,
    val surahName: String,
    val textArabic: String,
    val textBengali: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "reading_history")
data class HistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    val surahNumber: Int,
    val verseNumber: Int,
    val surahName: String,
    val textArabic: String,
    val textBengali: String,
    val readAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "payment_requests")
data class PaymentRequestEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    val username: String,
    val planName: String,
    val amount: String,
    val method: String, // "bKash", "Nagad", "Rocket", "Card"
    val transactionId: String,
    val status: String = "Pending", // "Pending", "Approved", "Rejected"
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "audit_logs")
data class AuditLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val adminUsername: String,
    val action: String,
    val target: String,
    val details: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "app_settings")
data class AppSettingsEntity(
    @PrimaryKey val id: Int = 1,
    val isDarkMode: Boolean = true,
    val arabicFontSizeSp: Float = 24f,
    val showBengaliTranslation: Boolean = true,
    val selectedReciterId: String = "alafasy",
    val playbackSpeed: Float = 1.0f,
    val language: String = "bn"
)
