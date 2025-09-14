package ir.sharif.androidsample.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun ThinDivider(modifier: Modifier = Modifier) {
  HorizontalDivider(
    modifier = modifier,
    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
  )
}

@Composable
fun SectionLabel(text: String, modifier: Modifier = Modifier) {
  Text(
    text,
    modifier = modifier,
    style = MaterialTheme.typography.labelLarge,
    color = MaterialTheme.colorScheme.onSurfaceVariant
  )
}
