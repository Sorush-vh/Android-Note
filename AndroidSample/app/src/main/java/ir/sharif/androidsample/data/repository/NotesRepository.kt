package ir.sharif.androidsample.data.repository

import ir.sharif.androidsample.data.dto.*
import ir.sharif.androidsample.data.model.*
import ir.sharif.androidsample.data.remote.NotesApi
import java.time.Instant
import ir.sharif.androidsample.data.dto.NoteKind as DtoKind
import ir.sharif.androidsample.data.model.NoteKind as ModelKind

// -------- DTO <-> Model enum mappers --------
private fun DtoKind.toModel(): ModelKind = when (this) {
  DtoKind.SHOPPING -> ModelKind.SHOPPING
  DtoKind.IDEAS    -> ModelKind.IDEAS
  DtoKind.GOALS    -> ModelKind.GOALS
  DtoKind.ROUTINE  -> ModelKind.ROUTINE
}

private fun ModelKind.toDto(): DtoKind = when (this) {
  ModelKind.SHOPPING -> DtoKind.SHOPPING
  ModelKind.IDEAS    -> DtoKind.IDEAS
  ModelKind.GOALS    -> DtoKind.GOALS
  ModelKind.ROUTINE  -> DtoKind.ROUTINE
}

class NotesRepository(private val api: NotesApi) {

  // Lists: API now returns RAW lists, not wrapped
  suspend fun pinned(): List<NoteEnvelope>                = api.list(pinned = true).map { it.toEnvelope() }
  suspend fun recent(): List<NoteEnvelope>                = api.recent().map { it.toEnvelope() }
  suspend fun search(q: String): List<NoteEnvelope>       = api.list(search = q).map { it.toEnvelope() }
  suspend fun finished(): List<NoteEnvelope>              = api.finished().map { it.toEnvelope() }

  // Single item ops
  suspend fun get(id: String): NoteEnvelope               = api.get(id).toEnvelope()
  suspend fun create(body: NoteUpsert): NoteEnvelope      = api.create(body).toEnvelope()
  suspend fun replace(id: String, body: NoteUpsert): NoteEnvelope = api.replace(id, body).toEnvelope()
  suspend fun patch(id: String, patch: Map<String, Any?>): NoteEnvelope = api.patch(id, patch).toEnvelope()
  suspend fun delete(id: String)                          = api.delete(id)
}

// -------- DTO -> Model mapper --------
private fun NoteDto.toEnvelope(): NoteEnvelope {
  val kindModel: ModelKind = this.kind.toModel()

  val colorInt = parseHexColorToArgbInt(bg_color) ?: 0xFFFFFFFF.toInt()
  val reminderMs = reminder_at?.let { runCatching { Instant.parse(it).toEpochMilli() }.getOrNull() }
  val updatedMs  = runCatching { Instant.parse(updated_at).toEpochMilli() }.getOrDefault(System.currentTimeMillis())

  val content: NoteContent = when (kindModel) {
    ModelKind.IDEAS -> {
      val body = (data as? Map<*, *>)?.get("body") as? String ?: ""
      IdeasContent(body = body)
    }
    ModelKind.SHOPPING -> {
      val items = ((data as? Map<*, *>)?.get("items") as? List<*>)?.mapNotNull { row ->
        (row as? Map<*, *>)?.let {
          val id = (it["id"] as? Number)?.toLong() ?: return@let null
          val text = it["text"] as? String ?: ""
          val done = it["done"] as? Boolean ?: false
          ShoppingContent.ShoppingItem(id, text, done)
        }
      } ?: emptyList()
      ShoppingContent(items)
    }
    ModelKind.GOALS -> {
      val tasks = ((data as? Map<*, *>)?.get("tasks") as? List<*>)?.mapNotNull { t ->
        (t as? Map<*, *>)?.let {
          val id = (it["id"] as? Number)?.toLong() ?: return@let null
          val text = it["text"] as? String ?: ""
          val done = it["done"] as? Boolean ?: false
          val subs = (it["subtasks"] as? List<*>)?.mapNotNull { s ->
            (s as? Map<*, *>)?.let { sm ->
              val sid   = (sm["id"] as? Number)?.toLong() ?: return@let null
              val stext = sm["text"] as? String ?: ""
              val sdone = sm["done"] as? Boolean ?: false
              GoalsContent.SubTask(sid, stext, sdone)
            }
          } ?: emptyList()
          GoalsContent.MainTask(id, text, done, subs)
        }
      } ?: emptyList()
      GoalsContent(tasks)
    }
    ModelKind.ROUTINE -> {
      val subs = ((data as? Map<*, *>)?.get("subnotes") as? List<*>)?.mapNotNull { rn ->
        (rn as? Map<*, *>)?.let { m ->
          val id    = (m["id"] as? Number)?.toLong() ?: return@let null
          val title = m["title"] as? String ?: ""
          val body  = m["body"]  as? String ?: ""
          val done  = m["done"]  as? Boolean ?: false
          val cHex  = m["colorArgb"] as? String
          val cInt  = parseHexColorToArgbInt(cHex) ?: 0xFFFFFFFF.toInt()
          RoutineContent.SubNote(id, title, body, done, cInt)
        }
      } ?: emptyList()
      RoutineContent(subs)
    }
  }

  return NoteEnvelope(
    id = this.id,
    kind = kindModel,
    title = this.title,
    pinned = this.pinned,
    finished = this.is_done,
    colorArgb = colorInt,
    labels = this.labels,
    lastEditedEpochMs = updatedMs,
    reminderAtEpochMs = reminderMs,
    content = content
  )
}

private fun parseHexColorToArgbInt(hex: String?): Int? {
  val h = hex?.trim()?.removePrefix("#") ?: return null
  val v = when (h.length) {
    6 -> "FF$h" // RRGGBB -> add full alpha
    8 -> h      // AARRGGBB
    else -> return null
  }
  return v.toLong(16).toInt()
}
