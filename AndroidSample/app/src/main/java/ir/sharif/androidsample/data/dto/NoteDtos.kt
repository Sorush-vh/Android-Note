package ir.sharif.androidsample.data.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

// Server sends lowercase strings; @Json maps them to upper-case enum constants.
enum class NoteKind {
  @Json(name = "shopping") SHOPPING,
  @Json(name = "ideas")    IDEAS,
  @Json(name = "goals")    GOALS,
  @Json(name = "routine")  ROUTINE
}

@JsonClass(generateAdapter = true)
data class NoteDto(
  val id: String,
  val kind: NoteKind,                 // DTO enum (parsed via @Json)
  val title: String,
  val pinned: Boolean,
  val is_done: Boolean,
  val finished_at: String?,
  val bg_color: String?,              // "#AARRGGBB" or "#RRGGBB"
  val reminder_at: String?,           // ISO8601 or null
  val labels: List<String> = emptyList(),
  val data: Map<String, Any?>? = null,
  val created_at: String,
  val updated_at: String
)

@JsonClass(generateAdapter = true)
data class NoteUpsert(
  val kind: NoteKind,                 // DTO enum (will serialize to lowercase via @Json)
  val title: String,
  val pinned: Boolean = false,
  val is_done: Boolean = false,
  val bg_color: String? = null,
  val reminder_at: String? = null,
  val labels: List<String> = emptyList(),
  val data: Any? = null
)

@JsonClass(generateAdapter = true)
data class ListResponse<T>(
  val value: List<T>,
  val Count: Int
)
