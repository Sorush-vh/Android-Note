package ir.sharif.androidsample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowCompat
import ir.sharif.androidsample.di.ServiceLocator
import ir.sharif.androidsample.ui.auth.LoginScreen
import ir.sharif.androidsample.ui.auth.SignupScreen

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    // Dark icons on light status bar
    WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars = true

    setContent {
      var authed by remember { mutableStateOf(false) }

      // Decide initial screen based on token (ServiceLocator is initialized in your Application class)
      LaunchedEffect(Unit) {
        authed = ServiceLocator.tokens.access() != null
      }

      if (authed) {
        App(
          onLoggedOut = { authed = false } // App will call this after logout/email-change/password-change
        )
      } else {
        // Simple 2-screen auth flow
        var showSignup by remember { mutableStateOf(false) }
        if (showSignup) {
          SignupScreen(
            onDone = { showSignup = false },  // go back to Login
            onBack = { showSignup = false }
          )
        } else {
          LoginScreen(
            onLoggedIn = { authed = true },
            onGoToSignup = { showSignup = true }
          )
        }
      }
    }
  }
}



