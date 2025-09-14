package ir.sharif.androidsample.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable


@Composable
fun AddNoteDialog(
  onConfirm: (title: String, desc: String) -> Unit,
  onDismiss: () -> Unit
) {
  var title by remember { mutableStateOf("") }
  var desc by remember { mutableStateOf("") }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text("New note") },
    text = {
      Column {
        ir.sharif.androidsample.ui.components.LabeledTextField(title, { title = it }, "Title", Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        ir.sharif.androidsample.ui.components.LabeledTextField(desc, { desc = it }, "Description", Modifier.fillMaxWidth(), singleLine = false)
      }
    },
    confirmButton = { TextButton(onClick = { onConfirm(title.trim(), desc.trim()) }) { Text("Add") } },
    dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
  )
}


@Composable
fun ConfirmLogoutDialog(
  show: Boolean,
  onConfirm: () -> Unit,
  onDismiss: () -> Unit
) {
  if (!show) return

  BasicAlertDialog(onDismissRequest = onDismiss) {
    Surface(
      shape = MaterialTheme.shapes.large,
      tonalElevation = 2.dp
    ) {
      Column(
        modifier = Modifier
          .padding(horizontal = 20.dp, vertical = 16.dp)
          .widthIn(min = 280.dp, max = 420.dp)
      ) {
        Text("Log Out", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onSurface)
        Spacer(Modifier.height(8.dp))
        Text(
          "Are you sure you want to log out from the application?",
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(20.dp))

        // BIG, CENTERED, NEARLY FULL-WIDTH BUTTONS
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 4.dp),
          horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          OutlinedButton(
            onClick = onDismiss,
            modifier = Modifier
              .weight(1f)
              .height(52.dp)
          ) {
            Text("Cancel")
          }
          Button(
            onClick = onConfirm,
            modifier = Modifier
              .weight(1f)
              .height(52.dp),
            colors = ButtonDefaults.buttonColors(
              containerColor = MaterialTheme.colorScheme.primary,
              contentColor = MaterialTheme.colorScheme.onPrimary
            )
          ) {
            Text("Yes")
          }
        }
      }
    }
  }
}

