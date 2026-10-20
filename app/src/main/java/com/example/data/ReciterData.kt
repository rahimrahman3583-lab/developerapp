package com.example.data

data class ReciterInfo(
    val id: String,
    val nameEnglish: String,
    val nameBengali: String,
    val subfolder: String,
    val bitrate: String = "128kbps"
) {
    fun getVerseAudioUrl(surah: Int, verse: Int): String {
        val sStr = surah.toString().padStart(3, '0')
        val vStr = verse.toString().padStart(3, '0')
        return "https://everyayah.com/data/${subfolder}/${sStr}${vStr}.mp3"
    }
}

object Reciters {
    val list = listOf(
        ReciterInfo("alafasy", "Mishary Rashid Alafasy", "মিশারী রাশিদ আল-আফাসী", "Alafasy_128kbps"),
        ReciterInfo("sudais", "Abdur-Rahman as-Sudais", "আব্দুর রহমান আস-সুদাইস", "Abdurrahmaan_As-Sudais_192kbps", "192kbps"),
        ReciterInfo("muaiqly", "Maher Al-Muaiqly", "মাহের আল-মুআইকলী", "Maher_AlMuaiqly_64kbps", "64kbps"),
        ReciterInfo("ghamadi", "Saad Al-Ghamdi", "সা'দ আল-গামদী", "Ghamadi_40kbps", "40kbps")
    )

    fun getById(id: String): ReciterInfo {
        return list.find { it.id == id } ?: list.first()
    }
}
