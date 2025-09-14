package ir.sharif.androidsample.ui.notes

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Checklist
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material.icons.outlined.List
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import ir.sharif.androidsample.data.dto.NoteDto
import ir.sharif.androidsample.data.model.*

/** One-row card for NoteEnvelope (new model). */
@Composable
fun NoteRow(
  note: NoteEnvelope,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val kindIcon = when (note.kind) {
    NoteKind.SHOPPING -> Icons.Outlined.ShoppingCart
    NoteKind.IDEAS    -> Icons.Outlined.Lightbulb
    NoteKind.GOALS    -> Icons.Outlined.List
    NoteKind.ROUTINE  -> Icons.Outlined.Checklist
  }

  val cardColor = colorFromArgbInt(note.colorArgb)
  val borderColor = MaterialTheme.colorScheme.primary

  Box(modifier = modifier.fillMaxWidth()) {
    Row(
      Modifier
        .clip(RoundedCornerShape(16.dp))
        .background(cardColor)
        .border(1.5.dp, borderColor, RoundedCornerShape(16.dp))
        .clickable(onClick = onClick)
        .padding(horizontal = 14.dp, vertical = 10.dp)
        .fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Icon(kindIcon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
      Spacer(Modifier.width(12.dp))
      Column(Modifier.weight(1f)) {
        Text(
          note.title.ifBlank { "(untitled)" },
          style = MaterialTheme.typography.titleMedium,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
        val preview = previewText(note)
        if (preview.isNotBlank()) {
          Spacer(Modifier.height(4.dp))
          Text(
            preview,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
        }
      }
    }

    // ⭐ badge (top-right) only if pinned
    if (note.pinned) {
      Row(
        Modifier
          .align(Alignment.TopEnd)
          .padding(top = 4.dp, end = 8.dp)
          .clip(RoundedCornerShape(10.dp))
          .background(MaterialTheme.colorScheme.surface)
          .border(1.dp, borderColor, RoundedCornerShape(10.dp))
          .padding(horizontal = 6.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Icon(Icons.Outlined.Star, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
      }
    }
  }
}

/** Lazy list of NoteEnvelope. Only uses NoteRow. */
@Composable
fun NotesFeed(
  notes: List<NoteEnvelope>,
  onOpen: (NoteEnvelope) -> Unit,
  modifier: Modifier = Modifier
) {
  LazyColumn(modifier) {
    items(notes, key = { it.id ?: it.hashCode() }) { note ->
      NoteRow(
        note = note,
        onClick = { onOpen(note) },
        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
      )
    }
  }
}

/* ---------- internals ---------- */

private fun previewText(note: NoteEnvelope): String = when (val c = note.content) {
  is ShoppingContent -> c.items.take(3).joinToString { it.text }
  is IdeasContent    -> c.body.take(120)
  is GoalsContent    -> buildString {
    val done = c.tasks.count { it.done }
    append("${c.tasks.size} main task(s) • $done done")
  }
  is RoutineContent  -> c.subnotes.take(2).joinToString { it.title }
}

fun colorFromArgbInt(argb: Int): Color {
  val a = ((argb ushr 24) and 0xFF) / 255f
  val r = ((argb ushr 16) and 0xFF) / 255f
  val g = ((argb ushr 8) and 0xFF) / 255f
  val b = (argb and 0xFF) / 255f
  return Color(r, g, b, a)
}

/* ---------- Legacy (NoteDto) – kept to avoid breaking old call sites ---------- */

@Composable
fun NoteCardDto(
  item: NoteDto,
  onToggle: (NoteDto) -> Unit,
  onDelete: (NoteDto) -> Unit,
  modifier: Modifier = Modifier
) {
  Card(modifier = modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp)) {
    Row(Modifier.padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween) {
      Column(Modifier.weight(1f)) {
        Text(
          item.title,
          style = MaterialTheme.typography.titleMedium,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
        Spacer(Modifier.height(4.dp))
        Text(
          item.labels.joinToString(),
          style = MaterialTheme.typography.bodyMedium,
          maxLines = 2,
          overflow = TextOverflow.Ellipsis
        )
      }
      Spacer(Modifier.width(8.dp))
      Column(horizontalAlignment = Alignment.End) {
        Checkbox(checked = item.is_done, onCheckedChange = { onToggle(item) })
        TextButton(onClick = { onDelete(item) }) { Text("Delete") }
      }
    }
  }
}

@Composable
fun NotesListDto(
  items: List<NoteDto>,
  onToggle: (NoteDto) -> Unit,
  onDelete: (NoteDto) -> Unit,
  modifier: Modifier = Modifier
) {
  LazyColumn(modifier) {
    items(items, key = { it.id }) { note ->
      NoteCardDto(
        item = note,
        onToggle = onToggle,
        onDelete = onDelete,
        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
      )
    }
  }
}
