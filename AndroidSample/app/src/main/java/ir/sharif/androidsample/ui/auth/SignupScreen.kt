package ir.sharif.androidsample.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ir.sharif.androidsample.di.ServiceLocator
import ir.sharif.androidsample.ui.components.LabeledTextField
import ir.sharif.androidsample.ui.components.PasswordField
import ir.sharif.androidsample.ui.components.PrimaryButton
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.ui.text.input.ImeAction

@Composable
fun SignupScreen(
  onDone: () -> Unit,
  onBack: () -> Unit,
  vm: AuthViewModel = viewModel(factory = ServiceLocator.vmFactory())
) {
  val s by vm.state.collectAsState()

  var username by remember { mutableStateOf("") }
  var email by remember { mutableStateOf("") }
  var firstName by remember { mutableStateOf("") }
  var lastName by remember { mutableStateOf("") }
  var password by remember { mutableStateOf("") }
  var confirm by remember { mutableStateOf("") }

  // Show errors after first submit attempt
  var showErrors by remember { mutableStateOf(false) }

  // Lightweight local checks for per-field hints
  val emailOk = android.util.Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()
  val usernameErr = showErrors && username.isBlank()
  val emailErr    = showErrors && !emailOk
  val firstErr    = showErrors && firstName.isBlank()
  val lastErr     = showErrors && lastName.isBlank()
  val passErr     = showErrors && password.length < 8
  val confirmErr  = showErrors && confirm != password

  Surface {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
      Column(Modifier.widthIn(max = 500.dp).padding(24.dp)) {

        Text("Create account", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(16.dp))

        LabeledTextField(
          value = username,
          onValueChange = { username = it },
          label = "Username",
          isError = usernameErr,
          supportingText = if (usernameErr) "Required" else null,
          modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))

        LabeledTextField(
          value = email,
          onValueChange = { email = it },
          label = "Email",
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
          isError = emailErr,
          supportingText = if (emailErr) "Invalid email format" else null,
          modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))

        LabeledTextField(
          value = firstName,
          onValueChange = { firstName = it },
          label = "First name",
          isError = firstErr,
          supportingText = if (firstErr) "Required" else null,
          modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))

        LabeledTextField(
          value = lastName,
          onValueChange = { lastName = it },
          label = "Last name",
          isError = lastErr,
          supportingText = if (lastErr) "Required" else null,
          modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))

        PasswordField(
          value = password,
          onValueChange = { password = it },
          label = "Password",
          isError = passErr,
          supportingText = if (passErr) "Minimum 8 characters" else null,
          modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))

        PasswordField(
          value = confirm,
          onValueChange = { confirm = it },
          label = "Confirm password",
          isError = confirmErr,
          supportingText = if (confirmErr) "Passwords do not match" else null,
          modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        PrimaryButton(
          text = "Sign up",
          onClick = {
            showErrors = true
            // Let VM be the final gate; optionally short-circuit if local errors exist
            val hasLocalError = usernameErr || emailErr || firstErr || lastErr || passErr || confirmErr
            if (!hasLocalError) {
              vm.submitSignup(
                username = username,
                email = email,
                firstName = firstName,
                lastName = lastName,
                password = password,
                confirm = confirm,
                onSuccess = onDone
              )
            }
          },
          modifier = Modifier.fillMaxWidth()
        )

        TextButton(onClick = onBack, modifier = Modifier.align(Alignment.End)) {
          Text("Back to sign in")
        }

        if (s.loading) {
          LinearProgressIndicator(Modifier.fillMaxWidth().padding(top = 12.dp))
        }
        s.error?.let {
          Text(
            text = it,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 8.dp)
          )
        }
      }
    }
  }
}
