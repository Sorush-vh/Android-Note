package ir.sharif.androidsample.ui.components

import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
@Composable
fun PrimaryButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
  Button(onClick = onClick, modifier = modifier) { Text(text) }
}

@Composable
fun SecondaryButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
  OutlinedButton(onClick = onClick, modifier = modifier) { Text(text) }
}

@Composable
fun FullWidthTonalButton(
  text: String,
  icon: ImageVector? = null,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  FilledTonalButton(
    onClick = onClick,
    modifier = modifier
      .padding(horizontal = 16.dp)
      .fillMaxWidth(),
    shape = MaterialTheme.shapes.medium
  ) {
    if (icon != null) {
      Icon(icon, contentDescription = null, modifier = Modifier.padding(end = 8.dp))
    }
    Text(text)
  }
}
