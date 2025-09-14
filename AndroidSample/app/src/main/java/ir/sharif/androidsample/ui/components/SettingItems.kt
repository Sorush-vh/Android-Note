package ir.sharif.androidsample.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun SettingRow(
  icon: ImageVector,
  title: String,
  trailing: @Composable () -> Unit = {},
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val onSurfaceVariant = MaterialTheme.colorScheme.onSurfaceVariant
  Row(
    modifier = modifier
      .fillMaxWidth()
      .heightIn(min = 56.dp)
      .clickable { onClick() }
      .padding(horizontal = 16.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Icon(icon, contentDescription = null, tint = onSurfaceVariant)
    Spacer(Modifier.width(12.dp))
    Text(title, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
    Spacer(Modifier.width(12.dp))
    trailing()
  }
}
