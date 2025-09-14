package ir.sharif.androidsample.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun AppTopBarCenteredBack(
  title: String,
  onBack: () -> Unit,
  modifier: Modifier = Modifier,
  backTint: Color = MaterialTheme.colorScheme.primary
) {
  CenterAlignedTopAppBar(
    modifier = modifier,
    title = { Text(title, style = MaterialTheme.typography.titleLarge) },
    navigationIcon = {
      Row(
        modifier = Modifier
          .padding(start = 8.dp)
          .clickable { onBack() },
        verticalAlignment = Alignment.CenterVertically
      ) {
        Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back", tint = backTint)
        Spacer(Modifier.width(6.dp))
        Text("Back", color = backTint, style = MaterialTheme.typography.labelLarge)
      }
    },
    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
      containerColor = MaterialTheme.colorScheme.surface
    )
  )
}
