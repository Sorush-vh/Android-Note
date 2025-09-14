package ir.sharif.androidsample.ui.notes

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Bolt
import androidx.compose.material.icons.outlined.Checklist
import androidx.compose.material.icons.outlined.ListAlt
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import ir.sharif.androidsample.data.model.NoteKind

@Composable
fun NewNoteScreen(
  onPick: (NoteKind) -> Unit,
  onBack: () -> Unit
) {
  // when showing the 4 note-type cards:
  val ideasColor   = Color(0xFFB39DDB) // purple
  val goalsColor   = Color(0xFFFFCC80) // orange
  val shopColor    = Color(0xFFB2FF59) // light green (leans cyan)
  val routineColor = Color(0xFFFFAB91) // warmer than current red-ish



  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text("New Notes") },
        navigationIcon = {
          TextButton(onClick = onBack) { Text("Back") }
        }
      )
    }
  ) { inner ->
    Column(
      Modifier.fillMaxSize().padding(inner).padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      NotePickCard(
        icon = Icons.Outlined.Bolt,
        title = "Interesting Ideas",
        subtitle = "Use free text area, feel free to write it all",
        containerColor = ideasColor,
        onClick = { onPick(NoteKind.IDEAS) }
      )
      NotePickCard(
        icon = Icons.Outlined.ShoppingCart,
        title = "Buying Something",
        subtitle = "Use checklist, so you won't miss anything",
        containerColor = shopColor,
        onClick = { onPick(NoteKind.SHOPPING) }
      )
      NotePickCard(
        icon = Icons.Outlined.StarOutline,
        title = "Goals",
        subtitle = "Near/future goals, note and keep focus",
        containerColor = goalsColor,
        onClick = { onPick(NoteKind.GOALS) }
      )
      NotePickCard(
        icon = Icons.Outlined.ListAlt,
        title = "Routine Tasks",
        subtitle = "Checklist with sub-checklist",
        containerColor = routineColor,
        onClick = { onPick(NoteKind.ROUTINE) }
      )
    }
  }
}

@Composable
private fun NotePickCard(
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  title: String,
  subtitle: String,
  containerColor: androidx.compose.ui.graphics.Color,
  onClick: () -> Unit
) {
  ElevatedCard(
    onClick = onClick,
    colors = CardDefaults.elevatedCardColors(containerColor = containerColor)
  ) {
    Row(Modifier.fillMaxWidth().padding(16.dp)) {
      Icon(icon, contentDescription = null, modifier = Modifier.size(36.dp))
      Spacer(Modifier.width(12.dp))
      Column(Modifier.weight(1f)) {
        Text(title, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onPrimaryContainer)
        Spacer(Modifier.height(2.dp))
        Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onPrimaryContainer)
      }
    }
  }
}
