package ir.sharif.androidsample.ui.profile

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ir.sharif.androidsample.data.dto.MeResponse
import ir.sharif.androidsample.data.repository.AuthRepository
import ir.sharif.androidsample.data.repository.ProfileRepository
import ir.sharif.androidsample.util.toUserMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ir.sharif.androidsample.data.store.PrefsStore
import ir.sharif.androidsample.data.store.TextSize
import ir.sharif.androidsample.di.ServiceLocator
import kotlinx.coroutines.flow.first
import ir.sharif.androidsample.data.store.TextSize as PrefTextSize


data class ProfileState(
  val loading: Boolean = false,
  val error: String? = null,
  val me: MeResponse? = null
)

class ProfileViewModel(
  private val profileRepo: ProfileRepository,
  private val authRepo: AuthRepository,
  private val prefs: PrefsStore = ServiceLocator.prefs // quick inject
) : ViewModel() {

  private val _state = MutableStateFlow(ProfileState())
  val state = _state.asStateFlow()

  // Rename to make it obvious these are Flows
  val textSizeFlow   = prefs.textSize
  val emailNotifsFlow = prefs.emailNotifs
  val pushNotifsFlow  = prefs.pushNotifs

  fun cycleTextSize() = viewModelScope.launch {
    // first() needs the kotlinx.coroutines.flow.first import
    val current = prefs.textSize.first()
    val next = when (current) {
      PrefTextSize.Small  -> PrefTextSize.Medium
      PrefTextSize.Medium -> PrefTextSize.Large
      PrefTextSize.Large  -> PrefTextSize.Small
    }
    prefs.setTextSize(next)
  }


  fun load() = viewModelScope.launch {
    _state.value = _state.value.copy(loading = true, error = null)
    runCatching { profileRepo.me() }
      .onSuccess { _state.value = ProfileState(me = it) }
      .onFailure { e -> _state.value = ProfileState(error = e.toUserMessage()) }
  }

  /** Offline validation */
  private fun validateEdit(firstName: String, lastName: String, email: String): String? {
    if (firstName.isBlank() || lastName.isBlank() || email.isBlank())
      return "Please fill all fields."
    if (!Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches())
      return "Invalid email address."
    return null
  }

  /** Update names/email. Returns true on success. */
  suspend fun update(firstName: String?, lastName: String?, email: String): Boolean {
    val fn = firstName?.trim().orEmpty()
    val ln = lastName?.trim().orEmpty()
    val em = email.trim()

    // Offline checks
    validateEdit(fn, ln, em)?.let { msg ->
      _state.value = _state.value.copy(error = msg)
      return false
    }

    val current = _state.value.me
    val currentEmail = current?.email?.trim().orEmpty()
    val currentFn = current?.first_name.orEmpty()
    val currentLn = current?.last_name.orEmpty()

    // No-op optimization: nothing changed
    if (fn == currentFn && ln == currentLn && em == currentEmail) {
      _state.value = _state.value.copy(error = null, loading = false)
      return true
    }

    // Optional email uniqueness probe (ignore if endpoint not present)
      // Optional email uniqueness probe (ignore if endpoint not present)
    // Optional email uniqueness probe (ignore if endpoint not present)
    if (currentEmail != em) {
      val available: Boolean? = try {
        profileRepo.isEmailAvailable(em)
      } catch (_: Throwable) {
        null // endpoint missing or failed; rely on server validation
      }
      if (available == false) {
        _state.value = _state.value.copy(error = "This email is already in use.")
        return false
      }
    }


    _state.value = _state.value.copy(loading = true, error = null)
    return runCatching { profileRepo.update(fn, ln, em) }
      .onSuccess { dto -> _state.value = ProfileState(me = dto) }
      .onFailure { e -> _state.value = _state.value.copy(loading = false, error = e.toUserMessage()) }
      .isSuccess
  }

  suspend fun uploadAvatar(bytes: ByteArray, filename: String): Boolean {
    _state.value = _state.value.copy(loading = true, error = null)
    return runCatching { profileRepo.uploadAvatar(bytes, filename) }
      .onSuccess { me -> _state.value = ProfileState(me = me) }
      .onFailure { e -> _state.value = _state.value.copy(loading = false, error = e.toUserMessage()) }
      .isSuccess
  }


  fun logout(onDone: () -> Unit) = viewModelScope.launch {
    authRepo.logout()
    onDone()
  }

  fun setEmailNotifs(on: Boolean) = viewModelScope.launch { prefs.setEmailNotifs(on) }
  fun setPushNotifs(on: Boolean)  = viewModelScope.launch { prefs.setPushNotifs(on) }
}
