package ir.sharif.androidsample.data.store

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.Flow

private val Context.dataStore by preferencesDataStore("app_prefs")

enum class TextSize { Small, Medium, Large }

class PrefsStore(private val ctx: Context) {
  private val KEY_TEXT = stringPreferencesKey("text_size")
  private val KEY_EMAIL = booleanPreferencesKey("notif_email")
  private val KEY_PUSH = booleanPreferencesKey("notif_push")

  val textSize: Flow<TextSize> = ctx.dataStore.data.map {
    when (it[KEY_TEXT]) {
      "Small" -> TextSize.Small
      "Large" -> TextSize.Large
      else -> TextSize.Medium
    }
  }
  suspend fun setTextSize(size: TextSize) {
    ctx.dataStore.edit { it[KEY_TEXT] = size.name }
  }

  val emailNotifs: Flow<Boolean> = ctx.dataStore.data.map { it[KEY_EMAIL] ?: true }
  suspend fun setEmailNotifs(on: Boolean) { ctx.dataStore.edit { it[KEY_EMAIL] = on } }

  val pushNotifs: Flow<Boolean> = ctx.dataStore.data.map { it[KEY_PUSH] ?: true }
  suspend fun setPushNotifs(on: Boolean) { ctx.dataStore.edit { it[KEY_PUSH] = on } }
}
