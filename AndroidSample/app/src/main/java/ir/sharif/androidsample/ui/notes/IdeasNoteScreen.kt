// IdeasNoteScreen.kt
package ir.sharif.androidsample.ui.notes

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ir.sharif.androidsample.data.dto.NoteUpsert
import ir.sharif.androidsample.data.model.IdeasContent
import ir.sharif.androidsample.di.ServiceLocator
import ir.sharif.androidsample.ui.components.ThinDivider
import ir.sharif.androidsample.ui.util.*
import kotlinx.coroutines.*
import java.time.Instant
import ir.sharif.androidsample.data.dto.NoteKind as DtoKind

private const val DEFAULT_TITLE = "Interesting idea"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IdeasNoteScreen(
  noteId: String? = null,
  onBack: () -> Unit,
  onDelete: () -> Unit = {},
  vm: NoteDetailViewModel = viewModel(factory = ServiceLocator.vmFactory())
) {
  val state by vm.state.collectAsState()
  val scope = rememberCoroutineScope()
  val ctx = LocalContext.current

  // load when navigating into an existing note
  LaunchedEffect(noteId) {
    if (!noteId.isNullOrBlank()) vm.load(noteId)   // VM loads note by id
  }

  // UI state
  val surface = MaterialTheme.colorScheme.surface
  var bgColor by remember(surface) { mutableStateOf(surface) }
  var pinned by remember { mutableStateOf(false) }
  var finished by remember { mutableStateOf(false) }
  val labels = remember { mutableStateListOf<String>() }

  var title by remember { mutableStateOf(DEFAULT_TITLE) }
  var body by remember { mutableStateOf("") }

  var lastEdited by remember { mutableStateOf(Instant.now()) }
  fun touch() { lastEdited = Instant.now() }

  var showMore by remember { mutableStateOf(false) }
  var showLabels by remember { mutableStateOf(false) }
  var showReminder by remember { mutableStateOf(false) }

  // apply server state once per note load
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
        is IdeasContent -> body = c.body
        else -> Unit
      }
      applied = true
    }
  }

  // helpers
  fun liveId(): String? = state.note?.id ?: noteId
  fun hasMeaningfulContent() = body.isNotBlank()
  fun allowCreate() = title.trim() != DEFAULT_TITLE && hasMeaningfulContent()
  fun toUpsert(): NoteUpsert = NoteUpsert(
    kind = DtoKind.IDEAS,
    title = title,
    pinned = pinned,
    is_done = finished,
    bg_color = colorToHex(bgColor),
    reminder_at = null,
    labels = labels.toList(),
    data = mapOf("body" to body)
  )

  // debounced patch for edits on existing note
  val debounced = remember {
    object {
      var job: Job? = null
      fun submit(block: suspend () -> Unit) {
        job?.cancel()
        job = scope.launch {
          delay(200)
          runCatching { block() }
        }
      }
      suspend fun flush(block: suspend () -> Unit) {
        job?.cancelAndJoin()
        runCatching { block() }
      }
    }
  }

  suspend fun patchIfPossible() {
    val id = liveId() ?: return
    vm.patch(id, mapOf("title" to title, "data" to mapOf("body" to body)))
  }

  val saveAndExit: () -> Unit = {
    scope.launch {
      // ensure last keystrokes are sent for existing notes
      debounced.flush { patchIfPossible() }
      val id = liveId()
      runCatching {
        if (id.isNullOrBlank()) {
          if (allowCreate()) vm.create(toUpsert()) else Unit
        } else {
          vm.replace(id, toUpsert())
        }
      }
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
          val id = liveId()
          if (!id.isNullOrBlank()) scope.launch {
            runCatching { vm.patch(id, mapOf("pinned" to pinned)) }
          }
        },
        onMore = { showMore = true }
      )
    }
  ) { inner ->
    Column(
      Modifier.fillMaxSize().padding(inner).padding(horizontal = 16.dp)
    ) {
      ThinDivider()
      Spacer(Modifier.height(16.dp))

      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Outlined.Lightbulb, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.width(10.dp))
        TextField(
          value = title,
          onValueChange = {
            title = it; touch()
            // patch title live only if it's an existing note
            val id = liveId()
            if (!id.isNullOrBlank()) debounced.submit { patchIfPossible() }
          },
          singleLine = true,
          placeholder = { Text(DEFAULT_TITLE) },
          colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent
          ),
          modifier = Modifier.fillMaxWidth()
        )
      }

      Spacer(Modifier.height(12.dp))

      TextField(
        value = body,
        onValueChange = {
          body = it; touch()
          val id = liveId()
          if (!id.isNullOrBlank()) debounced.submit { patchIfPossible() }
        },
        minLines = 8,
        placeholder = { Text("Write your idea…") },
        colors = TextFieldDefaults.colors(
          focusedIndicatorColor = Color.Transparent,
          unfocusedIndicatorColor = Color.Transparent,
          disabledIndicatorColor = Color.Transparent,
          focusedContainerColor = Color.Transparent,
          unfocusedContainerColor = Color.Transparent,
          disabledContainerColor = Color.Transparent
        ),
        modifier = Modifier.fillMaxWidth().weight(1f, fill = false)
      )

      Spacer(Modifier.height(12.dp))
      ThinDivider()

      // labels (read-only row you already have)
      if (labels.isNotEmpty()) {
        Spacer(Modifier.height(8.dp))
        LabelChipsRow(labels = labels)
      } else {
        Spacer(Modifier.height(8.dp))
      }
    }
  }

  // More / delete / color / labels / reminder
  NoteMoreSheet(
    show = showMore,
    onDismiss = { showMore = false },
    showLabelRow = true,
    bgColor = bgColor,
    onBgColorChange = { c ->
      bgColor = c; touch()
      val id = liveId()
      if (!id.isNullOrBlank()) scope.launch {
        runCatching { vm.patch(id, mapOf("bg_color" to colorToHex(c))) }
      }
    },
    onAddLabelClick = { showLabels = true },
    onSetReminderClick = { showReminder = true },
    finished = finished,
    onToggleFinished = {
      finished = !finished; touch()
      val id = liveId()
      if (!id.isNullOrBlank()) scope.launch {
        runCatching { vm.patch(id, mapOf("is_done" to finished)) }
      }
    },
    onDelete = {
      val id = liveId() ?: return@NoteMoreSheet
      scope.launch {
        runCatching { vm.delete(id) }
          .onSuccess { onDelete(); onBack() }
      }
    }
  )

  AddLabelDialog(
    show = showLabels,
    onDismiss = { showLabels = false },
    onAdd = { lab ->
      if (labels.none { it.equals(lab, ignoreCase = true) }) {
        labels.add(lab); touch()
        val id = liveId()
        if (!id.isNullOrBlank()) scope.launch {
          runCatching { vm.patch(id, mapOf("labels" to labels.toList())) }
        }
      }
      showLabels = false
    }
  )

  ReminderDialog(
    show = showReminder,
    onDismiss = { showReminder = false },
    onSet = { ms ->
      showReminder = false; touch()
      val id = liveId()
      if (!id.isNullOrBlank()) {
        val iso = isoIn(ms)
        scope.launch { runCatching { vm.patch(id, mapOf("reminder_at" to iso)) } }
      }
      ir.sharif.androidsample.core.notify.ReminderScheduler.schedule(
        context = ctx,
        delayMillis = ms,
        title = title.ifBlank { "Idea" },
        message = "Time to revisit this idea."
      )
    }
  )
}
