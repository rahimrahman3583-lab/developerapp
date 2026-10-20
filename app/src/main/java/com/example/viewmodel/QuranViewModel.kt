package com.example.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.audio.QuranAudioPlayer
import com.example.data.*
import com.example.sensors.CompassSensorManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

enum class AppScreen {
    SPLASH,
    HOME,
    SURAH_LIST,
    SURAH_READING,
    HADITH,
    AUDIO_PLAYER,
    BOOKMARKS,
    READING_HISTORY,
    SEARCH,
    PRAYER_TIMES,
    QIBLA,
    AUTH,
    PROFILE,
    PREMIUM,
    SETTINGS,
    ADMIN_PANEL,
    HELP_ABOUT
}

class QuranViewModel(application: Application) : AndroidViewModel(application) {

    val db: QuranDatabase = Room.databaseBuilder(
        application.applicationContext,
        QuranDatabase::class.java,
        "quranify_islamic.db"
    ).fallbackToDestructiveMigration().build()

    val audioPlayer = QuranAudioPlayer(application.applicationContext)
    val compassManager = CompassSensorManager(application.applicationContext)

    // Navigation Back Stack
    private val _screenStack = MutableStateFlow(listOf(AppScreen.SPLASH))
    val currentScreen: StateFlow<AppScreen> = _screenStack.map { it.lastOrNull() ?: AppScreen.HOME }
        .stateIn(viewModelScope, SharingStarted.Eagerly, AppScreen.SPLASH)

    // Current User
    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser: StateFlow<UserEntity?> = _currentUser.asStateFlow()

    // App Settings
    private val _isDarkMode = MutableStateFlow(true)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    private val _arabicFontSize = MutableStateFlow(24f)
    val arabicFontSize: StateFlow<Float> = _arabicFontSize.asStateFlow()

    private val _showBengaliTranslation = MutableStateFlow(true)
    val showBengaliTranslation: StateFlow<Boolean> = _showBengaliTranslation.asStateFlow()

    private val _selectedReciterId = MutableStateFlow("alafasy")
    val selectedReciterId: StateFlow<String> = _selectedReciterId.asStateFlow()

    private val _playbackSpeed = MutableStateFlow(1.0f)
    val playbackSpeed: StateFlow<Float> = _playbackSpeed.asStateFlow()

    // Surah & Reading State
    val allSurahs = db.surahDao().getAllSurahsFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedSurah = MutableStateFlow<SurahEntity?>(null)
    val selectedSurah: StateFlow<SurahEntity?> = _selectedSurah.asStateFlow()

    private val _currentVerses = MutableStateFlow<List<VerseEntity>>(emptyList())
    val currentVerses: StateFlow<List<VerseEntity>> = _currentVerses.asStateFlow()

