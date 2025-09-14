package ir.sharif.androidsample.util

import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import retrofit2.HttpException
import java.io.IOException

/** Turn exceptions (esp. Retrofit HttpException from DRF) into user-friendly text. */
fun Throwable.toUserMessage(moshi: Moshi = Moshi.Builder().build()): String {
  return when (this) {
    is HttpException -> {
      val code = code()
      val raw = response()?.errorBody()?.string().orEmpty()
      val parsed = parseDrfErrorBody(raw, moshi)
      parsed ?: when (code) {
        400 -> "The request was invalid."
        401 -> "You’re not authorized. Please log in again."
        403 -> "You don’t have permission to do this."
        404 -> "Not found."
        409 -> "Conflict."
        else -> "HTTP $code error."
      }
    }
    is IOException -> "Network error. Check your connection."
    else -> this.message ?: "Something went wrong."
  }
}

private fun parseDrfErrorBody(raw: String, moshi: Moshi): String? {
  if (raw.isBlank()) return null
  return try {
    // DRF typically returns: {"field":["msg1","msg2"]} OR {"detail":"..."}
    val mapType = Types.newParameterizedType(Map::class.java, String::class.java, Any::class.java)
    val map = moshi.adapter<Map<String, Any>>(mapType).fromJson(raw) ?: return null

    // 1) detail
    (map["detail"] as? String)
      ?: run {
        // 2) first field error
        val (key, value) = map.entries.firstOrNull() ?: return null
        val text = when (value) {
          is List<*> -> value.filterIsInstance<String>().joinToString("; ")
          is String -> value
          else -> value?.toString()
        } ?: return null

        // Normalize common fields
        when (key.lowercase()) {
          "username" -> "Username: $text"
          "email"    -> "Email: $text"
          "password" -> "Password: $text"
          else       -> text
        }
      }
  } catch (_: Exception) { null }
}
