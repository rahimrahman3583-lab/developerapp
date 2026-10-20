package com.example.data

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    suspend fun getUserByUsername(username: String): UserEntity?

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    suspend fun getUserById(id: Long): UserEntity?

    @Query("SELECT * FROM users ORDER BY createdAt DESC")
    fun getAllUsersFlow(): Flow<List<UserEntity>>

    @Query("SELECT COUNT(*) FROM users")
    suspend fun getUserCount(): Int

    @Query("SELECT COUNT(*) FROM users WHERE isPremium = 1")
    suspend fun getPremiumUserCount(): Int

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertUser(user: UserEntity): Long

    @Update
    suspend fun updateUser(user: UserEntity)

    @Query("UPDATE users SET isPremium = :isPremium, premiumPlan = :plan, premiumExpiry = :expiry WHERE id = :userId")
    suspend fun updatePremiumStatus(userId: Long, isPremium: Boolean, plan: String, expiry: Long)
}

@Dao
interface SurahDao {
    @Query("SELECT * FROM surahs ORDER BY number ASC")
    fun getAllSurahsFlow(): Flow<List<SurahEntity>>

    @Query("SELECT * FROM surahs WHERE number = :number LIMIT 1")
    suspend fun getSurahByNumber(number: Int): SurahEntity?

    @Query("SELECT COUNT(*) FROM surahs")
    suspend fun getSurahCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(surahs: List<SurahEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSurah(surah: SurahEntity)

    @Update
    suspend fun updateSurah(surah: SurahEntity)
}

@Dao
interface VerseDao {
    @Query("SELECT * FROM verses WHERE surahNumber = :surahNumber ORDER BY verseNumber ASC")
    fun getVersesBySurahFlow(surahNumber: Int): Flow<List<VerseEntity>>

    @Query("SELECT * FROM verses WHERE surahNumber = :surahNumber AND verseNumber = :verseNumber LIMIT 1")
    suspend fun getVerse(surahNumber: Int, verseNumber: Int): VerseEntity?

    @Query("SELECT COUNT(*) FROM verses")
    suspend fun getVerseCount(): Int

    @Query("SELECT * FROM verses WHERE textBengali LIKE '%' || :query || '%' OR textArabic LIKE '%' || :query || '%' LIMIT 100")
    suspend fun searchVerses(query: String): List<VerseEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(verses: List<VerseEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVerse(verse: VerseEntity): Long

    @Update
    suspend fun updateVerse(verse: VerseEntity)

    @Delete
    suspend fun deleteVerse(verse: VerseEntity)
}

@Dao
interface BookmarkDao {
    @Query("SELECT * FROM bookmarks WHERE userId = :userId ORDER BY createdAt DESC")
    fun getBookmarksFlow(userId: Long): Flow<List<BookmarkEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM bookmarks WHERE userId = :userId AND surahNumber = :surah AND verseNumber = :verse)")
    suspend fun isBookmarked(userId: Long, surah: Int, verse: Int): Boolean

    @Query("DELETE FROM bookmarks WHERE userId = :userId AND surahNumber = :surah AND verseNumber = :verse")
    suspend fun removeBookmark(userId: Long, surah: Int, verse: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookmark(bookmark: BookmarkEntity): Long

    @Delete
    suspend fun deleteBookmark(bookmark: BookmarkEntity)
}

@Dao
interface HistoryDao {
    @Query("SELECT * FROM reading_history WHERE userId = :userId ORDER BY readAt DESC LIMIT 50")
    fun getHistoryFlow(userId: Long): Flow<List<HistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(history: HistoryEntity): Long

    @Delete
    suspend fun deleteHistory(history: HistoryEntity)

    @Query("DELETE FROM reading_history WHERE userId = :userId")
    suspend fun clearHistory(userId: Long)
}

@Dao
interface PaymentRequestDao {
    @Query("SELECT * FROM payment_requests ORDER BY createdAt DESC")
    fun getAllRequestsFlow(): Flow<List<PaymentRequestEntity>>

    @Query("SELECT * FROM payment_requests WHERE userId = :userId ORDER BY createdAt DESC")
    fun getUserRequestsFlow(userId: Long): Flow<List<PaymentRequestEntity>>

    @Query("SELECT COUNT(*) FROM payment_requests WHERE status = 'Pending'")
    suspend fun getPendingCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRequest(request: PaymentRequestEntity): Long

    @Update
    suspend fun updateRequest(request: PaymentRequestEntity)
}

@Dao
interface AuditLogDao {
    @Query("SELECT * FROM audit_logs ORDER BY timestamp DESC LIMIT 100")
    fun getAuditLogsFlow(): Flow<List<AuditLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: AuditLogEntity): Long
}

@Dao
interface SettingsDao {
    @Query("SELECT * FROM app_settings WHERE id = 1 LIMIT 1")
    suspend fun getSettings(): AppSettingsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSettings(settings: AppSettingsEntity)
}

@Database(
    entities = [
        UserEntity::class,
        SurahEntity::class,
        VerseEntity::class,
        BookmarkEntity::class,
        HistoryEntity::class,
        PaymentRequestEntity::class,
        AuditLogEntity::class,
        AppSettingsEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class QuranDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun surahDao(): SurahDao
    abstract fun verseDao(): VerseDao
    abstract fun bookmarkDao(): BookmarkDao
    abstract fun historyDao(): HistoryDao
    abstract fun paymentRequestDao(): PaymentRequestDao
    abstract fun auditLogDao(): AuditLogDao
    abstract fun settingsDao(): SettingsDao
}
