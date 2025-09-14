package ir.sharif.androidsample.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Mail
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import androidx.compose.ui.platform.LocalContext

@Composable
fun ProfileHeader(
  fullName: String,
  email: String?,
  avatarUrl: String? = null
) {
  val onSurface = MaterialTheme.colorScheme.onSurface
  val onSurfaceVariant = MaterialTheme.colorScheme.onSurfaceVariant
  val ctx = LocalContext.current

  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(16.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    // Avatar (image if available, otherwise initials bubble)
    if (!avatarUrl.isNullOrBlank()) {
      AsyncImage(
        model = ImageRequest.Builder(ctx)
          .data(avatarUrl)
          .crossfade(true)
          // If you find the avatar still caches after an upload, you can also disable caches:
          // .memoryCachePolicy(CachePolicy.DISABLED)
          // .diskCachePolicy(CachePolicy.DISABLED)
          .build(),
        contentDescription = "Avatar",
        modifier = Modifier
          .size(64.dp)
          .clip(CircleShape)
      )
    } else {
      Surface(
        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
        shape = CircleShape,
        modifier = Modifier.size(64.dp)
      ) {
        Box(contentAlignment = Alignment.Center) {
          Text(
            text = initialsOf(fullName),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
          )
        }
      }
    }

    Spacer(Modifier.width(16.dp))

    Column(Modifier.weight(1f)) {
      Text(
        text = if (fullName.isBlank()) "—" else fullName,
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
        color = onSurface
      )
      Spacer(Modifier.height(6.dp))
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
          Icons.Outlined.Mail,
          contentDescription = "Email",
          tint = onSurfaceVariant
        )
        Spacer(Modifier.width(8.dp))
        Text(
          email?.ifBlank { "—" } ?: "—",
          style = MaterialTheme.typography.bodyMedium,
          color = onSurfaceVariant
        )
      }
    }
  }
}

private fun initialsOf(name: String): String =
  name.split(" ")
    .filter { it.isNotBlank() }
    .take(2)
    .map { it.first().uppercase() }
    .joinToString("")
    .ifBlank { "U" }
