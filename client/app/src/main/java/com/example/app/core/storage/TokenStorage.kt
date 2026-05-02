package com.example.app.core.storage

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = "velvet_prefs")

class TokenStorage(private val context: Context) {

    companion object {
        private val TOKEN_KEY = stringPreferencesKey("jwt_token")
        private val USER_ID_KEY = stringPreferencesKey("user_id")
        private val EMAIL_KEY = stringPreferencesKey("email")
        private val NAME_KEY = stringPreferencesKey("name")
    }

    suspend fun saveAuthData(token: String, userId: Int, email: String, name: String){
        context.dataStore.edit { prefs ->
            prefs[TOKEN_KEY] = token
            prefs[USER_ID_KEY] = userId.toString()
            prefs[EMAIL_KEY] = email
            prefs[NAME_KEY] = name
        }
    }

    val token: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs [TOKEN_KEY]
    }

    val userId: Flow<Int?> = context.dataStore.data.map { prefs ->
        prefs[USER_ID_KEY]?.toIntOrNull()
    }

    val email: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[EMAIL_KEY]
    }

    val name: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[NAME_KEY]
    }

    suspend fun clear() {
        context.dataStore.edit { it.clear() }
    }

    val isLoggedIn: Flow<Boolean> = token.map { it != null }
}