package ir.sharif.androidsample.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ir.sharif.androidsample.di.ServiceLocator
import ir.sharif.androidsample.ui.components.PasswordField
import ir.sharif.androidsample.ui.components.PrimaryButton
import ir.sharif.androidsample.ui.components.LabeledTextField

@Composable
fun LoginScreen(
  onLoggedIn: () -> Unit,
  onGoToSignup: () -> Unit,
  vm: AuthViewModel = viewModel(factory = ServiceLocator.vmFactory())
) {
  val s by vm.state.collectAsState()
  var username by remember { mutableStateOf("") }
  var password by remember { mutableStateOf("") }

  Surface {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
      Column(Modifier.widthIn(max = 420.dp).padding(24.dp)) {
        Text("Sign in", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(16.dp))
        LabeledTextField(username, { username = it }, "Username", Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        PasswordField(password, { password = it }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(16.dp))
        PrimaryButton("Sign in", onClick = { vm.login(username.trim(), password, onLoggedIn) }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        TextButton(onClick = onGoToSignup, modifier = Modifier.align(Alignment.End)) { Text("Create account") }

        if (s.loading) LinearProgressIndicator(Modifier.fillMaxWidth().padding(top = 12.dp))
        s.error?.let { Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 8.dp)) }
      }
    }
  }
}
