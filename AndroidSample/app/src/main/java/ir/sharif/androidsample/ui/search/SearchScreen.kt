package ir.sharif.androidsample.ui.search

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ir.sharif.androidsample.data.model.NoteEnvelope
import ir.sharif.androidsample.di.ServiceLocator
import ir.sharif.androidsample.ui.notes.NoteRow
import ir.sharif.androidsample.ui.notes.NotesViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SearchScreen(
  onOpenNote: (NoteEnvelope) -> Unit,
  vm: NotesViewModel = viewModel(factory = ServiceLocator.vmFactory())
) {
  var query by remember { mutableStateOf("") }
  var results by remember { mutableStateOf<List<NoteEnvelope>>(emptyList()) }
  var loading by remember { mutableStateOf(false) }
  var error by remember { mutableStateOf<String?>(null) }
  val scope = rememberCoroutineScope()

  // “Recent” source when query is blank
  val home by vm.home.collectAsState()
  LaunchedEffect(Unit) { vm.loadHome() }
  var recentVisible by rememberSaveable { mutableStateOf(3) }

  var searchJob by remember { mutableStateOf<Job?>(null) }
  fun doSearch(q: String) {
    searchJob?.cancel()
    if (q.isBlank()) { results = emptyList(); loading = false; error = null; return }
    searchJob = scope.launch {
      delay(180)
      loading = true; error = null
      runCatching { vm.search(q) }
        .onSuccess { list -> results = list.sortedByDescending { it.lastEditedEpochMs } }
        .onFailure { e -> error = e.message }
      loading = false
    }
  }
  LaunchedEffect(query) { doSearch(query) }

  Column(Modifier.fillMaxSize().padding(16.dp)) {
    OutlinedTextField(
      value = query,
      onValueChange = { query = it },
      modifier = Modifier.fillMaxWidth(),
      label = { Text("Search your notes") },
      singleLine = true,
      keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
      keyboardActions = KeyboardActions(onSearch = { doSearch(query) })
    )

    if (loading) { Spacer(Modifier.height(12.dp)); LinearProgressIndicator(Modifier.fillMaxWidth()) }
    error?.let { Spacer(Modifier.height(12.dp)); Text(it, color = MaterialTheme.colorScheme.error) }

    Spacer(Modifier.height(16.dp))

    if (query.isBlank()) {
      Text("Recent", style = MaterialTheme.typography.titleMedium)
      Spacer(Modifier.height(8.dp))
      val recent = remember(home.recent) { home.recent }
      if (recent.isEmpty()) {
        Text("No recent notes.", color = MaterialTheme.colorScheme.onSurfaceVariant)
      } else {
        LazyColumn {
          items(recent.take(recentVisible), key = { it.id ?: it.hashCode() }) { note ->
            NoteRow(note = note, onClick = { onOpenNote(note) }, modifier = Modifier.padding(vertical = 6.dp))
          }
          if (recentVisible < recent.size) {
            item { TextButton(onClick = { recentVisible += 3 }) { Text("Show more (${recent.size - recentVisible})") } }
          }
        }
      }
    } else {
      if (results.isEmpty() && !loading && error == null) {
        Text("No results for “$query”.", color = MaterialTheme.colorScheme.onSurfaceVariant)
      } else {
        LazyColumn {
          items(results, key = { it.id ?: it.hashCode() }) { note ->
            NoteRow(note = note, onClick = { onOpenNote(note) }, modifier = Modifier.padding(vertical = 6.dp))
          }
        }
      }
    }
  }
}
