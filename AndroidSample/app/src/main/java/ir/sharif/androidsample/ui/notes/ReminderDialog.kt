package ir.sharif.androidsample.ui.notes

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ReminderDialog(
  show: Boolean,
  onDismiss: () -> Unit,
  onSet: (delayMillis: Long) -> Unit
) {
  if (!show) return

  var hours by remember { mutableStateOf("0") }
  var mins  by remember { mutableStateOf("0") }
  var secs  by remember { mutableStateOf("0") }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text("Set Reminder") },
    text = {
      Column {
        Text("When should we remind you? (HH:MM:SS)")
        Spacer(Modifier.height(10.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          OutlinedTextField(value = hours, onValueChange = { hours = it.filter { ch -> ch.isDigit() } }, label = { Text("HH") }, singleLine = true, modifier = Modifier.width(80.dp))
          OutlinedTextField(value = mins,  onValueChange = { mins  = it.filter { ch -> ch.isDigit() } }, label = { Text("MM") }, singleLine = true, modifier = Modifier.width(80.dp))
          OutlinedTextField(value = secs,  onValueChange = { secs  = it.filter { ch -> ch.isDigit() } }, label = { Text("SS") }, singleLine = true, modifier = Modifier.width(80.dp))
        }
      }
    },
    confirmButton = {
      Button(onClick = {
        val h = hours.toLongOrNull() ?: 0L
        val m = mins.toLongOrNull() ?: 0L
        val s = secs.toLongOrNull() ?: 0L
        val delayMs = ((h * 3600) + (m * 60) + s) * 1000L
        onSet(delayMs)
      }) { Text("Set") }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) { Text("Cancel") }
    }
  )
}
