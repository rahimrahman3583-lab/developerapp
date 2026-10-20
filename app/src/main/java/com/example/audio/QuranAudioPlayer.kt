package com.example.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class AudioPlaybackState(
    val isPlaying: Boolean = false,
    val isBuffering: Boolean = false,
    val currentSurah: Int = 1,
    val currentVerse: Int = 1,
    val currentTitle: String = "Al-Fatiha (১:১)",
    val currentUrl: String = "",
    val currentPositionMs: Int = 0,
    val durationMs: Int = 0,
    val isRepeatOne: Boolean = false,
    val isShuffle: Boolean = false,
    val playbackSpeed: Float = 1.0f,
    val errorMessage: String? = null
)

class QuranAudioPlayer(private val context: Context) {

    private var mediaPlayer: MediaPlayer? = null
    private val handler = Handler(Looper.getMainLooper())

    private val _playbackState = MutableStateFlow(AudioPlaybackState())
    val playbackState: StateFlow<AudioPlaybackState> = _playbackState.asStateFlow()

    private val progressRunnable = object : Runnable {
        override fun run() {
            mediaPlayer?.let { mp ->
                try {
                    if (mp.isPlaying) {
                        _playbackState.value = _playbackState.value.copy(
                            currentPositionMs = mp.currentPosition,
                            durationMs = if (mp.duration > 0) mp.duration else _playbackState.value.durationMs
                        )
                    }
                } catch (e: Exception) {
                    // Ignore transient state
                }
            }
            handler.postDelayed(this, 300)
        }
    }

    init {
        handler.post(progressRunnable)
    }

    fun playVerse(
        surahNumber: Int,
        verseNumber: Int,
        title: String,
        audioUrl: String,
        onCompleteNext: (() -> Unit)? = null
    ) {
        if (audioUrl.isBlank()) {
            _playbackState.value = _playbackState.value.copy(
                errorMessage = "এই আয়াতে অডিও লিঙ্ক সংযুক্ত নেই (No audio source available)"
            )
            return
        }

        try {
            releasePlayer()
            _playbackState.value = _playbackState.value.copy(
                isBuffering = true,
                isPlaying = false,
                currentSurah = surahNumber,
                currentVerse = verseNumber,
                currentTitle = title,
                currentUrl = audioUrl,
                errorMessage = null
            )

            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                setDataSource(audioUrl)
                setOnPreparedListener { mp ->
                    _playbackState.value = _playbackState.value.copy(
                        isBuffering = false,
                        isPlaying = true,
                        durationMs = mp.duration
                    )
                    applyPlaybackSpeed(_playbackState.value.playbackSpeed)
                    mp.start()
                }
                setOnCompletionListener {
                    if (_playbackState.value.isRepeatOne) {
                        it.seekTo(0)
                        it.start()
                    } else {
                        _playbackState.value = _playbackState.value.copy(
                            isPlaying = false,
                            currentPositionMs = 0
                        )
                        onCompleteNext?.invoke()
                    }
                }
                setOnErrorListener { _, what, extra ->
                    _playbackState.value = _playbackState.value.copy(
                        isBuffering = false,
                        isPlaying = false,
                        errorMessage = "অডিও প্লেব্যাকের সমস্যা (ত্রুটি $what / $extra)। ইন্টারনেট সংযোগ পরীক্ষা করুন।"
                    )
                    true
                }
                prepareAsync()
            }
        } catch (e: Exception) {
            _playbackState.value = _playbackState.value.copy(
                isBuffering = false,
                isPlaying = false,
                errorMessage = "প্লেব্যাক শুরু করা সম্ভব হয়নি: ${e.localizedMessage}"
            )
        }
    }

    fun togglePlayPause() {
        val mp = mediaPlayer
        if (mp != null) {
            try {
                if (mp.isPlaying) {
                    mp.pause()
                    _playbackState.value = _playbackState.value.copy(isPlaying = false)
                } else {
                    mp.start()
                    _playbackState.value = _playbackState.value.copy(isPlaying = true)
                }
            } catch (e: Exception) {
                _playbackState.value = _playbackState.value.copy(
                    errorMessage = "প্লেয়ার পুনরায় চালু করা সম্ভব হয়নি: ${e.localizedMessage}"
                )
            }
        } else if (_playbackState.value.currentUrl.isNotBlank()) {
            playVerse(
                _playbackState.value.currentSurah,
                _playbackState.value.currentVerse,
                _playbackState.value.currentTitle,
                _playbackState.value.currentUrl
            )
        }
    }

    fun seekTo(positionMs: Int) {
        mediaPlayer?.let { mp ->
            try {
                mp.seekTo(positionMs)
                _playbackState.value = _playbackState.value.copy(currentPositionMs = positionMs)
            } catch (e: Exception) {
                // Ignore
            }
        }
    }

    fun toggleRepeat() {
        _playbackState.value = _playbackState.value.copy(isRepeatOne = !_playbackState.value.isRepeatOne)
    }

    fun toggleShuffle() {
        _playbackState.value = _playbackState.value.copy(isShuffle = !_playbackState.value.isShuffle)
    }

    fun setSpeed(speed: Float) {
        _playbackState.value = _playbackState.value.copy(playbackSpeed = speed)
        applyPlaybackSpeed(speed)
    }

    private fun applyPlaybackSpeed(speed: Float) {
        mediaPlayer?.let { mp ->
            try {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    mp.playbackParams = mp.playbackParams.setSpeed(speed)
                }
            } catch (e: Exception) {
                // Ignore
            }
        }
    }

    fun stop() {
        releasePlayer()
        _playbackState.value = _playbackState.value.copy(
            isPlaying = false,
            isBuffering = false,
            currentPositionMs = 0
        )
    }

    private fun releasePlayer() {
        try {
            mediaPlayer?.stop()
            mediaPlayer?.release()
        } catch (e: Exception) {
            // Ignore
        } finally {
            mediaPlayer = null
        }
    }

    fun destroy() {
        handler.removeCallbacks(progressRunnable)
        releasePlayer()
    }
}
