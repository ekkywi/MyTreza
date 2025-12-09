package com.trezanix.mytreza.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private val Context.dataStore by preferencesDataStore(name = "mytreza_prefs")

class TokenManager @Inject constructor(@ApplicationContext private val context: Context) {
    companion object {
        private val ACCESS_TOKEN_KEY = stringPreferencesKey("access_token")
        private val USER_NAME_KEY = stringPreferencesKey("user_full_name")
        private val USER_EMAIL_KEY = stringPreferencesKey("user_email")
    }

    val accessToken: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[ACCESS_TOKEN_KEY]
    }

    val userName: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[USER_NAME_KEY]
    }

    val userEmail: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[USER_EMAIL_KEY]
    }

    suspend fun saveAuthData(token: String, name: String, email: String) {
        context.dataStore.edit { prefs ->
            prefs[ACCESS_TOKEN_KEY] = token
            prefs[USER_NAME_KEY] = name
            prefs[USER_EMAIL_KEY] = email
        }
    }

    suspend fun clearAuthData() {
        context.dataStore.edit { prefs ->
            prefs.clear()
        }
    }

    suspend fun saveToken(token: String) {
        context.dataStore.edit { prefs -> prefs[ACCESS_TOKEN_KEY] = token }
    }

    suspend fun clearToken() {
        context.dataStore.edit { prefs -> prefs.remove(ACCESS_TOKEN_KEY) }
    }
}