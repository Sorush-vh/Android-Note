package ir.sharif.androidsample.ui.notes

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import java.time.Duration
import java.time.Instant
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.DoneAll
import androidx.compose.material.icons.outlined.Label
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material3.*

import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp

/* ------------------ Bottom bar ------------------ */

@Composable
fun NoteDetailBottomBar(
  lastEdited: Instant,
  pinned: Boolean,
  onSearch: () -> Unit,
  onToggleBookmark: () -> Unit,
  onMore: () -> Unit,
) {
  val hint = remember(lastEdited) { "Last edited ${humanize(lastEdited)}" }
  Surface(tonalElevation = 3.dp) {
    Row(
      Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        hint,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.weight(1f)
      )
      IconButton(onClick = onSearch) {
        Icon(Icons.Outlined.Search, contentDescription = "Search")
      }
      IconButton(onClick = onToggleBookmark) {
        if (pinned) {
          Icon(Icons.Outlined.Bookmarks, contentDescription = "Unpin")
        } else {
          Icon(Icons.Outlined.BookmarkAdd, contentDescription = "Pin")
        }
      }
      Surface(shape = CircleShape, color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)) {
        IconButton(onClick = onMore) {
          Icon(Icons.Outlined.MoreVert, contentDescription = "More")
        }
      }
    }
  }
}

/* ------------------ More settings sheet ------------------ */
// NoteDetailBars.kt


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteMoreSheet(
  show: Boolean,
  onDismiss: () -> Unit,
  showLabelRow: Boolean,
  bgColor: Color,
  onBgColorChange: (Color) -> Unit,
  onAddLabelClick: () -> Unit = {},
  onSetReminderClick: () -> Unit = {},
  finished: Boolean,
  onToggleFinished: () -> Unit,
  onDelete: () -> Unit
) {
  if (!show) return
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = MaterialTheme.colorScheme.surface,
    contentColor = MaterialTheme.colorScheme.onSurface,
    tonalElevation = 3.dp,
    scrimColor = MaterialTheme.colorScheme.scrim.copy(alpha = 0.45f),
    dragHandle = { BottomSheetDefaults.DragHandle() }
  ) {
    Column(Modifier.navigationBarsPadding().imePadding().padding(16.dp)) {
      Text("More settings", style = MaterialTheme.typography.titleMedium)
      Spacer(Modifier.height(16.dp))

      // Background color first
      ListItem(
        leadingContent = { Icon(Icons.Outlined.Palette, null) },
        headlineContent = { Text("Background color") },
        supportingContent = { Text("Personalize the note color") }
      )
      FancyColorPaletteRow(selected = bgColor, onSelect = onBgColorChange)
      Divider()

      // Labels
      if (showLabelRow) {
        ListItem(
          leadingContent = { Icon(Icons.Outlined.Label, null) },
          headlineContent = { Text("Add label") },
          supportingContent = { Text("Organize with tags") },
          modifier = Modifier.clickable { onAddLabelClick() }
        )
        Divider()
      }

      // Finished
      ListItem(
        leadingContent = { Icon(Icons.Outlined.DoneAll, null) },
        headlineContent = { Text("Mark as finished") },
        supportingContent = { Text(if (finished) "Currently finished" else "Currently active") },
        trailingContent = { Switch(checked = finished, onCheckedChange = { onToggleFinished() }) }
      )
      Divider()

      // Reminder
      ListItem(
        leadingContent = { Icon(Icons.Outlined.Notifications, null) },
        headlineContent = { Text("Set reminder") },
        supportingContent = { Text("Get a notification later") },
        modifier = Modifier.clickable { onSetReminderClick() }
      )
      Divider()

      // Danger zone — DELETE
      Spacer(Modifier.height(8.dp))
      ListItem(
        leadingContent = { Icon(Icons.Outlined.Delete, null, tint = MaterialTheme.colorScheme.error) },
        headlineContent = { Text("Delete note", color = MaterialTheme.colorScheme.error) },
        supportingContent = { Text("Move to trash", color = MaterialTheme.colorScheme.error) },
        modifier = Modifier.clickable {
          onDelete()
          onDismiss()
        }
      )

      Spacer(Modifier.height(with(LocalDensity.current) { 12.dp }))
    }
  }
}


/* --------------- helpers --------------- */



/* --------------- helpers --------------- */

@Composable
private fun FancyColorPaletteRow(selected: Color, onSelect: (Color) -> Unit) {
  val swatches = listOf(
    Color(0xFFFFF7E0), // warm sand
    Color(0xFFFFE082), // amber 200
    Color(0xFFB2FF59), // lime A200
    Color(0xFF80DEEA), // cyan 200
    Color(0xFFFFAB91), // deep orange 200
    Color(0xFFD1C4E9), // purple 100
    Color(0xFFFFF3E0), // orange 50
    Color(0xFFE0F2F1)  // teal 50
  )
  Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
    swatches.forEach { c ->
      val selectedBorder = if (c == selected) 2.dp else 0.dp
      Box(
        Modifier
          .size(if (c == selected) 28.dp else 24.dp)
          .clip(CircleShape)
          .background(c)
          .border(selectedBorder, MaterialTheme.colorScheme.primary, CircleShape)
          .clickable { onSelect(c) }
      )
    }
  }
}

private fun humanize(whenEdited: java.time.Instant): String {
  val d = java.time.Duration.between(whenEdited, java.time.Instant.now())
  val min = d.toMinutes(); val hr = d.toHours()
  return when {
    hr >= 24 -> "${hr / 24}d ago"
    hr >= 1  -> "${hr}h ago"
    min >= 1 -> "${min}m ago"
    else     -> "just now"
  }
}
