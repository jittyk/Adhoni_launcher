package com.jitz.adhoni_launcher.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "adhoni_parent_prefs")

class ParentRepository(private val context: Context) {

    companion object {
        private val PARENT_PIN_KEY = stringPreferencesKey("parent_pin")
        private val ALLOWED_APPS_KEY = stringSetPreferencesKey("allowed_apps")
    }

    val parentPin: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[PARENT_PIN_KEY]
    }

    val allowedApps: Flow<Set<String>> = context.dataStore.data.map { prefs ->
        prefs[ALLOWED_APPS_KEY] ?: emptySet()
    }

    suspend fun savePin(pin: String) {
        context.dataStore.edit { prefs ->
            prefs[PARENT_PIN_KEY] = pin
        }
    }

    suspend fun toggleAppPermission(packageName: String, isAllowed: Boolean) {
        context.dataStore.edit { prefs ->
            val currentApps = prefs[ALLOWED_APPS_KEY]?.toMutableSet() ?: mutableSetOf()
            if (isAllowed) {
                currentApps.add(packageName)
            } else {
                currentApps.remove(packageName)
            }
            prefs[ALLOWED_APPS_KEY] = currentApps
        }
    }
}