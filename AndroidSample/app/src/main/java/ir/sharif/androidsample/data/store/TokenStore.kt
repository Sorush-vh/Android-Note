package ir.sharif.androidsample.data.store

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore("auth_prefs")

class TokenStore(private val context: Context) {
  companion object {
    private val KEY_ACCESS = stringPreferencesKey("access")
    private val KEY_REFRESH = stringPreferencesKey("refresh")
  }

  suspend fun save(access: String, refresh: String?) {
    context.dataStore.edit { p ->
      p[KEY_ACCESS] = access
      if (refresh != null) p[KEY_REFRESH] = refresh
    }
  }

  suspend fun saveAccess(token: String) {
    context.dataStore.edit { p -> p[KEY_ACCESS] = token }
  }

  suspend fun updateAccess(token: String) = saveAccess(token)

  suspend fun clear() { context.dataStore.edit { it.clear() } }

  suspend fun access(): String? = context.dataStore.data.map { it[KEY_ACCESS] }.first()
  suspend fun refresh(): String? = context.dataStore.data.map { it[KEY_REFRESH] }.first()
}
