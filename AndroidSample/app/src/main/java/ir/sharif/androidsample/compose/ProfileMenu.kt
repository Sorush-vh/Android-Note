package ir.sharif.androidsample.compose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.ArrowBack
// ---------- Reusable: put two composables side-by-side with spacing ----------

class ProfileMenu : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      MaterialTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
          AppRoot()
        }
      }
    }
  }
}


@Composable
fun AppRoot() {
  val auth: AuthViewModel = viewModel()
  val screen by auth.currentScreen.collectAsStateWithLifecycle()

  Box(Modifier.fillMaxSize()) {
    // tiny badge in corner to show the current screen
    Text(
      text = screen.name,
      style = MaterialTheme.typography.labelSmall,
      modifier = Modifier
        .align(Alignment.TopEnd)
        .padding(6.dp)
    )

    when (screen) {
      Screen.LOGIN -> LoginScreen(
        vm = auth,
        onLoginSuccess = { auth.goToProfile() }
      )
      Screen.PROFILE -> ProfileScreen(
        auth = auth,
        onChangePassword = { auth.goToChangePassword() },
        onLogoutToLogin = { auth.logout(); auth.goToLogin() }
      )
      Screen.CHANGE_PASSWORD -> ChangePasswordScreen(
        auth = auth,
        onBackToProfile = { auth.goToProfile() }   // ← this is what the arrow calls
      )
    }
  }
}






@Composable
fun SideBySide(
  spacing: Dp = 8.dp,
  verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
  modifier: Modifier = Modifier,
  start: @Composable () -> Unit,
  end:   @Composable () -> Unit
) {
  Row(modifier, verticalAlignment = verticalAlignment) {
    start()
    Spacer(Modifier.width(spacing))
    end()
  }
}

@Composable
fun AvatarCircle(
  initials: String,
  size: Dp = 32.dp,
  modifier: Modifier = Modifier
) {
  Surface(
    shape = CircleShape,
    color = MaterialTheme.colorScheme.primary,
    modifier = modifier.size(size)
  ) {
    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
      Text(
        text = initials,
        style = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.SemiBold),
        color = MaterialTheme.colorScheme.onPrimary
      )
    }
  }
}

private fun initialsOf(first: String, last: String): String {
  val a = first.trim().firstOrNull()?.uppercaseChar()
  val b = last.trim().firstOrNull()?.uppercaseChar()
  return buildString { if (a != null) append(a); if (b != null) append(b) }
}

@Composable
fun ProfileTop(user: User) {
  val initials = initialsOf(user.firstName, user.lastName)

  SideBySide(
    spacing = 12.dp,
    start = { AvatarCircle(initials = initials) },
    end = {
      Column {
        Text(
          text = "${user.firstName} ${user.lastName}",
          style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
        )
        Spacer(Modifier.height(2.dp))
        EmailWithIcon(email = user.emailAddress)
      }
    }
  )
}

