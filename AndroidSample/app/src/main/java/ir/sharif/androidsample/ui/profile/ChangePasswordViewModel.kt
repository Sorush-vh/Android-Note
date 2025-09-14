package ir.sharif.androidsample.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ir.sharif.androidsample.data.repository.AuthRepository
import ir.sharif.androidsample.data.repository.ProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

data class ChangePasswordState(
  val loading: Boolean = false,
  val oldError: String? = null,
  val newError: String? = null,
  val confirmError: String? = null,
  val generalError: String? = null,
  val success: Boolean = false
)

class ChangePasswordViewModel(
  private val profileRepo: ProfileRepository,
  private val authRepo: AuthRepository
) : ViewModel() {

  private val _state = MutableStateFlow(ChangePasswordState())
  val state = _state.asStateFlow()

  /**
   * Validate, call backend, then logout + notify caller on success.
   * Also logs out on 401 (expired/invalid session).
   */
  fun submit(old: String, new: String, confirm: String, onLoggedOut: () -> Unit) = viewModelScope.launch {
    // Clear previous errors
    _state.value = ChangePasswordState(loading = false)

    // Offline checks
    var oldErr: String? = null
    var newErr: String? = null
    var confirmErr: String? = null
    if (old.isBlank()) oldErr = "Please enter your current password."
    if (new.length < 8) newErr = "New password must be at least 8 characters."
    if (confirm != new) confirmErr = "Passwords do not match."
    if (oldErr != null || newErr != null || confirmErr != null) {
      _state.value = ChangePasswordState(
        oldError = oldErr, newError = newErr, confirmError = confirmErr
      )
      return@launch
    }

    _state.value = _state.value.copy(loading = true)
    try {
      profileRepo.changePassword(old, new) // <- your repo calls POST /api/accounts/change-password/
      // Success: force logout & bounce caller
      _state.value = ChangePasswordState(success = true)
      authRepo.logout()
      onLoggedOut()
    } catch (e: HttpException) {
      if (e.code() == 401) {
        // Session expired -> logout and bounce
        _state.value = ChangePasswordState(generalError = "Your session expired. Please sign in again.")
        authRepo.logout()
        onLoggedOut()
        return@launch
      }
      // Try to extract field errors from 400 JSON body
      val body = e.response()?.errorBody()?.string().orEmpty()
      val mappedOld = when {
        "old_password" in body && "incorrect" in body.lowercase() -> "Current password is incorrect."
        "old_password" in body -> "Please check your current password."
        else -> null
      }
      val mappedNew = when {
        "new_password" in body && ("short" in body.lowercase() || "at least" in body.lowercase()) ->
          "New password is too short."
        "new_password" in body -> "New password is not acceptable."
        else -> null
      }
      val general = if (mappedOld == null && mappedNew == null)
        (body.takeIf { it.isNotBlank() } ?: "Password change failed.") else null

      _state.value = ChangePasswordState(
        oldError = mappedOld,
        newError = mappedNew,
        generalError = general
      )
    } catch (e: Exception) {
      _state.value = ChangePasswordState(generalError = e.message ?: "Password change failed.")
    }
  }
}
