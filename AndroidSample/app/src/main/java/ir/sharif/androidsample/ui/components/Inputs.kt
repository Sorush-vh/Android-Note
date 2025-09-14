package ir.sharif.androidsample.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.material3.LocalContentColor
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign


@Composable
fun LabeledTextField(
  value: String,
  onValueChange: (String) -> Unit,
  label: String,
  modifier: Modifier = Modifier,
  singleLine: Boolean = true,
  keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
  trailingIcon: (@Composable () -> Unit)? = null,

  // ✨ New (optional) error props:
  isError: Boolean = false,
  supportingText: String? = null,
) {
  OutlinedTextField(
    value = value,
    onValueChange = onValueChange,
    label = { Text(label) },
    singleLine = singleLine,
    keyboardOptions = keyboardOptions,
    trailingIcon = trailingIcon,
    isError = isError,
    supportingText = {
      if (supportingText != null) {
        val color = if (isError) MaterialTheme.colorScheme.error else LocalContentColor.current
        Text(
          text = supportingText,
          color = color,
          style = MaterialTheme.typography.labelSmall,
          modifier = Modifier.padding(top = 2.dp)
        )
      }
    },
    modifier = modifier
  )
}
@Composable
fun PasswordField(
  value: String,
  onValueChange: (String) -> Unit,
  modifier: Modifier = Modifier,
  label: String = "Password",
  isError: Boolean = false,
  supportingText: String? = null,
) {
  var visible by remember { mutableStateOf(false) }
  OutlinedTextField(
    value = value,
    onValueChange = onValueChange,
    label = { Text(label) },
    singleLine = true,
    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
    trailingIcon = {
      IconButton(onClick = { visible = !visible }) {
        Icon(
          imageVector = if (visible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
          contentDescription = if (visible) "Hide password" else "Show password"
        )
      }
    },
    visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
    isError = isError,
    supportingText = {
      supportingText?.let {
        val color = if (isError) MaterialTheme.colorScheme.error else LocalContentColor.current
        Text(it, color = color, style = MaterialTheme.typography.labelSmall)
      }
    },
    modifier = modifier
  )
}

@Composable
fun LabeledEditField(
  label: String,
  value: String,
  onValueChange: (String) -> Unit,
  helper: String? = null
) {
  Column(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp)) {
    Text(label, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurface)
    Spacer(Modifier.height(6.dp))
    OutlinedTextField(
      value = value,
      onValueChange = onValueChange,
      singleLine = true,
      modifier = Modifier.fillMaxWidth()
    )
    if (!helper.isNullOrBlank()) {
      Spacer(Modifier.height(6.dp))
      Text(
        helper,
        style = MaterialTheme.typography.bodySmall, // ← fixed
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Start
      )
    }
  }
}

