package ir.sharif.androidsample.ui.notes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.text.KeyboardOptions

@Composable
fun AddLabelDialog(
  show: Boolean,
  onDismiss: () -> Unit,
  onAdd: (String) -> Unit
) {
  if (!show) return
  var text by remember { mutableStateOf("") }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text("Add the label") },
    text = {
      OutlinedTextField(
        value = text,
        onValueChange = { text = it },
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text("Label") },
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
      )
    },
    confirmButton = {
      Button(onClick = {
        val v = text.trim()
        if (v.isNotEmpty()) onAdd(v)
        onDismiss()
      }) { Text("Add") }
    },
    dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
  )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun LabelChipsRow(
  labels: List<String>,
  modifier: Modifier = Modifier
) {
  if (labels.isEmpty()) return
  FlowRow(
    modifier = modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.spacedBy(8.dp),
    verticalArrangement = Arrangement.spacedBy(8.dp)
  ) {
    labels.forEach { lab ->
      AssistChip(
        onClick = { /* later: filter/edit */ },
        label = { Text(lab, style = MaterialTheme.typography.labelMedium) },
        colors = AssistChipDefaults.assistChipColors(
          containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
      )
    }
  }
}
