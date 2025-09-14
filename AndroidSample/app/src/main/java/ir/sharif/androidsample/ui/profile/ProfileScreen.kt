package ir.sharif.androidsample.ui.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.TextFields
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ir.sharif.androidsample.di.ServiceLocator
import ir.sharif.androidsample.ui.components.*
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.compose.runtime.DisposableEffect
import androidx.lifecycle.compose.LocalLifecycleOwner



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
  onBack: () -> Unit,
  onLoggedOut: () -> Unit,
  vm: ProfileViewModel = viewModel(factory = ServiceLocator.vmFactory()),
  onEditProfile: () -> Unit = {},
  onChangePassword: () -> Unit = {},
) {
  val s by vm.state.collectAsState()
  LaunchedEffect(Unit) { vm.load() }

  // bottom sheet
  var showNotif by remember { mutableStateOf(false) }
  val notifEmail by vm.emailNotifsFlow.collectAsState(initial = true)
  val notifPush  by vm.pushNotifsFlow.collectAsState(initial = true)

  // logout confirm
  var showLogoutConfirm by remember { mutableStateOf(false) }
  val lifecycleOwner = LocalLifecycleOwner.current
  DisposableEffect(lifecycleOwner) {
    val obs = LifecycleEventObserver { _, event ->
      if (event == Lifecycle.Event.ON_RESUME) vm.load()
    }
    lifecycleOwner.lifecycle.addObserver(obs)
    onDispose { lifecycleOwner.lifecycle.removeObserver(obs) }
  }

  Scaffold(
    topBar = { AppTopBarCenteredBack(title = "Settings", onBack = onBack) }
  ) { inner ->
    Column(Modifier.fillMaxSize().padding(inner)) {

      ThinDivider()

      val fullName = remember(s.me) {
        listOfNotNull(
          s.me?.first_name?.takeIf { it.isNotBlank() },
          s.me?.last_name?.takeIf { it.isNotBlank() }
        ).joinToString(" ").ifBlank { "—" }
      }
      val email = s.me?.email

      ProfileHeader(
        fullName = fullName,
        email = email,
        avatarUrl = s.me?.avatar_url
      )


      FullWidthTonalButton(
        text = "Edit Profile",
        icon = Icons.Outlined.Edit,
        onClick = onEditProfile
      )

      Spacer(Modifier.height(18.dp))
      ThinDivider()
      Spacer(Modifier.height(10.dp))

      SectionLabel("App Settings", modifier = Modifier.padding(horizontal = 16.dp))
      Spacer(Modifier.height(6.dp))

      SettingRow(
        icon = Icons.Outlined.Lock,
        title = "Change Password",
        trailing = { Icon(Icons.Outlined.ChevronRight, contentDescription = null,
          tint = MaterialTheme.colorScheme.onSurfaceVariant) },
        onClick = onChangePassword
      )

      // Text Size
      SettingRow(
        icon = Icons.Outlined.TextFields,
        title = "Text Size",
        trailing = {
          val current by vm.textSizeFlow.collectAsState(
            initial = ir.sharif.androidsample.data.store.TextSize.Medium
          )
          Text(current.name, color = MaterialTheme.colorScheme.onSurfaceVariant)
        },
        onClick = { vm.cycleTextSize() }
      )

      // Notifications — opens bottom sheet
      SettingRow(
        icon = Icons.Outlined.Notifications,
        title = "Notifications",
        trailing = {
          Text(
            if (notifEmail || notifPush) "Some active" else "All off",
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        },
        onClick = { showNotif = true }
      )

      Spacer(Modifier.height(8.dp))
      ThinDivider()
      Spacer(Modifier.height(6.dp))

      // Logout row triggers dialog
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(start = 12.dp, end = 16.dp, top = 4.dp, bottom = 16.dp)
          .heightIn(min = 48.dp)
          .clickable { showLogoutConfirm = true },
        verticalAlignment = Alignment.CenterVertically
      ) {
        Icon(
          Icons.AutoMirrored.Outlined.Logout,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.error
        )
        Spacer(Modifier.width(12.dp))
        Text(
          "Log Out",
          style = MaterialTheme.typography.bodyLarge,
          color = MaterialTheme.colorScheme.error
        )
      }

      if (s.loading) LinearProgressIndicator(Modifier.fillMaxWidth())
      s.error?.let {
        Text(it, color = MaterialTheme.colorScheme.error,
          modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
      }
    }
  }

  // --- Logout confirmation dialog ---
    ConfirmLogoutDialog(
      show = showLogoutConfirm,
      onConfirm = {
        showLogoutConfirm = false
        vm.logout(onLoggedOut)  // ⬅️ actually logs out, then navigates
      },
      onDismiss = { showLogoutConfirm = false }
    )


  // --- Notifications bottom sheet ---
  if (showNotif) {
    ModalBottomSheet(
      onDismissRequest = { showNotif = false },
      dragHandle = null
    ) {
      Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          "Notifications",
          style = MaterialTheme.typography.titleMedium,
          modifier = Modifier.weight(1f)
        )
        TextButton(onClick = { showNotif = false }) {
          Text("Close")
        }
      }
      ThinDivider()

      // Email notifications toggle
      Row(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text("Email Notifications", modifier = Modifier.weight(1f))
        Switch(
          checked = notifEmail,
          onCheckedChange = { vm.setEmailNotifs(it) }
        )
      }

      // Push notifications toggle
      Row(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text("Push Notifications", modifier = Modifier.weight(1f))
        Switch(
          checked = notifPush,
          onCheckedChange = { vm.setPushNotifs(it) }
        )
      }

      Spacer(Modifier.height(16.dp))
    }
  }
}
