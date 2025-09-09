package ir.sharif.androidsample.compose

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.security.MessageDigest
import java.util.Locale


class UsersViewModel : ViewModel() {

  private val _users = MutableStateFlow<List<User>>(emptyList())
  val users: StateFlow<List<User>> = _users.asStateFlow()

  private val _firstName = MutableStateFlow(FieldState())
  val firstName: StateFlow<FieldState> = _firstName.asStateFlow()

  private val _lastName = MutableStateFlow(FieldState())
  val lastName: StateFlow<FieldState> = _lastName.asStateFlow()

  private val _username = MutableStateFlow(FieldState())
  val username: StateFlow<FieldState> = _username.asStateFlow()

  private val _email = MutableStateFlow(FieldState())
  val email: StateFlow<FieldState> = _email.asStateFlow()

  private val _password = MutableStateFlow(FieldState())
  val password: StateFlow<FieldState> = _password.asStateFlow()

  private val _repeatPassword = MutableStateFlow(FieldState())
  val repeatPassword: StateFlow<FieldState> = _repeatPassword.asStateFlow()

  // live validation handlers
  fun onFirstNameChange(s: String) {
    _firstName.value = FieldState(s, if (s.isBlank()) "First name cannot be empty" else null)
  }
  fun onLastNameChange(s: String) {
    _lastName.value = FieldState(s, if (s.isBlank()) "Last name cannot be empty" else null)
  }
  fun onUsernameChange(s: String) {
    _username.value = FieldState(s, if (s.isBlank()) "Username cannot be empty" else null)
  }
  fun onEmailChange(s: String) {
    _email.value = FieldState(
      s,
      when {
        s.isBlank() -> "Email cannot be empty"
        !EMAIL_REGEX.matches(s) -> "Invalid email format"
        else -> null
      }
    )
  }
  fun onPasswordChange(s: String) {
    _password.value = FieldState(s, if (s.length < 8) "Password must be at least 8 characters" else null)
  }
  fun onRepeatPasswordChange(s: String) {
    val pwd = _password.value.value
    _repeatPassword.value = FieldState(s, if (s != pwd) "Passwords don't match" else null)
  }

  // submit-time validation + registration
  fun validateOnSubmit(): Boolean {
    _firstName.update { it.copy(error = if (it.value.isBlank()) "Required" else null) }
    _lastName.update { it.copy(error = if (it.value.isBlank()) "Required" else null) }
    _username.update { it.copy(error = if (it.value.isBlank()) "Required" else null) }
    _email.update {
      it.copy(error = when {
        it.value.isBlank() -> "Required"
        !EMAIL_REGEX.matches(it.value) -> "Invalid email format"
        else -> null
      })
    }
    _password.update { it.copy(error = if (it.value.length < 8) "Min 8 characters" else null) }
    _repeatPassword.update {
      it.copy(error = if (it.value != _password.value.value) "Passwords don't match" else null)
    }

    val allOk = listOf(
      _firstName.value.error,
      _lastName.value.error,
      _username.value.error,
      _email.value.error,
      _password.value.error,
      _repeatPassword.value.error
    ).all { it == null }

    if (allOk) registerUser()
    return allOk
  }

  private fun registerUser() {
    // hash once; pass hash to both password + repeat so factory check passes
    val hashed = sha256(_password.value.value)
    runCatching {
      User.create(
        firstName     = _firstName.value.value.trim(),
        lastName      = _lastName.value.value.trim(),
        username      = _username.value.value.trim(),
        emailAddress  = _email.value.value.trim(),
        password      = hashed,
        repeatPassword= hashed
      )
    }.onSuccess { user ->
      _users.update { it + user }
    }.onFailure {
      // optionally map to field errors
    }
  }

  private fun sha256(input: String): String {
    val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
    return bytes.joinToString("") { "%02x".format(it).lowercase(Locale.US) }
  }
}
