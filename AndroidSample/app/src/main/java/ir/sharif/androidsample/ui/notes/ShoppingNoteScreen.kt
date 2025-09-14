package ir.sharif.androidsample.ui.notes

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ir.sharif.androidsample.data.dto.NoteUpsert
import ir.sharif.androidsample.data.model.ShoppingContent
import ir.sharif.androidsample.di.ServiceLocator
import ir.sharif.androidsample.ui.components.ThinDivider
import ir.sharif.androidsample.ui.util.*
import kotlinx.coroutines.*
import java.time.Instant
import kotlin.random.Random
import ir.sharif.androidsample.data.dto.NoteKind as DtoKind

private const val DEFAULT_TITLE = "New Shopping List"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoppingNoteScreen(
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
  data class Item(val id: Long, val text: String, val done: Boolean)
  val items = remember { mutableStateListOf<Item>() }

  var lastEdited by remember { mutableStateOf(Instant.now()) }
  fun touch() { lastEdited = Instant.now() }

  var showMore by remember { mutableStateOf(false) }
  var showAddLabel by remember { mutableStateOf(false) }
  var showReminder by remember { mutableStateOf(false) }

  // apply server state once
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
        is ShoppingContent -> {
          items.clear()
          items.addAll(c.items.map { Item(it.id, it.text, it.done) })
        }
        else -> Unit
      }
      applied = true
    }
  }

  fun idLive(): String? = state.note?.id ?: noteId
  fun hasMeaningfulContent() = items.any { it.text.isNotBlank() }
  fun allowCreate() = title.trim() != DEFAULT_TITLE && hasMeaningfulContent()
  fun toDataMap() = mapOf("items" to items.map { mapOf("id" to it.id, "text" to it.text, "done" to it.done) })

  // live patch only if id exists
  suspend fun patchIfPossible() {
    val id = idLive() ?: return
    vm.patch(id, mapOf("title" to title, "data" to toDataMap()))
  }

  val debounced = remember {
    object {
      var job: Job? = null
      fun submit(block: suspend () -> Unit) {
        job?.cancel()
        job = scope.launch { delay(250L); runCatching { block() } }
      }
      suspend fun flush(block: suspend () -> Unit) {
        job?.cancelAndJoin()
        runCatching { block() }
      }
    }
  }

  suspend fun saveNow() {
    val id = idLive()
    if (id.isNullOrBlank()) {
      if (allowCreate()) {
        vm.create(
          NoteUpsert(
            kind = DtoKind.SHOPPING,
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
          kind = DtoKind.SHOPPING,
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
        Icon(Icons.Outlined.ShoppingCart, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
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

      Column(
        Modifier.fillMaxWidth().weight(1f, fill = false).verticalScroll(rememberScrollState())
      ) {
        items.forEachIndexed { idx, item ->
          Row(Modifier.fillMaxWidth().padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            TextField(
              value = item.text,
              onValueChange = { txt ->
                items[idx] = item.copy(text = txt); touch()
                debounced.submit { patchIfPossible() }
              },
              singleLine = true,
              placeholder = { Text("Item") },
              modifier = Modifier.weight(1f),
              colors = clearTextFieldColors()
            )
            Spacer(Modifier.width(8.dp))
            Checkbox(
              checked = item.done,
              onCheckedChange = { checked ->
                items[idx] = item.copy(done = checked); touch()
                debounced.submit { patchIfPossible() }
              }
            )
          }
        }

        var draft by remember { mutableStateOf("") }
        TextField(
          value = draft,
          onValueChange = { draft = it },
          placeholder = { Text("Add item…") },
          singleLine = true,
          keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
          keyboardActions = KeyboardActions(onDone = {
            val t = draft.trim()
            if (t.isNotEmpty()) {
              items.add(Item(id = Random.nextLong(), text = t, done = false))
              draft = ""; touch(); debounced.submit { patchIfPossible() }
            }
          }),
          colors = clearTextFieldColors(),
          modifier = Modifier.fillMaxWidth()
        )
      }

      Spacer(Modifier.height(12.dp))
      ThinDivider()

      if (labels.isNotEmpty()) {
        Spacer(Modifier.height(8.dp))
        LabelChipsRow(labels = labels)
      } else {
        Spacer(Modifier.height(8.dp))
      }
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
        title = title.ifBlank { "Shopping list" },
        message = "Grab those items!"
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

