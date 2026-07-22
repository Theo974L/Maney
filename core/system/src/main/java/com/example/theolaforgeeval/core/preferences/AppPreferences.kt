package com.example.theolaforgeeval.core.preferences

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Centralise les préférences de retour utilisateur (animations, vibrations, sons)
 * pour que chaque écran puisse les respecter de la même façon.
 */
class AppPreferences(context: Context) {

    private val prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val _animationsEnabled = MutableStateFlow(prefs.getBoolean(KEY_ANIMATIONS, true))
    val animationsEnabled: StateFlow<Boolean> = _animationsEnabled.asStateFlow()

    private val _vibrationsEnabled = MutableStateFlow(prefs.getBoolean(KEY_VIBRATIONS, true))
    val vibrationsEnabled: StateFlow<Boolean> = _vibrationsEnabled.asStateFlow()

    private val _soundsEnabled = MutableStateFlow(prefs.getBoolean(KEY_SOUNDS, true))
    val soundsEnabled: StateFlow<Boolean> = _soundsEnabled.asStateFlow()

    fun setAnimationsEnabled(enabled: Boolean) {
        _animationsEnabled.value = enabled
        prefs.edit().putBoolean(KEY_ANIMATIONS, enabled).apply()
    }

    fun setVibrationsEnabled(enabled: Boolean) {
        _vibrationsEnabled.value = enabled
        prefs.edit().putBoolean(KEY_VIBRATIONS, enabled).apply()
    }

    fun setSoundsEnabled(enabled: Boolean) {
        _soundsEnabled.value = enabled
        prefs.edit().putBoolean(KEY_SOUNDS, enabled).apply()
    }

    companion object {
        private const val PREFS_NAME = "maney_prefs"
        private const val KEY_ANIMATIONS = "animations_enabled"
        private const val KEY_VIBRATIONS = "vibrations_enabled"
        private const val KEY_SOUNDS = "sounds_enabled"
    }
}
