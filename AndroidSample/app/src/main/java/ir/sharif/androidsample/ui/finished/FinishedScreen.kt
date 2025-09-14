package ir.sharif.androidsample.ui.finished

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ir.sharif.androidsample.data.model.NoteEnvelope
import ir.sharif.androidsample.di.ServiceLocator
import ir.sharif.androidsample.ui.notes.NoteRow
import ir.sharif.androidsample.ui.notes.NotesViewModel

@Composable
fun FinishedScreen(
  onOpenNote: (NoteEnvelope) -> Unit,
  vm: NotesViewModel = viewModel(factory = ServiceLocator.vmFactory())
) {
  var loading by remember { mutableStateOf(true) }
  var error by remember { mutableStateOf<String?>(null) }
  var items by remember { mutableStateOf<List<NoteEnvelope>>(emptyList()) }
  var visible by rememberSaveable { mutableStateOf(3) }

  LaunchedEffect(Unit) {
    loading = true; error = null
    runCatching { vm.finished() }
      .onSuccess { list -> items = list.sortedByDescending { it.lastEditedEpochMs } }
      .onFailure { e -> error = e.message ?: "Failed to load finished notes" }
    loading = false
  }

  Column(Modifier.fillMaxSize().padding(16.dp)) {
    Text("Finished", style = MaterialTheme.typography.headlineSmall)
    Spacer(Modifier.height(12.dp))

    if (loading) { LinearProgressIndicator(Modifier.fillMaxWidth()); Spacer(Modifier.height(12.dp)) }
    error?.let { Text(it, color = MaterialTheme.colorScheme.error); Spacer(Modifier.height(12.dp)) }

    if (!loading && items.isEmpty() && error == null) {
      Text("Your completed tasks will appear here.", color = MaterialTheme.colorScheme.onSurfaceVariant)
    } else {
      LazyColumn {
        items(items.take(visible), key = { it.id ?: it.hashCode() }) { note ->
          NoteRow(note = note, onClick = { onOpenNote(note) }, modifier = Modifier.padding(vertical = 6.dp))
        }
        if (visible < items.size) {
          item { TextButton(onClick = { visible += 3 }) { Text("Show more (${items.size - visible})") } }
        }
      }
    }
  }
}
