package ir.sharif.androidsample.ui.notes

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Checklist
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ir.sharif.androidsample.data.dto.NoteUpsert
import ir.sharif.androidsample.data.model.RoutineContent
import ir.sharif.androidsample.di.ServiceLocator
import ir.sharif.androidsample.ui.components.ThinDivider
import ir.sharif.androidsample.ui.util.*
import kotlinx.coroutines.*
import java.time.Instant
import kotlin.random.Random
import ir.sharif.androidsample.data.dto.NoteKind as DtoKind

private const val DEFAULT_TITLE = "Routine"

data class RSubNote(var id: Long, var title: String, var body: String, var done: Boolean, var color: Color)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoutineNoteScreen(
  noteId: String? = null,
  onBack: () -> Unit,
  onDelete: () -> Unit = {},
  vm: NoteDetailViewModel = viewModel(factory = ServiceLocator.vmFactory())
) {
  val state by vm.state.collectAsState()
  LaunchedEffect(noteId) { if (!noteId.isNullOrBlank()) vm.load(noteId) }
  val scope = rememberCoroutineScope()

  val surface = MaterialTheme.colorScheme.surface
  var bgColor by remember(surface) { mutableStateOf(surface) }
  var pinned by remember { mutableStateOf(false) }
  var finished by remember { mutableStateOf(false) }

  var title by remember { mutableStateOf(DEFAULT_TITLE) }
  val subnotes = remember { mutableStateListOf<RSubNote>() }

  var lastEdited by remember { mutableStateOf(Instant.now()) }
  fun touch() { lastEdited = Instant.now() }

  var showMore by remember { mutableStateOf(false) }
  var showReminder by remember { mutableStateOf(false) }

  val defaultSubnoteColor = MaterialTheme.colorScheme.secondaryContainer

  var applied by remember { mutableStateOf(false) }
  LaunchedEffect(state.note?.id) {
    val n = state.note ?: return@LaunchedEffect
    if (!applied) {
      title    = n.title.ifBlank { DEFAULT_TITLE }
      pinned   = n.pinned
      finished = n.finished
      bgColor  = colorFromArgbInt(n.colorArgb)
      when (val c = n.content) {
        is RoutineContent -> {
          subnotes.clear()
          c.subnotes.forEach { sn ->
            subnotes.add(
              RSubNote(
                id = sn.id,
                title = sn.title,
                body = sn.body,
                done = sn.done,
                color = colorFromArgbInt(sn.colorArgb)
              )
            )
          }
        }
        else -> Unit
      }
      applied = true
    }
  }

  fun idLive(): String? = state.note?.id ?: noteId
  fun hasMeaningfulContent() = subnotes.isNotEmpty() || subnotes.any { it.title.isNotBlank() || it.body.isNotBlank() }
  fun allowCreate() = title.trim() != DEFAULT_TITLE && hasMeaningfulContent()

  fun toDataMap() = mapOf(
    "subnotes" to subnotes.map {
      mapOf(
        "id" to it.id,
        "title" to it.title,
        "body" to it.body,
        "done" to it.done,
        // store as note-level hex (backend already expects hex strings elsewhere)
        "color" to colorToHex(it.color)
      )
    }
  )

  suspend fun patchIfPossible() {
    val id = idLive() ?: return
    vm.patch(id, mapOf("title" to title, "data" to toDataMap()))
  }

  val debounced = remember {
    object { var job: Job? = null
      fun submit(block: suspend () -> Unit) { job?.cancel(); job = scope.launch { delay(250L); runCatching { block() } } }
      suspend fun flush(block: suspend () -> Unit) { job?.cancelAndJoin(); runCatching { block() } }
    }
  }

  suspend fun saveNow() {
    val id = idLive()
    if (id.isNullOrBlank()) {
      if (allowCreate()) {
        vm.create(
          NoteUpsert(
            kind = DtoKind.ROUTINE,
            title = title, pinned = pinned, is_done = finished,
            bg_color = colorToHex(bgColor), labels = emptyList(),
            data = toDataMap()
          )
        )
      }
    } else {
      vm.replace(
        id,
        NoteUpsert(
          kind = DtoKind.ROUTINE,
          title = title, pinned = pinned, is_done = finished,
          bg_color = colorToHex(bgColor), labels = emptyList(),
          data = toDataMap()
        )
      )
    }
  }

  val saveAndExit: () -> Unit = {
    scope.launch {
      debounced.flush { patchIfPossible() }
      runCatching { saveNow() }
      onBack()
    }
  }

  Scaffold(
    containerColor = bgColor,
    topBar = {
      TopAppBar(
        title = {},
        navigationIcon = {
          TextButton(onClick = saveAndExit) {
            Icon(Icons.Outlined.ArrowBack, contentDescription = null)
            Spacer(Modifier.width(6.dp))
            Text("Back", color = MaterialTheme.colorScheme.primary)
          }
        }
      )
    },
    bottomBar = {
      NoteDetailBottomBar(
        lastEdited = lastEdited,
        pinned = pinned,
        onSearch = { saveAndExit() },
        onToggleBookmark = {
          pinned = !pinned; touch()
          idLive()?.let { id -> scope.launch { runCatching { vm.patch(id, mapOf("pinned" to pinned)) } } }
        },
        onMore = { showMore = true }
      )
    }
  ) { inner ->
    Column(Modifier.fillMaxSize().padding(inner).padding(horizontal = 16.dp)) {
      ThinDivider()
      Spacer(Modifier.height(16.dp))

      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Outlined.Checklist, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.width(10.dp))
        TextField(
          value = title,
          onValueChange = { title = it; touch(); debounced.submit { patchIfPossible() } },
          singleLine = true,
          placeholder = { Text(DEFAULT_TITLE) },
          colors = clearTextFieldColors(),
          modifier = Modifier.fillMaxWidth()
        )
      }

      Spacer(Modifier.height(12.dp))
      Column(Modifier.fillMaxWidth().weight(1f, false).verticalScroll(rememberScrollState())) {
        subnotes.forEachIndexed { i, s ->
          Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = s.done, onCheckedChange = { c -> s.done = c; touch(); debounced.submit { patchIfPossible() } })
            Spacer(Modifier.width(8.dp))
            Column(Modifier.weight(1f)) {
              TextField(
                value = s.title, onValueChange = { v -> s.title = v; touch(); debounced.submit { patchIfPossible() } },
                singleLine = true, placeholder = { Text("Title") },
                colors = clearTextFieldColors()
              )
              Spacer(Modifier.height(4.dp))
              TextField(
                value = s.body, onValueChange = { v -> s.body = v; touch(); debounced.submit { patchIfPossible() } },
                minLines = 2, placeholder = { Text("Description") }
              )
            }
            Spacer(Modifier.width(8.dp))
            TextButton(onClick = { subnotes.removeAt(i); touch(); debounced.submit { patchIfPossible() } }) { Text("Remove") }
          }
          Spacer(Modifier.height(10.dp))
          ThinDivider()
          Spacer(Modifier.height(10.dp))
        }
        TextButton(onClick = {
          subnotes.add(RSubNote(Random.nextLong(), "", "", false, defaultSubnoteColor))
          touch(); debounced.submit { patchIfPossible() }
        }) { Text("Add subnote") }
      }

      Spacer(Modifier.height(12.dp))
      ThinDivider()
    }
  }

  NoteMoreSheet(
    show = showMore,
    onDismiss = { showMore = false },
    showLabelRow = false,
    bgColor = bgColor,
    onBgColorChange = { c ->
      bgColor = c; touch()
      idLive()?.let { id -> scope.launch { runCatching { vm.patch(id, mapOf("bg_color" to colorToHex(c))) } } }
    },
    onSetReminderClick = { showReminder = true },
    finished = finished,
    onToggleFinished = {
      val id = idLive()
      finished = !finished; touch()
      if (!id.isNullOrBlank()) scope.launch { runCatching { vm.patch(id, mapOf("is_done" to finished)) } }
    },
    onDelete = {
      idLive()?.let { id ->
        scope.launch { runCatching { vm.delete(id) }.onSuccess { onDelete(); onBack() } }
      }
    }
  )

  val ctx = LocalContext.current
  ReminderDialog(
    show = showReminder,
    onDismiss = { showReminder = false },
    onSet = { ms ->
      showReminder = false; touch()
      idLive()?.let { id ->
        val iso = isoIn(ms)
        scope.launch { runCatching { vm.patch(id, mapOf("reminder_at" to iso)) } }
      }
      ir.sharif.androidsample.core.notify.ReminderScheduler.schedule(
        context = ctx,
        delayMillis = ms,
        title = title.ifBlank { "Routine reminder" },
        message = "Time to go through your routine."
      )
    }
  )
}

/* helpers */
@Composable
private fun clearTextFieldColors() = TextFieldDefaults.colors(
  focusedIndicatorColor = Color.Transparent,
  unfocusedIndicatorColor = Color.Transparent,
  disabledIndicatorColor = Color.Transparent,
  focusedContainerColor = Color.Transparent,
  unfocusedContainerColor = Color.Transparent,
  disabledContainerColor = Color.Transparent
)
private fun colorToHex(c: Color): String {
  val a = (c.alpha * 255).toInt().coerceIn(0,255)
  val r = (c.red   * 255).toInt().coerceIn(0,255)
  val g = (c.green * 255).toInt().coerceIn(0,255)
  val b = (c.blue  * 255).toInt().coerceIn(0,255)
  return "#%02X%02X%02X%02X".format(a, r, g, b)
}

