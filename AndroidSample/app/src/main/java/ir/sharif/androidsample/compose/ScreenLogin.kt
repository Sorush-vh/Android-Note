package ir.sharif.androidsample.compose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel





class LoginMenu : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      LoginScreen()
    }
  }
}


@Composable
fun LoginScreen(
  vm: AuthViewModel = viewModel(),
  onLoginSuccess: () -> Unit = {}   // ← default no-op keeps old callers working
) {
  AppScaffold(title = "Profile") { _ ->


    val e by vm.email.collectAsStateWithLifecycle()
    val p by vm.password.collectAsStateWithLifecycle()
    val loggingIn by vm.loggingIn.collectAsStateWithLifecycle()

    Column(Modifier.padding(16.dp)) {
      ScreenHeader(
        title = "Let's Login",
        subtitle = "Add Notes to your ideas"
      )
      Spacer(Modifier.height(24.dp))

      FormTextField(
        value = e.value,
        onValueChange = vm::onEmailChange,
        label = "Email",
        error = e.error,
        keyboardOptions = KeyboardOptions(
          keyboardType = KeyboardType.Email,
          imeAction = ImeAction.Next
        )
      )
      Spacer(Modifier.height(12.dp))

      PasswordField(
        value = p.value,
        onValueChange = vm::onPasswordChange,
        label = "Password",
        error = p.error
      )
      Spacer(Modifier.height(20.dp))

      Button(
        onClick = {
          if (vm.validateAndLogin()) {
            onLoginSuccess()     // ← navigate on success
          }
        },
        enabled = !loggingIn,
        modifier = Modifier.fillMaxWidth()
      ) {
        Text(if (loggingIn) "Logging in…" else "Log in")
      }
    }
  }
}



@Composable
fun ScreenHeader(
  title: String,
  subtitle: String? = null,
  modifier: Modifier = Modifier
) {
  Column(modifier.fillMaxWidth()) {
    Text(
      text = title,
      style = TextStyle(fontSize = 28.sp, fontWeight = FontWeight.SemiBold)
    )
    if (subtitle != null) {
      Spacer(Modifier.height(6.dp))
      Text(
        text = subtitle,
        style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Normal)
      )
    }
  }
}
