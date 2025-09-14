package ir.sharif.androidsample.ui.notes

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ir.sharif.androidsample.data.dto.NoteUpsert
import ir.sharif.androidsample.data.model.GoalsContent
import ir.sharif.androidsample.di.ServiceLocator
import ir.sharif.androidsample.ui.components.ThinDivider
import ir.sharif.androidsample.ui.util.*
import kotlinx.coroutines.*
import java.time.Instant
import kotlin.random.Random
import ir.sharif.androidsample.data.dto.NoteKind as DtoKind

private const val DEFAULT_TITLE = "Goals"

data class GSubTask(var id: Long, var text: String, var done: Boolean)
data class GTask(var id: Long, var text: String, var done: Boolean, val subtasks: MutableList<GSubTask>)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalsNoteScreen(
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
  val labels = remember { mutableStateListOf<String>() }

  var title by remember { mutableStateOf(DEFAULT_TITLE) }
  val tasks = remember { mutableStateListOf<GTask>() }
  var draftMain by remember { mutableStateOf("") }

  var lastEdited by remember { mutableStateOf(Instant.now()) }
  fun touch() { lastEdited = Instant.now() }

  var showMore by remember { mutableStateOf(false) }
  var showAddLabel by remember { mutableStateOf(false) }
  var showReminder by remember { mutableStateOf(false) }

  var applied by remember { mutableStateOf(false) }
  LaunchedEffect(state.note?.id) {
    val n = state.note ?: return@LaunchedEffect
    if (!applied) {
      title    = n.title.ifBlank { DEFAULT_TITLE }
      pinned   = n.pinned
      finished = n.finished
      labels.clear(); labels.addAll(n.labels)
      bgColor  = colorFromArgbInt(n.colorArgb)
      when (val c = n.content) {
        is GoalsContent -> {
          tasks.clear()
          c.tasks.forEach { t ->
            tasks.add(
              GTask(
                id = t.id, text = t.text, done = t.done,
                subtasks = t.subtasks.map { s -> GSubTask(s.id, s.text, s.done) }.toMutableList()
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
  fun hasMeaningfulContent() =
    tasks.isNotEmpty() || tasks.any { it.text.isNotBlank() || it.subtasks.any { s -> s.text.isNotBlank() } }
  fun allowCreate() = title.trim() != DEFAULT_TITLE && hasMeaningfulContent()

  fun toDataMap() = mapOf(
    "tasks" to tasks.map { t ->
      mapOf(
        "id" to t.id, "text" to t.text, "done" to t.done,
        "subtasks" to t.subtasks.map { s -> mapOf("id" to s.id, "text" to s.text, "done" to s.done) }
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
            kind = DtoKind.GOALS,
            title = title, pinned = pinned, is_done = finished,
            bg_color = colorToHex(bgColor), labels = labels.toList(),
            data = toDataMap()
          )
        )
      }
    } else {
      vm.replace(
        id,
        NoteUpsert(
          kind = DtoKind.GOALS,
          title = title, pinned = pinned, is_done = finished,
          bg_color = colorToHex(bgColor), labels = labels.toList(),
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
        Icon(Icons.Outlined.List, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
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
        tasks.forEachIndexed { i, t ->
          Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = t.done, onCheckedChange = { c -> t.done = c; touch(); debounced.submit { patchIfPossible() } })
            Spacer(Modifier.width(8.dp))
            TextField(
              value = t.text,
              onValueChange = { v -> t.text = v; touch(); debounced.submit { patchIfPossible() } },
              singleLine = true,
              placeholder = { Text("Main task") },
              modifier = Modifier.weight(1f),
              colors = clearTextFieldColors()
            )
            Spacer(Modifier.width(8.dp))
            TextButton(onClick = { tasks.removeAt(i); touch(); debounced.submit { patchIfPossible() } }) { Text("Remove") }
          }

          Spacer(Modifier.height(6.dp))
          t.subtasks.forEachIndexed { j, s ->
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(start = 32.dp)) {
              Checkbox(checked = s.done, onCheckedChange = { c -> s.done = c; touch(); debounced.submit { patchIfPossible() } })
              Spacer(Modifier.width(6.dp))
              TextField(
                value = s.text,
                onValueChange = { v -> s.text = v; touch(); debounced.submit { patchIfPossible() } },
                singleLine = true,
                placeholder = { Text("Subtask") },
                modifier = Modifier.weight(1f),
                colors = clearTextFieldColors()
              )
              Spacer(Modifier.width(8.dp))
              TextButton(onClick = { t.subtasks.removeAt(j); touch(); debounced.submit { patchIfPossible() } }) { Text("Remove") }
            }
          }
          Row(modifier = Modifier.padding(start = 32.dp)) {
            TextButton(onClick = {
              t.subtasks.add(GSubTask(id = Random.nextLong(), text = "", done = false))
              touch(); debounced.submit { patchIfPossible() }
            }) { Text("Add subtask") }
          }
          Spacer(Modifier.height(10.dp))
          ThinDivider()
          Spacer(Modifier.height(10.dp))
        }

        TextField(
          value = draftMain,
          onValueChange = { draftMain = it },
          singleLine = true,
          placeholder = { Text("Add main task…") },
          modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(6.dp))
        TextButton(onClick = {
          val t = draftMain.trim()
          if (t.isNotEmpty()) {
            tasks.add(GTask(id = Random.nextLong(), text = t, done = false, subtasks = mutableListOf()))
            draftMain = ""; touch(); debounced.submit { patchIfPossible() }
          }
        }) { Text("Add task") }
      }

      Spacer(Modifier.height(12.dp))
      ThinDivider()

      if (labels.isNotEmpty()) {
        Spacer(Modifier.height(8.dp))
        LabelChipsRow(labels = labels)
      } else Spacer(Modifier.height(8.dp))
    }
  }

  NoteMoreSheet(
    show = showMore,
    onDismiss = { showMore = false },
    showLabelRow = true,
    bgColor = bgColor,
    onBgColorChange = { c ->
      bgColor = c; touch()
      idLive()?.let { id -> scope.launch { runCatching { vm.patch(id, mapOf("bg_color" to colorToHex(c))) } } }
    },
    onAddLabelClick = { showAddLabel = true },
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

  AddLabelDialog(
    show = showAddLabel,
    onDismiss = { showAddLabel = false },
    onAdd = { lab ->
      if (labels.none { it.equals(lab, true) }) {
        labels.add(lab); touch()
        idLive()?.let { id -> scope.launch { runCatching { vm.patch(id, mapOf("labels" to labels.toList())) } } }
      }
      showAddLabel = false
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
        title = title.ifBlank { "Goal reminder" },
        message = "Time to work on your goal."
      )
    }
  )
}

/* helpers (same as Ideas) */
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