@Composable
fun EmailWithIcon(email: String) {
  SideBySide(
    spacing = 6.dp,
    verticalAlignment = Alignment.CenterVertically,
    start = {
      Icon(
        imageVector = Icons.Filled.Email,
        contentDescription = "Email",
        tint = MaterialTheme.colorScheme.onSurfaceVariant
      )
    },
    end = {
      Text(
        text = email,
        style = TextStyle(fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
      )
    }
  )
}

@Composable
fun ProfileActions(
  onChangePassword: () -> Unit,
  onFontSize: () -> Unit,
  onNotifications: () -> Unit,
) {
  Button(onClick = onChangePassword, modifier = Modifier.fillMaxWidth()) {
    Text("Change password")
  }
  Spacer(Modifier.height(24.dp))
  OutlinedButton(onClick = onFontSize, modifier = Modifier.fillMaxWidth()) {
    Text("Font size")
  }
  Spacer(Modifier.height(12.dp))
  OutlinedButton(onClick = onNotifications, modifier = Modifier.fillMaxWidth()) {
    Text("Notifications")
  }
}

@Composable
fun LogoutButtonWithConfirm(
  onConfirmLogout: () -> Unit
) {
  var show by rememberSaveable { mutableStateOf(false) }

  Button(onClick = { show = true }, modifier = Modifier.fillMaxWidth()) {
    Text("Log out")
  }

  if (show) {
    AlertDialog(
      onDismissRequest = { show = false },
      title = { Text("Log out?") },
      text  = { Text("You’ll be returned to the login screen.") },
      confirmButton = {
        TextButton(onClick = {
          show = false
          onConfirmLogout()
        }) { Text("Log out") }
      },
      dismissButton = {
        TextButton(onClick = { show = false }) { Text("Cancel") }
      }
    )
  }
}


@Composable
fun ProfileScreen(
  auth: AuthViewModel,
  onChangePassword: () -> Unit,
  onLogoutToLogin: () -> Unit
) {
  AppScaffold(title = "Profile") { _ ->
    // profile content

    val user by auth.currentUser.collectAsStateWithLifecycle()

    if (user == null) {
      // Guard (optional)
      Column(
        Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center
      ) {
        Text("You’re not logged in.")
        Spacer(Modifier.height(12.dp))
        Button(onClick = onLogoutToLogin, modifier = Modifier.fillMaxWidth()) {
          Text("Go to Login")
        }
      }
//      return
    }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
      ScreenHeader(title = "Profile", subtitle = "Your account settings")
      Spacer(Modifier.height(24.dp))

      ProfileTop(user = user!!)
      Spacer(Modifier.height(24.dp))

      ProfileActions(
        onChangePassword = onChangePassword,
        onFontSize = { /* TODO open font size picker */ },
        onNotifications = { /* TODO toggle notifications */ }
      )

      Spacer(Modifier.weight(1f))

      LogoutButtonWithConfirm(
        onConfirmLogout = {
          auth.logout()
          onLogoutToLogin() // navigate back to login
        }
      )
    }
  }
}

@Composable
fun ChangePasswordScreen(
  auth: AuthViewModel,
  onBackToProfile: () -> Unit
) {
  AppScaffold(title = "Change Password") { _ ->
    // profile content

    BackHandler(onBack = onBackToProfile)
    val old by auth.oldPwd.collectAsStateWithLifecycle()
    val new by auth.newPwd.collectAsStateWithLifecycle()
    val rep by auth.repeatNewPwd.collectAsStateWithLifecycle()


    Column(Modifier.fillMaxSize().padding(16.dp)) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(
          onClick = onBackToProfile,
          modifier = Modifier.size(48.dp) // ensures standard touch target
        ) {
          Icon(
            imageVector = Icons.Filled.ArrowBack,
            contentDescription = "Back to profile"
          )
        }

        Spacer(Modifier.width(8.dp))
        Text("Change Password", style = MaterialTheme.typography.titleLarge)
      }

      Spacer(Modifier.height(24.dp))

      // Heading: Current/Old password
      Text(
        "Current password",
        style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
      )
      Spacer(Modifier.height(8.dp))
      PasswordField(
        value = old.value,
        onValueChange = auth::onOldPwdChange,
        label = "Current password",
        error = old.error
      )

      Spacer(Modifier.height(24.dp))

      // Heading: New password
      Text("New password", style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.SemiBold))
      Spacer(Modifier.height(8.dp))
      PasswordField(
        value = new.value,
        onValueChange = auth::onNewPwdChange,
        label = "New password",
        error = new.error
      )
      Spacer(Modifier.height(12.dp))
      PasswordField(
        value = rep.value,
        onValueChange = auth::onRepeatNewPwdChange,
        label = "Repeat new password",
        error = rep.error
      )

      Spacer(Modifier.weight(1f))

      Button(
        onClick = {
          val ok = auth.validateAndChangePassword()
          if (ok) onBackToProfile()
        },
        modifier = Modifier.fillMaxWidth()
      ) {
        Text("Save")
      }
    }
  }
}

//
//



