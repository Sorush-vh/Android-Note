package ir.sharif.androidsample.ui.profile

import android.database.Cursor
import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import ir.sharif.androidsample.di.ServiceLocator
import ir.sharif.androidsample.ui.components.ThinDivider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
  onBack: () -> Unit,
  onEmailChanged: () -> Unit,                    // jump to login after logout (email changed)
  vm: ProfileViewModel = viewModel(factory = ServiceLocator.vmFactory())
) {
  val ui by vm.state.collectAsState()
  val scope = rememberCoroutineScope()
  val snackbar = remember { SnackbarHostState() }
  val ctx = LocalContext.current

  // Ensure we actually fetch profile if needed
  LaunchedEffect(Unit) { if (ui.me == null) vm.load() }

  // Local editable copies (seed once from server data)
  var seeded by rememberSaveable { mutableStateOf(false) }
  var firstName by rememberSaveable { mutableStateOf("") }
  var lastName by rememberSaveable { mutableStateOf("") }
  var email by rememberSaveable { mutableStateOf("") }
  var originalEmail by rememberSaveable { mutableStateOf<String?>(null) }

  LaunchedEffect(ui.me, seeded) {
    val me = ui.me
    if (!seeded && me != null) {
      firstName     = me.first_name.orEmpty()
      lastName      = me.last_name.orEmpty()
      email         = me.email
      originalEmail = me.email
      seeded = true
    }
  }

  // Photo picker — permissionless system picker
  val pickAvatar = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.PickVisualMedia()
  ) { uri: Uri? ->
    if (uri != null) {
      scope.launch {
        val bytes = withContext(Dispatchers.IO) { ctx.contentResolver.openInputStream(uri)?.use { it.readBytes() } }
        if (bytes == null) {
          snackbar.showMessage("Unable to read selected image")
          return@launch
        }
        val fileName = resolveDisplayName(ctx, uri) ?: "avatar.jpg"
        val ok = vm.uploadAvatar(bytes, fileName)
        if (ok) {
          snackbar.showMessage("Image updated")
          // vm.state will already contain latest me via ProfileViewModel
        } else {
          snackbar.showMessage(ui.error ?: "Image upload failed")
        }
      }
    }
  }

  Scaffold(
    topBar = {
      CenterAlignedTopAppBar(
        title = { Text("Edit Profile") },
        navigationIcon = {
          TextButton(onClick = onBack) {
            Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = null)
            Spacer(Modifier.width(6.dp))
            Text("Settings")
          }
        }
      )
    },
    snackbarHost = { SnackbarHost(snackbar) }
  ) { inner ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(inner)
    ) {
      ThinDivider()

      // Avatar + Change Image
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 24.dp),
        contentAlignment = Alignment.Center
      ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          val avatarUrl = ui.me?.avatar_url
          if (!avatarUrl.isNullOrBlank()) {
            AsyncImage(
              model = ImageRequest.Builder(LocalContext.current)
                .data(avatarUrl)
                .crossfade(true)
                .build(),
              contentDescription = "Profile picture",
              contentScale = ContentScale.Crop,
              modifier = Modifier
                .size(96.dp)
                .then(Modifier) // keep same size/shape as placeholder
                .clip(CircleShape)
            )
          } else {
            Surface(
              shape = CircleShape,
              color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
              modifier = Modifier.size(96.dp)
            ) {
              Box(contentAlignment = Alignment.Center) {
                val initials = remember(ui.me) {
                  listOfNotNull(
                    ui.me?.first_name?.firstOrNull()?.uppercaseChar(),
                    ui.me?.last_name?.firstOrNull()?.uppercaseChar()
                  ).joinToString("").ifBlank { "U" }
                }
                Text(
                  initials,
                  style = MaterialTheme.typography.headlineMedium,
                  color = MaterialTheme.colorScheme.primary
                )
              }
            }
          }
          Spacer(Modifier.height(12.dp))
          OutlinedButton(
            onClick = {
              pickAvatar.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            },
            modifier = Modifier.widthIn(min = 220.dp)
          ) {
            Icon(Icons.Outlined.Edit, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Change Image")
          }
        }
      }

      ThinDivider()

      // Editable fields
      LabeledEditField(
        label = "First Name",
        value = firstName,
        onValueChange = { firstName = it }
      )
      LabeledEditField(
        label = "Last Name",
        value = lastName,
        onValueChange = { lastName = it }
      )
      LabeledEditField(
        label = "Email Address",
        value = email,
        onValueChange = { email = it },
        helper = "Changing email means you’ll need to re-login."
      )

      Spacer(Modifier.height(10.dp))

      // Save button
      Button(
        onClick = {
          scope.launch {
            val changedEmail = email.trim() != originalEmail?.trim()
            val ok = vm.update(
              firstName = firstName.ifBlank { null },
              lastName  = lastName.ifBlank { null },
              email     = email.trim()
            )
            if (ok) {
              if (changedEmail) {
                vm.logout(onDone = onEmailChanged)   // kick to Login
              } else {
                snackbar.showMessage("Saved")
                onBack()
              }
            } else {
              snackbar.showMessage(ui.error ?: "Save failed")
            }
          }
        },
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp)
          .height(48.dp),
        enabled = !ui.loading
      ) {
        Icon(Icons.Outlined.Check, contentDescription = null)
        Spacer(Modifier.width(8.dp))
        Text("Save Changes")
      }

      if (ui.loading) {
        LinearProgressIndicator(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
        )
      }
      ui.error?.let {
        Text(
          it,
          color = MaterialTheme.colorScheme.error,
          modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
        )
      }
    }
  }
}

@Composable
private fun LabeledEditField(
  label: String,
  value: String,
  onValueChange: (String) -> Unit,
  helper: String? = null
) {
  Column(Modifier
    .fillMaxWidth()
    .padding(horizontal = 16.dp, vertical = 10.dp)
  ) {
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
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Start
      )
    }
  }
}

/** Best-effort filename from a content Uri. */
private fun resolveDisplayName(ctx: android.content.Context, uri: Uri): String? {
  return runCatching {
    var name: String? = null
    val cursor: Cursor? = ctx.contentResolver.query(uri, null, null, null, null)
    cursor?.use {
      val idx = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
      if (idx >= 0 && it.moveToFirst()) {
        name = it.getString(idx)
      }
    }
    name
  }.getOrNull()
}

private suspend fun SnackbarHostState.showMessage(msg: String) {
  showSnackbar(message = msg, withDismissAction = true)
}
