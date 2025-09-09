package ir.sharif.androidsample.compose

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

import java.security.MessageDigest
import java.util.Locale

enum class Screen { LOGIN, PROFILE, CHANGE_PASSWORD }

class AuthViewModel : ViewModel() {


  private val _currentScreen = MutableStateFlow(Screen.LOGIN)
  val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

  private val _email = MutableStateFlow(FieldState())
  val email: StateFlow<FieldState> = _email.asStateFlow()

  private val _password = MutableStateFlow(FieldState())
  val password: StateFlow<FieldState> = _password.asStateFlow()

  // simple “is logging in” flag you can show as loading indicator later
  private val _loggingIn = MutableStateFlow(false)
  val loggingIn: StateFlow<Boolean> = _loggingIn.asStateFlow()

  private val _currentUser = MutableStateFlow<User?>(null)
  val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

  private val _oldPwd = MutableStateFlow(FieldState())
  val oldPwd: StateFlow<FieldState> = _oldPwd.asStateFlow()

  private val _newPwd = MutableStateFlow(FieldState())
  val newPwd: StateFlow<FieldState> = _newPwd.asStateFlow()

  private val _repeatNewPwd = MutableStateFlow(FieldState())
  val repeatNewPwd: StateFlow<FieldState> = _repeatNewPwd.asStateFlow()

  fun onOldPwdChange(s: String) {
    _oldPwd.value = FieldState(value = s, error = null) // we’ll validate on submit
  }

  fun onNewPwdChange(s: String) {
    _newPwd.value = FieldState(
      value = s,
      error = if (s.length < 8) "Min 8 characters" else null
    )
    // live re-check of repeat field, if user already typed it
    _repeatNewPwd.update { it.copy(error = if (it.value == s) null else "Passwords don't match") }
  }

  fun onRepeatNewPwdChange(s: String) {
    val new = _newPwd.value.value
    _repeatNewPwd.value = FieldState(
      value = s,
      error = if (s == new) null else "Passwords don't match"
    )
  }

  /** Validates and updates currentUser.password (hash) if OK. Returns true on success. */
  fun validateAndChangePassword(): Boolean {
    val user = _currentUser.value ?: return false

    val oldOk = sha256(_oldPwd.value.value) == user.password
    _oldPwd.update { it.copy(error = if (oldOk) null else "Current password is incorrect") }

    _newPwd.update { it.copy(error = if (it.value.length < 8) "Min 8 characters" else null) }

    _repeatNewPwd.update {
      it.copy(error = if (it.value == _newPwd.value.value) null else "Passwords don't match")
    }

    val sameAsOld = sha256(_newPwd.value.value) == user.password
    if (sameAsOld) {
      _newPwd.update { it.copy(error = "New password must be different") }
    }

    val ok = listOf(
      _oldPwd.value.error,
      _newPwd.value.error,
      _repeatNewPwd.value.error
    ).all { it == null }

    if (!ok) return false

    val newHashed = sha256(_newPwd.value.value)

    // ✅ Directly update _currentUser
    _currentUser.value = user.copy(password = newHashed)

    // Clear fields
    _oldPwd.value = FieldState()
    _newPwd.value = FieldState()
    _repeatNewPwd.value = FieldState()

    return true
  }


  fun onEmailChange(s: String) {
    _email.value = FieldState(
      value = s,
      error = when {
        s.isBlank() -> "Email cannot be empty"
        !EMAIL_REGEX.matches(s) -> "Invalid email format"
        else -> null
      }
    )
  }

  fun onPasswordChange(s: String) {
    _password.value = FieldState(
      value = s,
      error = if (s.length < 8) "Min 8 characters" else null
    )
  }

  /**TODO: activate this one when backend is up. Returns true if validation passes; call backend if true */
//  fun validateAndLogin(): Boolean {
//    // submit-time recheck
//    _email.update {
//      it.copy(error = when {
//        it.value.isBlank() -> "Required"
//        !EMAIL_REGEX.matches(it.value) -> "Invalid email format"
//        else -> null
//      })
//    }
//    _password.update {
//      it.copy(error = if (it.value.length < 8) "Min 8 characters" else null)
//    }
//
//    val ok = listOf(_email.value.error, _password.value.error).all { it == null }
//    if (ok) {
//      // TODO: call backend (suspend). For now, you can just set _loggingIn to true/false around it.
//      // _loggingIn.value = true
//      // try { repo.login(_email.value.value, _password.value.value) } finally { _loggingIn.value = false }
//    }
//    return ok
//  }

  /** Local-only login: validate, then create a temp User and expose it */
  fun validateAndLogin(): Boolean {
    // Recheck on submit
    _email.update {
      it.copy(error = when {
        it.value.isBlank() -> "Required"
        !EMAIL_REGEX.matches(it.value) -> "Invalid email format"
        else -> null
      })
    }
    _password.update {
      it.copy(error = if (it.value.length < 8) "Min 8 characters" else null)
    }

    val ok = listOf(_email.value.error, _password.value.error).all { it == null }
    if (!ok) return false

    // Simulate a successful login by creating a temp user
    val mail = _email.value.value.trim()
    val hashed = sha256(_password.value.value)
    val tempUser = User(
      firstName = "Temp",
      lastName = "User",
      username = mail.substringBefore('@').ifBlank { "user" },
      emailAddress = mail,
      password = hashed
    )
    _currentUser.value = tempUser
    return true
  }

  private fun sha256(input: String): String {
    val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
    return bytes.joinToString("") { "%02x".format(it).lowercase(Locale.US) }
  }

  // in AuthViewModel
  fun logout() {
    _currentUser.value = null
  }


  fun goToLogin()          { _currentScreen.value = Screen.LOGIN }
  fun goToProfile()        { _currentScreen.value = Screen.PROFILE }
  fun goToChangePassword() { _currentScreen.value = Screen.CHANGE_PASSWORD }




}
