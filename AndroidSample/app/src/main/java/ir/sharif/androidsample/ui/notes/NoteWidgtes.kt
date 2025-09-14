package ir.sharif.androidsample.ui.notes

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Checklist
import androidx.compose.material.icons.outlined.Grade
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material.icons.outlined.List
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import ir.sharif.androidsample.data.dto.NoteDto
import ir.sharif.androidsample.data.model.*

/* ---------------------- New model (NoteEnvelope) ---------------------- */

/** Full-width card row with colored background, purple border, and pinned star badge. */
@Composable
fun NoteRow(
  note: NoteEnvelope,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val containerColor =
    if (note.colorArgb != 0) colorFromArgbInt(note.colorArgb)
    else MaterialTheme.colorScheme.surface

  Card(
    onClick = onClick,
    shape = RoundedCornerShape(18.dp),
    colors = CardDefaults.cardColors(containerColor = containerColor),
    modifier = modifier
      .fillMaxWidth()
      .border(width = 1.5.dp, color = MaterialTheme.colorScheme.primary, shape = RoundedCornerShape(18.dp))
  ) {
    Box {
      Row(
        Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        val typeIcon = when (note.kind) {
          NoteKind.SHOPPING -> Icons.Outlined.ShoppingCart
          NoteKind.IDEAS    -> Icons.Outlined.Lightbulb
          NoteKind.GOALS    -> Icons.Outlined.Grade
          NoteKind.ROUTINE  -> Icons.Outlined.Checklist
        }
        Icon(typeIcon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.width(12.dp))

        Column(Modifier.weight(1f)) {
          Text(
            text = note.title.ifBlank { "(untitled)" },
            style = MaterialTheme.typography.titleMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
          val preview = previewText(note)
          if (preview.isNotBlank()) {
            Spacer(Modifier.height(4.dp))
            Text(
              text = preview,
              style = MaterialTheme.typography.bodyMedium,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              maxLines = 2,
              overflow = TextOverflow.Ellipsis
            )
          }
          if (note.labels.isNotEmpty()) {
            Spacer(Modifier.height(8.dp))
            PurpleLabelChips(labels = note.labels)
          }
        }
      }

      // Pinned star badge (only if pinned = true)
      if (note.pinned) {
        Box(
          Modifier
            .align(Alignment.TopEnd)
            .padding(6.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f))
        ) {
          Icon(
            Icons.Outlined.Star,
            contentDescription = "Pinned",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
          )
        }
      }
    }
  }
}

@Composable
fun NotesFeed(
  notes: List<NoteEnvelope>,
  onOpen: (NoteEnvelope) -> Unit,
  modifier: Modifier = Modifier
) {
  LazyColumn(modifier) {
    items(notes, key = { it.id ?: it.hashCode().toString() }) { note ->
      NoteRow(
        note = note,
        onClick = { onOpen(note) },
        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
      )
    }
  }
}

/** Short preview for the list. */
private fun previewText(note: NoteEnvelope): String = when (val c = note.content) {
  is ShoppingContent -> c.items.take(3).joinToString { it.text }
  is IdeasContent    -> c.body.take(120)
  is GoalsContent    -> buildString {
    val done = c.tasks.count { it.done }
    append("${c.tasks.size} task(s) • $done done")
  }
  is RoutineContent  -> c.subnotes.take(2).joinToString { it.title }
  else -> ""
}

/** Convert packed ARGB Int (0xAARRGGBB) to Compose Color safely. */
fun colorFromArgbInt(argb: Int): Color {
  val a = ((argb ushr 24) and 0xFF) / 255f
  val r = ((argb ushr 16) and 0xFF) / 255f
  val g = ((argb ushr 8) and 0xFF) / 255f
  val b = (argb and 0xFF) / 255f
  return Color(r, g, b, a)
}

/* ----------- Purple label chips with extra padding (universal style) ----------- */

@Composable
fun PurpleLabelChips(labels: List<String>) {
  Row(
    Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.spacedBy(8.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    labels.take(4).forEach { lab ->
      val shape = RoundedCornerShape(50) // pill
      AssistChip(
        onClick = { /* noop */ },
        label = {
          Text(
            lab,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = 2.dp)
          )
        },
        modifier = Modifier
          .clip(shape)
          ,
        shape = shape,
        colors = AssistChipDefaults.assistChipColors(
          containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.14f),
          labelColor = MaterialTheme.colorScheme.primary
        )
      )
    }
  }
}


/* ---------------------- Legacy (NoteDto) kept intact ---------------------- */

@Composable
fun NoteCardDto(
  item: NoteDto,
  onToggle: (NoteDto) -> Unit,
  onDelete: (NoteDto) -> Unit,
  modifier: Modifier = Modifier
) {
  Card(modifier = modifier.fillMaxWidth()) {
    Row(Modifier.padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween) {
      Column(Modifier.weight(1f)) {
        Text(
          item.title,
          style = MaterialTheme.typography.titleMedium,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
        Spacer(Modifier.height(4.dp))
        PurpleLabelChips(labels = item.labels) // unified purple labels here too
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
