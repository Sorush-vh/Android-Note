package ir.sharif.androidsample.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ir.sharif.androidsample.data.dto.SignupRequest
import ir.sharif.androidsample.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import android.util.Patterns
import ir.sharif.androidsample.util.toUserMessage

data class AuthState(val loading: Boolean = false, val error: String? = null, val loggedIn: Boolean = false)

class AuthViewModel(private val repo: AuthRepository) : ViewModel() {
  private val _state = MutableStateFlow(AuthState())
  val state = _state.asStateFlow()

  fun login(username: String, password: String, onSuccess: () -> Unit) = viewModelScope.launch {
    _state.value = AuthState(loading = true)
    runCatching { repo.login(username, password) }
      .onSuccess { _state.value = AuthState(loggedIn = true); onSuccess() }
      .onFailure { e -> _state.value = AuthState(error = e.toUserMessage()) }
  }

  fun signup(req: SignupRequest, onSuccess: () -> Unit) = viewModelScope.launch {
    _state.value = AuthState(loading = true)
    runCatching { repo.signup(req) }
      .onSuccess { _state.value = AuthState(); onSuccess() }
      .onFailure { e -> _state.value = AuthState(error = e.toUserMessage()) }
  }


  fun submitSignup(
    username: String,
    email: String,
    firstName: String,
    lastName: String,
    password: String,
    confirm: String,
    onSuccess: () -> Unit
  ) {
    viewModelScope.launch {
      // Offline rules
      val error = validateSignup(username, email, firstName, lastName, password, confirm)
      if (error != null) {
        _state.value = _state.value.copy(loading = false, error = error)
        return@launch
      }

      _state.value = _state.value.copy(loading = true, error = null)

      // Optional server-side availability checks; proceed if endpoint missing
      val emailAvail = repo.isEmailAvailable(email.trim())
      if (emailAvail == false) {
        _state.value = _state.value.copy(loading = false, error = "This email is already in use.")
        return@launch
      }
      val userAvail = repo.isUsernameAvailable(username.trim())
      if (userAvail == false) {
        _state.value = _state.value.copy(loading = false, error = "This username is already taken.")
        return@launch
      }

      // Server call
      signup(
        SignupRequest(
          username = username.trim(),
          password = password,
          email = email.trim(),
          first_name = firstName.trim(),
          last_name = lastName.trim()
        ),
        onSuccess = onSuccess
      )
    }
  }


  private fun validateSignup(
    username: String,
    email: String,
    firstName: String,
    lastName: String,
    password: String,
    confirm: String
  ): String? {
    if (username.isBlank() || email.isBlank() || firstName.isBlank() || lastName.isBlank() || password.isBlank() || confirm.isBlank())
      return "Please fill all fields."
    if (!Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches())
      return "Invalid email format."
    if (password.length < 8)
      return "Password must be at least 8 characters."
    if (password != confirm)
      return "Passwords do not match."
    return null
  }


  fun logout(onDone: () -> Unit) = viewModelScope.launch { repo.logout(); onDone() }
}
