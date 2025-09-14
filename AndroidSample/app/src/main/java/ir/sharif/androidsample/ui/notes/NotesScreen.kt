package ir.sharif.androidsample.ui.notes

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import ir.sharif.androidsample.data.model.NoteEnvelope
import ir.sharif.androidsample.di.ServiceLocator

@Composable
fun NotesScreen(
  onOpenNote: (NoteEnvelope) -> Unit,
  vm: NotesViewModel = viewModel(factory = ServiceLocator.vmFactory())
) {
  val ui by vm.home.collectAsState()
  LaunchedEffect(Unit) { vm.loadHome() }

  // refresh on resume (keeps lists up to date after edits)
  val owner = LocalLifecycleOwner.current
  DisposableEffect(owner) {
    val obs = LifecycleEventObserver { _, e -> if (e == Lifecycle.Event.ON_RESUME) vm.loadHome() }
    owner.lifecycle.addObserver(obs); onDispose { owner.lifecycle.removeObserver(obs) }
  }

  var pinnedVisible by rememberSaveable { mutableStateOf(3) }
  var recentVisible by rememberSaveable { mutableStateOf(3) }

  Column(Modifier.fillMaxSize().padding(horizontal = 12.dp)) {
    if (ui.loading) {
      LinearProgressIndicator(Modifier.fillMaxWidth())
      Spacer(Modifier.height(8.dp))
    }

    // Pinned (top half)
    Column(Modifier.fillMaxWidth().weight(1f)) {
      Text("Pinned", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(vertical = 8.dp))
      if (ui.pinned.isEmpty()) {
        Text("No pinned notes yet.", color = MaterialTheme.colorScheme.onSurfaceVariant)
      } else {
        LazyColumn {
          items(ui.pinned.take(pinnedVisible), key = { it.id ?: it.hashCode() }) { note ->
            NoteRow(note = note, onClick = { onOpenNote(note) }, modifier = Modifier.padding(vertical = 6.dp))
          }
          if (pinnedVisible < ui.pinned.size) {
            item {
              TextButton(onClick = { pinnedVisible += 3 }) { Text("Show more (${ui.pinned.size - pinnedVisible})") }
            }
          }
        }
      }
    }

    Divider(Modifier.padding(vertical = 6.dp))

    // Recent (bottom half)
    Column(Modifier.fillMaxWidth().weight(1f)) {
      Text("Recent", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(vertical = 8.dp))
      if (ui.recent.isEmpty()) {
        Text("No recent notes yet.", color = MaterialTheme.colorScheme.onSurfaceVariant)
      } else {
        LazyColumn {
          items(ui.recent.take(recentVisible), key = { it.id ?: it.hashCode() }) { note ->
            NoteRow(note = note, onClick = { onOpenNote(note) }, modifier = Modifier.padding(vertical = 6.dp))
          }
          if (recentVisible < ui.recent.size) {
            item {
              TextButton(onClick = { recentVisible += 3 }) { Text("Show more (${ui.recent.size - recentVisible})") }
            }
          }
        }
      }
    }

    ui.error?.let { Spacer(Modifier.height(8.dp)); Text(it, color = MaterialTheme.colorScheme.error) }
  }
}
