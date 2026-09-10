package com.example.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

enum class ThemeMode {
    SYSTEM,
    LIGHT,
    DARK
}

data class UserPreferences(
    val themeMode: ThemeMode = ThemeMode.DARK,
    val defaultAlarmOffsetMinutes: Int = 0,
    val notificationSoundEnabled: Boolean = true,
    val notificationVibrateEnabled: Boolean = true
)

class DataStoreManager(private val context: Context) {

    private object PreferencesKeys {
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val DEFAULT_ALARM_OFFSET = intPreferencesKey("default_alarm_offset")
        val NOTIFICATION_SOUND = booleanPreferencesKey("notification_sound")
        val NOTIFICATION_VIBRATE = booleanPreferencesKey("notification_vibrate")
    }

    val userPreferencesFlow: Flow<UserPreferences> = context.dataStore.data.map { preferences ->
        val themeString = preferences[PreferencesKeys.THEME_MODE] ?: ThemeMode.DARK.name
        val themeMode = try {
            ThemeMode.valueOf(themeString)
        } catch (_: Exception) {
            ThemeMode.DARK
        }
        val defaultOffset = preferences[PreferencesKeys.DEFAULT_ALARM_OFFSET] ?: 0
        val sound = preferences[PreferencesKeys.NOTIFICATION_SOUND] ?: true
        val vibrate = preferences[PreferencesKeys.NOTIFICATION_VIBRATE] ?: true

        UserPreferences(
            themeMode = themeMode,
            defaultAlarmOffsetMinutes = defaultOffset,
            notificationSoundEnabled = sound,
            notificationVibrateEnabled = vibrate
        )
    }

    suspend fun setThemeMode(mode: ThemeMode) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.THEME_MODE] = mode.name
        }
    }

    suspend fun setDefaultAlarmOffset(offsetMinutes: Int) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.DEFAULT_ALARM_OFFSET] = offsetMinutes
        }
    }

    suspend fun setNotificationSoundEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.NOTIFICATION_SOUND] = enabled
        }
    }

    suspend fun setNotificationVibrateEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.NOTIFICATION_VIBRATE] = enabled
        }
    }
}
