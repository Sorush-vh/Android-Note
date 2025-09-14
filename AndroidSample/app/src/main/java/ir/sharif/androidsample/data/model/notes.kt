package ir.sharif.androidsample.data.model

import com.squareup.moshi.JsonClass

enum class NoteKind { SHOPPING, IDEAS, GOALS, ROUTINE }

@JsonClass(generateAdapter = true)
data class NoteEnvelope(
  val id: String? = null,            // server id (UUID later)
  val kind: NoteKind,
  val title: String,
  val pinned: Boolean = false,
  val finished: Boolean = false,
  val colorArgb: Int = 0xFFFFFFFF.toInt(),  // ARGB persisted as Int
  val labels: List<String> = emptyList(),
  val lastEditedEpochMs: Long = System.currentTimeMillis(),
  val reminderAtEpochMs: Long? = null,      // absolute time for reminder (optional)
  val content: NoteContent
)

// ---- Polymorphic payloads (one of) ----
sealed interface NoteContent

@JsonClass(generateAdapter = true)
data class ShoppingContent(
  val items: List<ShoppingItem> = emptyList()
) : NoteContent {
  @JsonClass(generateAdapter = true)
  data class ShoppingItem(
    val id: Long,
    val text: String,
    val done: Boolean
  )
}

@JsonClass(generateAdapter = true)
data class IdeasContent(
  val body: String = ""
) : NoteContent

@JsonClass(generateAdapter = true)
data class GoalsContent(
  val tasks: List<MainTask> = emptyList()
) : NoteContent {
  @JsonClass(generateAdapter = true)
  data class MainTask(
    val id: Long,
    val text: String,
    val done: Boolean,
    val subtasks: List<SubTask> = emptyList()
  )
  @JsonClass(generateAdapter = true)
  data class SubTask(
    val id: Long,
    val text: String,
    val done: Boolean
  )
}

@JsonClass(generateAdapter = true)
data class RoutineContent(
  val subnotes: List<SubNote> = emptyList()
) : NoteContent {
  @JsonClass(generateAdapter = true)
  data class SubNote(
    val id: Long,
    val title: String,
    val body: String,
    val done: Boolean,
    val colorArgb: Int
  )
}
