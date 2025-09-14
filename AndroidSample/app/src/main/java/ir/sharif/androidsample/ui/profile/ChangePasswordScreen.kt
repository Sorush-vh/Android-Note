package ir.sharif.androidsample.ui.profile

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ir.sharif.androidsample.di.ServiceLocator
import ir.sharif.androidsample.ui.components.PasswordField
import ir.sharif.androidsample.ui.components.ThinDivider

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangePasswordScreen(
  onBack: () -> Unit,
  onLoggedOut: () -> Unit,
  vm: ChangePasswordViewModel = viewModel(factory = ServiceLocator.vmFactory())
) {
  val s by vm.state.collectAsState()

  var old by remember { mutableStateOf("") }
  var npw by remember { mutableStateOf("") }
  var cfm by remember { mutableStateOf("") }

  Scaffold(
    topBar = {
      CenterAlignedTopAppBar(
        title = { Text("Change Password") },
        navigationIcon = {
          TextButton(onClick = onBack) {
            Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = null)
            Spacer(Modifier.width(6.dp))
            Text("Back")
          }
        }
      )
    }
  ) { inner ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(inner)
    ) {
      ThinDivider()

      // Section 1
      Text(
        "Please input your current password",
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
        color = MaterialTheme.colorScheme.primary
      )
      Text(
        "Current Password",
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 2.dp),
        style = MaterialTheme.typography.labelLarge
      )
      PasswordField(
        value = old,
        onValueChange = { old = it },
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp)
      )
      if (s.oldError != null) {
        Text(
          s.oldError!!,
          color = MaterialTheme.colorScheme.error,
          style = MaterialTheme.typography.bodySmall,
          modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
        )
      }

      ThinDivider(Modifier.padding(top = 16.dp))

      // Section 2
      Text(
        "Now, create your new password",
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
        color = MaterialTheme.colorScheme.primary
      )

      Text(
        "New Password",
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 2.dp),
        style = MaterialTheme.typography.labelLarge
      )
      PasswordField(
        value = npw,
        onValueChange = { npw = it },
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp)
      )
      if (s.newError != null) {
        Text(
          s.newError!!,
          color = MaterialTheme.colorScheme.error,
          style = MaterialTheme.typography.bodySmall,
          modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
        )
      }

      Text(
        "Retype New Password",
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        style = MaterialTheme.typography.labelLarge
      )
      PasswordField(
        value = cfm,
        onValueChange = { cfm = it },
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp)
      )
      if (s.confirmError != null) {
        Text(
          s.confirmError!!,
          color = MaterialTheme.colorScheme.error,
          style = MaterialTheme.typography.bodySmall,
          modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
        )
      }

      if (s.generalError != null) {
        Text(
          s.generalError!!,
          color = MaterialTheme.colorScheme.error,
          textAlign = TextAlign.Start,
          modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
        )
      }

      Spacer(Modifier.height(18.dp))

      Button(
        onClick = { vm.submit(old, npw, cfm, onLoggedOut) },
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp)
          .height(48.dp),
        enabled = !s.loading
      ) {
        Text("Submit New Password", modifier = Modifier.weight(1f))
        Icon(Icons.Outlined.ArrowForward, contentDescription = null)
      }

      if (s.loading) {
        LinearProgressIndicator(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
        )
      }
    }
  }
}
