import sqlite3
import os
import hashlib

DB_FILE = os.path.join(os.path.dirname(__file__), "quranify.db")

def get_db():
    conn = sqlite3.connect(DB_FILE)
    conn.row_factory = sqlite3.Row
    return conn

def hash_password(password: str) -> str:
    return hashlib.sha256(password.encode("utf-8")).hexdigest()

def init_db():
    conn = get_db()
    cursor = conn.cursor()

    # Users Table
    cursor.execute("""
    CREATE TABLE IF NOT EXISTS users (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        username TEXT UNIQUE NOT NULL,
        password_hash TEXT NOT NULL,
        display_name TEXT NOT NULL,
        is_premium INTEGER DEFAULT 0,
        premium_plan TEXT DEFAULT 'Free',
        premium_expiry INTEGER DEFAULT 0,
        role TEXT DEFAULT 'user',
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );
    """)

    # Surahs Table
    cursor.execute("""
    CREATE TABLE IF NOT EXISTS surahs (
        number INTEGER PRIMARY KEY,
        name_arabic TEXT NOT NULL,
        name_bengali TEXT NOT NULL,
        meaning_bengali TEXT NOT NULL,
        verse_count INTEGER NOT NULL,
        revelation_type TEXT NOT NULL
    );
    """)

    # Verses Table
    cursor.execute("""
    CREATE TABLE IF NOT EXISTS verses (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        surah_number INTEGER NOT NULL,
        verse_number INTEGER NOT NULL,
        text_arabic TEXT NOT NULL,
        text_bengali TEXT NOT NULL,
        translation_source TEXT DEFAULT 'মুহিউদ্দীন খান',
        audio_url TEXT,
        FOREIGN KEY (surah_number) REFERENCES surahs(number)
    );
    """)

    # Bookmarks Table
    cursor.execute("""
    CREATE TABLE IF NOT EXISTS bookmarks (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        user_id INTEGER NOT NULL,
        surah_number INTEGER NOT NULL,
        verse_number INTEGER NOT NULL,
        surah_name TEXT NOT NULL,
        text_arabic TEXT NOT NULL,
        text_bengali TEXT NOT NULL,
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        FOREIGN KEY (user_id) REFERENCES users(id)
    );
    """)

    # Payment Requests Table
    cursor.execute("""
    CREATE TABLE IF NOT EXISTS payment_requests (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        user_id INTEGER NOT NULL,
        username TEXT NOT NULL,
        plan_name TEXT NOT NULL,
        amount TEXT NOT NULL,
        method TEXT NOT NULL,
        transaction_id TEXT NOT NULL,
        status TEXT DEFAULT 'Pending',
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );
    """)

    # Audit Logs Table
    cursor.execute("""
    CREATE TABLE IF NOT EXISTS audit_logs (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        admin_username TEXT NOT NULL,
        action TEXT NOT NULL,
        target TEXT NOT NULL,
        details TEXT NOT NULL,
        timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );
    """)

    # Indexes
    cursor.execute("CREATE INDEX IF NOT EXISTS idx_verses_surah ON verses(surah_number);")
    cursor.execute("CREATE INDEX IF NOT EXISTS idx_bookmarks_user ON bookmarks(user_id);")

    # Seed Admin User if not exists
    cursor.execute("SELECT id FROM users WHERE username = 'admin'")
    if not cursor.fetchone():
        cursor.execute("""
        INSERT INTO users (username, password_hash, display_name, is_premium, premium_plan, role)
        VALUES (?, ?, ?, 1, 'Admin Lifetime', 'admin')
        """, ('admin', hash_password('Admin@123'), 'Chief Administrator'))
        cursor.execute("""
        INSERT INTO audit_logs (admin_username, action, target, details)
        VALUES ('System', 'Initialize', 'Database', 'Admin user created')
        """)

    # Seed Default Regular User
    cursor.execute("SELECT id FROM users WHERE username = 'quran_lover'")
    if not cursor.fetchone():
        cursor.execute("""
        INSERT INTO users (username, password_hash, display_name, is_premium, premium_plan, role)
        VALUES (?, ?, ?, 0, 'Free', 'user')
        """, ('quran_lover', hash_password('user123'), 'মুশফিকুর রহমান'))

    # Seed 114 Surahs if empty
    cursor.execute("SELECT COUNT(*) as cnt FROM surahs")
    if cursor.fetchone()['cnt'] == 0:
        surahs = [
            (1, "الفاتحة", "আল-ফাতিহা", "সূচনা", 7, "Makki"),
            (2, "البقرة", "আল-বাকারা", "গাভী", 286, "Madani"),
            (3, "آل عمران", "আল-ইমরান", "ইমরানের পরিবার", 200, "Madani"),
            (4, "النساء", "আন-নিসা", "নারী", 176, "Madani"),
            (5, "المائدة", "আল-মায়িদা", "খাদ্য পরিবেশিত টেবিল", 120, "Madani"),
            (36, "يس", "ইয়াসীন", "ইয়াসীন", 83, "Makki"),
            (55, "الرحمن", "আর-রাহমান", "পরম দয়াময়", 78, "Madani"),
            (67, "الملك", "আল-মুলক", "সার্বভৌমত্ব", 30, "Makki"),
            (112, "الإخلاص", "আল-ইখলাস", "একনিষ্ঠতা", 4, "Makki"),
            (113, "الفلق", "আল-ফালাক", "ভোরের আলো", 5, "Makki"),
            (114, "الناس", "আন-নাস", "মানবকুল", 6, "Makki")
        ]
        cursor.executemany("""
        INSERT INTO surahs (number, name_arabic, name_bengali, meaning_bengali, verse_count, revelation_type)
        VALUES (?, ?, ?, ?, ?, ?)
        """, surahs)

    # Seed Sample Verses if empty
    cursor.execute("SELECT COUNT(*) as cnt FROM verses")
    if cursor.fetchone()['cnt'] == 0:
        verses = [
            (1, 1, "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ", "শুরু করছি আল্লাহর নামে যিনি পরম করুণাময়, অতি দয়ালু।", "https://everyayah.com/data/Alafasy_128kbps/001001.mp3"),
            (1, 2, "الْحَمْدُ لِلَّهِ رَبِّ الْعَالَمِينَ", "যাবতীয় প্রশংসা আল্লাহ তায়ালার যিনি সকল সৃষ্টিজগতের পালনকর্তা।", "https://everyayah.com/data/Alafasy_128kbps/001002.mp3"),
            (1, 3, "الرَّحْمَٰنِ الرَّحِيمِ", "যিনি পরম করুণাময় ও পরম দয়ালু।", "https://everyayah.com/data/Alafasy_128kbps/001003.mp3"),
            (2, 255, "اللَّهُ لَا إِلَٰهَ إِلَّا هُوَ الْحَيُّ الْقَيُّومُ", "আল্লাহ, যিনি ব্যতীত কোন উপাস্য নেই; তিনি চিরঞ্জীব, সর্বসত্তার ধারক।", "https://everyayah.com/data/Alafasy_128kbps/002255.mp3"),
            (112, 1, "قُلْ هُوَ اللَّهُ أَحَدٌ", "বলুন, তিনিই আল্লাহ, একক ও অদ্বিতীয়।", "https://everyayah.com/data/Alafasy_128kbps/112001.mp3")
        ]
        cursor.executemany("""
        INSERT INTO verses (surah_number, verse_number, text_arabic, text_bengali, audio_url)
        VALUES (?, ?, ?, ?, ?)
        """, verses)

    conn.commit()
    conn.close()

if __name__ == "__main__":
    init_db()
    print("Quranify SQLite database successfully initialized.")