    // Bookmarks & History
    val userBookmarks: StateFlow<List<BookmarkEntity>> = _currentUser.flatMapLatest { user ->
        val uid = user?.id ?: 1L
        db.bookmarkDao().getBookmarksFlow(uid)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userHistory: StateFlow<List<HistoryEntity>> = _currentUser.flatMapLatest { user ->
        val uid = user?.id ?: 1L
        db.historyDao().getHistoryFlow(uid)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Prayer Times & Qibla
    private val _prayerTimes = MutableStateFlow(PrayerTimeCalculator.calculate())
    val prayerTimes: StateFlow<PrayerTimesResult> = _prayerTimes.asStateFlow()

    val qiblaBearing: Double = QiblaCalculator.calculateBearing(23.8103, 90.4125) // Dhaka
    val qiblaDistanceKm: Double = QiblaCalculator.calculateDistanceKm(23.8103, 90.4125)

    // Search
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _searchResults = MutableStateFlow<List<VerseEntity>>(emptyList())
    val searchResults: StateFlow<List<VerseEntity>> = _searchResults.asStateFlow()

    // Hadith Bookmarks & Text Scaling
    private val _bookmarkedHadithIds = MutableStateFlow<Set<Int>>(setOf())
    val bookmarkedHadithIds: StateFlow<Set<Int>> = _bookmarkedHadithIds.asStateFlow()

    fun toggleHadithBookmark(hadithId: Int) {
        val current = _bookmarkedHadithIds.value.toMutableSet()
        if (current.contains(hadithId)) {
            current.remove(hadithId)
            showMessage("হাদিস বুকমার্ক থেকে সরানো হয়েছে")
        } else {
            current.add(hadithId)
            showMessage("হাদিস বুকমার্কে সংরক্ষণ করা হয়েছে")
        }
        _bookmarkedHadithIds.value = current
    }

    private val _hadithTextScale = MutableStateFlow(1.0f)
    val hadithTextScale: StateFlow<Float> = _hadithTextScale.asStateFlow()

    fun increaseHadithTextScale() {
        if (_hadithTextScale.value < 1.45f) {
            _hadithTextScale.value += 0.1f
        }
    }

    fun decreaseHadithTextScale() {
        if (_hadithTextScale.value > 0.85f) {
            _hadithTextScale.value -= 0.1f
        }
    }

    // Admin state
    val allUsers = db.userDao().getAllUsersFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allPaymentRequests = db.paymentRequestDao().getAllRequestsFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allAuditLogs = db.auditLogDao().getAuditLogsFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _adminStats = MutableStateFlow(AdminStats())
    val adminStats: StateFlow<AdminStats> = _adminStats.asStateFlow()

    // Feedback message
    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

    init {
        initializeDatabase()
        compassManager.start()
    }

    override fun onCleared() {
        super.onCleared()
        audioPlayer.destroy()
        compassManager.stop()
    }

    fun showMessage(msg: String) {
        _snackbarMessage.value = msg
    }

    fun clearMessage() {
        _snackbarMessage.value = null
    }

    // Navigation
    fun navigateTo(screen: AppScreen) {
        val current = _screenStack.value
        if (current.lastOrNull() != screen) {
            _screenStack.value = current + screen
        }
    }

    fun navigateBack(): Boolean {
        val current = _screenStack.value
        return if (current.size > 1) {
            _screenStack.value = current.dropLast(1)
            true
        } else {
            false
        }
    }

    // Database Initialization
    fun initializeDatabase(onComplete: (() -> Unit)? = null) {
        viewModelScope.launch(Dispatchers.IO) {
            // Seed Surahs if empty
            val surahCount = db.surahDao().getSurahCount()
            if (surahCount == 0) {
                db.surahDao().insertAll(VerifiedQuranData.allSurahs)
            }

            // Seed Verses if empty
            val verseCount = db.verseDao().getVerseCount()
            if (verseCount == 0) {
                db.verseDao().insertAll(VerifiedQuranData.verifiedVerses)
            }

            // Seed Admin and default user if empty
            val adminUser = db.userDao().getUserByUsername("admin")
            if (adminUser == null) {
                db.userDao().insertUser(
                    UserEntity(
                        username = "admin",
                        passwordHash = hashPassword("Admin@123"),
                        displayName = "Chief Administrator",
                        isPremium = true,
                        premiumPlan = "Admin Lifetime",
                        role = "admin"
                    )
                )
                db.auditLogDao().insertLog(
                    AuditLogEntity(
                        adminUsername = "System",
                        action = "Initialize",
                        target = "Database",
                        details = "Default admin account created"
                    )
                )
            }

            // Create default regular user
            val defaultUser = db.userDao().getUserByUsername("quran_lover")
            if (defaultUser == null) {
                val uid = db.userDao().insertUser(
                    UserEntity(
                        username = "quran_lover",
                        passwordHash = hashPassword("user123"),
                        displayName = "মুশফিকুর রহমান (Mushfiq)",
                        isPremium = false,
                        role = "user"
                    )
                )
                // Default bookmark for new user: Ayatul Kursi
                db.bookmarkDao().insertBookmark(
                    BookmarkEntity(
                        userId = uid,
                        surahNumber = 2,
                        verseNumber = 255,
                        surahName = "আল-বাকারা",
                        textArabic = "اللَّهُ لَا إِلَٰهَ إِلَّا هُوَ الْحَيُّ الْقَيُّومُ...",
                        textBengali = "আল্লাহ, যিনি ব্যতীত কোন উপাস্য নেই; তিনি চিরঞ্জীব, সর্বসত্তার ধারক..."
                    )
                )
            }

            // Load App Settings
            val settings = db.settingsDao().getSettings()
            if (settings != null) {
                _isDarkMode.value = settings.isDarkMode
                _arabicFontSize.value = settings.arabicFontSizeSp
                _showBengaliTranslation.value = settings.showBengaliTranslation
                _selectedReciterId.value = settings.selectedReciterId
                _playbackSpeed.value = settings.playbackSpeed
            }

            // Auto-login default user for seamless instant experience
            val userToLogin = defaultUser ?: db.userDao().getUserByUsername("quran_lover")
            _currentUser.value = userToLogin

            refreshAdminStats()

            withContext(Dispatchers.Main) {
                onComplete?.invoke()
            }
        }
    }

    // Theme & Settings
    fun toggleTheme() {
        val newTheme = !_isDarkMode.value
        _isDarkMode.value = newTheme
        saveCurrentSettings()
        showMessage(if (newTheme) "ডার্ক মোড সক্রিয় করা হয়েছে" else "লাইট মোড সক্রিয় করা হয়েছে")
    }

    fun setArabicFontSize(size: Float) {
        _arabicFontSize.value = size.coerceIn(18f, 38f)
        saveCurrentSettings()
    }

    fun toggleBengaliTranslation() {
        val newVal = !_showBengaliTranslation.value
        _showBengaliTranslation.value = newVal
        saveCurrentSettings()
        showMessage(if (newVal) "বাংলা অনুবাদ দৃশ্যমান" else "বাংলা অনুবাদ লুকানো হয়েছে")
    }

    fun setReciter(reciterId: String) {
        _selectedReciterId.value = reciterId
        saveCurrentSettings()
        showMessage("ক্বারী পরিবর্তন করা হয়েছে: ${Reciters.getById(reciterId).nameBengali}")
    }

    fun setPlaybackSpeed(speed: Float) {
        _playbackSpeed.value = speed
        audioPlayer.setSpeed(speed)
        saveCurrentSettings()
    }

    private fun saveCurrentSettings() {
        viewModelScope.launch(Dispatchers.IO) {
            db.settingsDao().saveSettings(
                AppSettingsEntity(
                    id = 1,
                    isDarkMode = _isDarkMode.value,
                    arabicFontSizeSp = _arabicFontSize.value,
                    showBengaliTranslation = _showBengaliTranslation.value,
                    selectedReciterId = _selectedReciterId.value,
                    playbackSpeed = _playbackSpeed.value
                )
            )
        }
    }

    // Quran Reading
    fun openSurah(surah: SurahEntity) {
        _selectedSurah.value = surah
        viewModelScope.launch(Dispatchers.IO) {
            db.verseDao().getVersesBySurahFlow(surah.number).collectLatest { verses ->
                _currentVerses.value = verses
            }
        }
        navigateTo(AppScreen.SURAH_READING)
    }

    fun logVerseRead(verse: VerseEntity, surahName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val uid = _currentUser.value?.id ?: 1L
            db.historyDao().insertHistory(
                HistoryEntity(
                    userId = uid,
                    surahNumber = verse.surahNumber,
                    verseNumber = verse.verseNumber,
                    surahName = surahName,
                    textArabic = verse.textArabic,
                    textBengali = verse.textBengali
                )
            )
        }
    }

    fun toggleBookmark(verse: VerseEntity, surahName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val uid = _currentUser.value?.id ?: 1L
            val isBookmarked = db.bookmarkDao().isBookmarked(uid, verse.surahNumber, verse.verseNumber)
            if (isBookmarked) {
                db.bookmarkDao().removeBookmark(uid, verse.surahNumber, verse.verseNumber)
                showMessage("বুকমার্ক থেকে সরানো হয়েছে")
            } else {
                db.bookmarkDao().insertBookmark(
                    BookmarkEntity(
                        userId = uid,
                        surahNumber = verse.surahNumber,
                        verseNumber = verse.verseNumber,
                        surahName = surahName,
                        textArabic = verse.textArabic,
                        textBengali = verse.textBengali
                    )
                )
                showMessage("বুকমার্কে যোগ করা হয়েছে")
            }
        }
    }

