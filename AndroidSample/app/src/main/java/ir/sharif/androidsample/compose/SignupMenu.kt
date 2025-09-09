package ir.sharif.androidsample.compose



import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.width
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.*
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.foundation.layout.*




class SignupMenu : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      SignupScreen()
    }
  }
}

@Composable
fun SignupScreen(vm: UsersViewModel = viewModel()) {
  val f by vm.firstName.collectAsStateWithLifecycle()
  val l by vm.lastName.collectAsStateWithLifecycle()
  val u by vm.username.collectAsStateWithLifecycle()
  val e by vm.email.collectAsStateWithLifecycle()
  val p by vm.password.collectAsStateWithLifecycle()
  val r by vm.repeatPassword.collectAsStateWithLifecycle()

  Column(Modifier.padding(16.dp)) {
    FirstNameField(value = f.value, error = f.error, onValueChange = vm::onFirstNameChange)
    Spacer(Modifier.height(12.dp))

    LastNameField(value = l.value, error = l.error, onValueChange = vm::onLastNameChange)
    Spacer(Modifier.height(12.dp))

    UsernameField(value = u.value, error = u.error, onValueChange = vm::onUsernameChange)
    Spacer(Modifier.height(12.dp))

    EmailField(value = e.value, error = e.error, onValueChange = vm::onEmailChange)
    Spacer(Modifier.height(12.dp))

    PasswordField(value = p.value, error = p.error, onValueChange = vm::onPasswordChange)
    Spacer(Modifier.height(12.dp))

    RepeatPasswordField(value = r.value, error = r.error, onValueChange = vm::onRepeatPasswordChange)
    Spacer(Modifier.height(20.dp))

    SubmitButton(onClick = { vm.validateOnSubmit() })
  }
}



@Composable
fun UsernameField(
  value: String,
  error: String?,
  onValueChange: (String) -> Unit
) {
  FormTextField(
    value = value,
    onValueChange = onValueChange,
    label = "Username",
    error = error,
    keyboardOptions = KeyboardOptions(
      keyboardType = KeyboardType.Text,
      imeAction = ImeAction.Next
    )
  )
}

@Composable
fun EmailField(
  value: String,
  error: String?,
  onValueChange: (String) -> Unit
) {
  FormTextField(
    value = value,
    onValueChange = onValueChange,
    label = "Email",
    error = error,
    keyboardOptions = KeyboardOptions(
      keyboardType = KeyboardType.Email,
      imeAction = ImeAction.Next
    )
  )
}

@Composable
fun RepeatPasswordField(
  value: String,
  error: String?,
  onValueChange: (String) -> Unit
) {
  PasswordField(
    value = value,
    onValueChange = onValueChange,
    label = "Repeat password",
    error = error
  )
}

@Composable
fun FirstNameField(value: String, error: String?, onValueChange: (String) -> Unit) {
  FormTextField(
    value = value,
    onValueChange = onValueChange,
    label = "First name",
    error = error,
    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Next)
  )
}

@Composable
fun LastNameField(value: String, error: String?, onValueChange: (String) -> Unit) {
  FormTextField(
    value = value,
    onValueChange = onValueChange,
    label = "Last name",
    error = error,
    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Next)
  )
}


@Composable
fun SubmitButton(onClick: () -> Unit) {
  Button(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
    Text("Sign up")
  }
}

@Composable
fun FormTextField(
  modifier: Modifier = Modifier,
  value: String,
  onValueChange: (String) -> Unit,
  label: String,
  error: String? = null,
  singleLine: Boolean = true,
  placeholder: String? = null,
  leadingIcon: (@Composable (() -> Unit))? = null,
  trailingIcon: (@Composable (() -> Unit))? = null,
  keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
  visualTransformation: VisualTransformation = VisualTransformation.None,
  enabled: Boolean = true,
  readOnly: Boolean = false,
) {
  OutlinedTextField(
    value = value,
    onValueChange = onValueChange,
    label = { Text(label) },
    placeholder = { if (placeholder != null) Text(placeholder) },
    singleLine = singleLine,
    isError = error != null,
    supportingText = { if (error != null) Text(error) },
    textStyle = TextStyle(
      fontSize = 14.sp,
      fontWeight = FontWeight.SemiBold
    ),
    leadingIcon = leadingIcon,
    trailingIcon = trailingIcon,
    keyboardOptions = keyboardOptions,
    visualTransformation = visualTransformation,
    enabled = enabled,
    readOnly = readOnly,
    modifier = modifier.fillMaxWidth()
  )
}


@Composable
fun PasswordField(
  value: String,
  onValueChange: (String) -> Unit,
  label: String = "Password",
  error: String? = null
) {
  var visible by remember { mutableStateOf(false) }

  FormTextField(
    value = value,
    onValueChange = onValueChange,
    label = label,
    error = error,
    trailingIcon = {
      val icon = if (visible) Icons.Default.VisibilityOff else Icons.Default.Visibility
      IconButton(onClick = { visible = !visible }) {
        Icon(icon, contentDescription = if (visible) "Hide" else "Show")
      }
    },
    visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
    keyboardOptions = KeyboardOptions(
      keyboardType = KeyboardType.Password,
      imeAction = ImeAction.Done
    )
  )
}