    fun removeBookmark(bookmark: BookmarkEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            db.bookmarkDao().deleteBookmark(bookmark)
            showMessage("বুকমার্ক মোছা হয়েছে")
        }
    }

    fun clearAllHistory() {
        viewModelScope.launch(Dispatchers.IO) {
            val uid = _currentUser.value?.id ?: 1L
            db.historyDao().clearHistory(uid)
            showMessage("পড়ার ইতিহাস পরিষ্কার করা হয়েছে")
        }
    }

    fun deleteHistoryItem(history: HistoryEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            db.historyDao().deleteHistory(history)
            showMessage("ইতিহাস থেকে সরানো হয়েছে")
        }
    }

    // Audio Playback
    fun playVerseAudio(verse: VerseEntity, surahName: String) {
        val reciter = Reciters.getById(_selectedReciterId.value)
        val audioUrl = if (verse.audioUrl.isNotBlank()) verse.audioUrl else reciter.getVerseAudioUrl(verse.surahNumber, verse.verseNumber)
        val title = "$surahName (${verse.surahNumber}:${verse.verseNumber})"
        audioPlayer.playVerse(verse.surahNumber, verse.verseNumber, title, audioUrl) {
            // On completion play next verse if available
            val nextVerseNum = verse.verseNumber + 1
            viewModelScope.launch(Dispatchers.IO) {
                val nextVerse = db.verseDao().getVerse(verse.surahNumber, nextVerseNum)
                if (nextVerse != null) {
                    playVerseAudio(nextVerse, surahName)
                }
            }
        }
    }

    // Search
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        if (query.isBlank()) {
            _searchResults.value = emptyList()
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            val results = db.verseDao().searchVerses(query.trim())
            _searchResults.value = results
        }
    }

    fun clearSearch() {
        _searchQuery.value = ""
        _searchResults.value = emptyList()
    }

    // Auth
    fun register(username: String, pass: String, passConfirm: String, displayName: String, onResult: (Boolean, String) -> Unit) {
        if (username.isBlank() || pass.isBlank()) {
            onResult(false, "ব্যবহারকারীর নাম ও পাসওয়ার্ড প্রদান করুন")
            return
        }
        if (pass.length < 6) {
            onResult(false, "পাসওয়ার্ড কমপক্ষে ৬ অক্ষরের হতে হবে")
            return
        }
        if (pass != passConfirm) {
            onResult(false, "পাসওয়ার্ড দুটি মেলেনি")
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            val existing = db.userDao().getUserByUsername(username.trim())
            if (existing != null) {
                withContext(Dispatchers.Main) {
                    onResult(false, "এই ইউজারনেমটি ইতিমধ্যে নিবন্ধিত")
                }
                return@launch
            }

            val newUser = UserEntity(
                username = username.trim(),
                passwordHash = hashPassword(pass),
                displayName = if (displayName.isBlank()) username else displayName,
                role = "user"
            )
            val newId = db.userDao().insertUser(newUser)
            val savedUser = db.userDao().getUserById(newId)
            _currentUser.value = savedUser
            refreshAdminStats()
            withContext(Dispatchers.Main) {
                onResult(true, "অভিনন্দন! রেজিস্ট্রেশন সফল হয়েছে।")
            }
        }
    }

    fun login(username: String, pass: String, onResult: (Boolean, String) -> Unit) {
        if (username.isBlank() || pass.isBlank()) {
            onResult(false, "ইউজারনেম এবং পাসওয়ার্ড লিখুন")
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            val user = db.userDao().getUserByUsername(username.trim())
            if (user == null || user.passwordHash != hashPassword(pass)) {
                withContext(Dispatchers.Main) {
                    onResult(false, "ভুল ইউজারনেম অথবা পাসওয়ার্ড")
                }
                return@launch
            }

            _currentUser.value = user
            withContext(Dispatchers.Main) {
                onResult(true, "স্বাগতম, ${user.displayName}!")
            }
        }
    }

    fun logout() {
        _currentUser.value = null
        showMessage("লগআউট সফল হয়েছে")
        navigateTo(AppScreen.HOME)
    }

    // Premium Subscription
    fun submitPaymentRequest(planName: String, amount: String, method: String, txId: String, onComplete: (Boolean, String) -> Unit) {
        if (txId.isBlank()) {
            onComplete(false, "ট্রানজেকশন আইডি (TxID) প্রদান করুন")
            return
        }
        val user = _currentUser.value
        if (user == null) {
            onComplete(false, "পেমেন্ট রিকোয়েস্ট পাঠানোর জন্য লগইন করুন")
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            db.paymentRequestDao().insertRequest(
                PaymentRequestEntity(
                    userId = user.id,
                    username = user.username,
                    planName = planName,
                    amount = amount,
                    method = method,
                    transactionId = txId.trim(),
                    status = "Pending"
                )
            )
            refreshAdminStats()
            withContext(Dispatchers.Main) {
                onComplete(true, "পেমেন্ট রেফারেন্স জমা হয়েছে! এডমিন যাচাইয়ের পর সক্রিয় হবে।")
            }
        }
    }

    // Admin Operations
    fun refreshAdminStats() {
        viewModelScope.launch(Dispatchers.IO) {
            val totalUsers = db.userDao().getUserCount()
            val premiumUsers = db.userDao().getPremiumUserCount()
            val totalSurahs = db.surahDao().getSurahCount()
            val totalVerses = db.verseDao().getVerseCount()
            val pendingPayments = db.paymentRequestDao().getPendingCount()

            _adminStats.value = AdminStats(
                totalUsers = totalUsers,
                premiumUsers = premiumUsers,
                totalSurahs = totalSurahs,
                totalVerses = totalVerses,
                pendingPayments = pendingPayments
            )
        }
    }

    fun adminGrantPremium(user: UserEntity, isGrant: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            db.userDao().updatePremiumStatus(
                userId = user.id,
                isPremium = isGrant,
                plan = if (isGrant) "Admin Lifetime" else "Free",
                expiry = if (isGrant) System.currentTimeMillis() + 365L * 24 * 3600 * 1000 else 0L
            )
            val adminName = _currentUser.value?.username ?: "Admin"
            db.auditLogDao().insertLog(
                AuditLogEntity(
                    adminUsername = adminName,
                    action = if (isGrant) "Grant Premium" else "Revoke Premium",
                    target = user.username,
                    details = "Subscription status changed to: $isGrant"
                )
            )
            if (_currentUser.value?.id == user.id) {
                _currentUser.value = db.userDao().getUserById(user.id)
            }
            refreshAdminStats()
            showMessage("ব্যবহারকারী ${user.username} এর প্রিমিয়াম স্ট্যাটাস আপডেট হয়েছে")
        }
    }

    fun adminReviewPayment(request: PaymentRequestEntity, isApprove: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            val newStatus = if (isApprove) "Approved" else "Rejected"
            db.paymentRequestDao().updateRequest(request.copy(status = newStatus))

            if (isApprove) {
                db.userDao().updatePremiumStatus(
                    userId = request.userId,
                    isPremium = true,
                    plan = request.planName,
                    expiry = System.currentTimeMillis() + 365L * 24 * 3600 * 1000
                )
            }

            val adminName = _currentUser.value?.username ?: "Admin"
            db.auditLogDao().insertLog(
                AuditLogEntity(
                    adminUsername = adminName,
                    action = "Review Payment",
                    target = "TxID: ${request.transactionId}",
                    details = "Status: $newStatus for ${request.username}"
                )
            )
            refreshAdminStats()
            showMessage("পেমেন্ট রিকোয়েস্ট $newStatus করা হয়েছে")
        }
    }

    fun adminAddVerse(surahNumber: Int, verseNumber: Int, arabic: String, bengali: String, onDone: (Boolean, String) -> Unit) {
        if (arabic.isBlank() || bengali.isBlank()) {
            onDone(false, "আরবি ও বাংলা টেক্সট পূরণ করুন")
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            db.verseDao().insertVerse(
                VerseEntity(
                    surahNumber = surahNumber,
                    verseNumber = verseNumber,
                    textArabic = arabic.trim(),
                    textBengali = bengali.trim(),
                    translationSource = "মুহিউদ্দীন খান"
                )
            )
            val adminName = _currentUser.value?.username ?: "Admin"
            db.auditLogDao().insertLog(
                AuditLogEntity(
                    adminUsername = adminName,
                    action = "Add Verse",
                    target = "Surah $surahNumber:$verseNumber",
                    details = "Verse added by admin"
                )
            )
            refreshAdminStats()
            withContext(Dispatchers.Main) {
                onDone(true, "আয়াত সফলভাবে সংরক্ষিত হয়েছে!")
            }
        }
    }

    private fun hashPassword(password: String): String {
        val bytes = java.security.MessageDigest.getInstance("SHA-256").digest(password.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}

data class AdminStats(
    val totalUsers: Int = 0,
    val premiumUsers: Int = 0,
    val totalSurahs: Int = 0,
    val totalVerses: Int = 0,
    val pendingPayments: Int = 0
)
