package ir.sharif.androidsample.data.repository

import ir.sharif.androidsample.data.dto.*
import ir.sharif.androidsample.data.remote.AuthApi
import ir.sharif.androidsample.data.store.TokenStore
import ir.sharif.androidsample.data.dto.ChangePasswordRequest
import retrofit2.HttpException

class AuthRepository(
  private val api: AuthApi,
  private val tokens: TokenStore
) {
  suspend fun signup(req: SignupRequest) { api.signup(req) }

  // convenience overload used by AuthViewModel
  suspend fun signup(
    username: String,
    email: String,
    first: String?,
    last: String?,
    password: String
  ) = signup(SignupRequest(username = username, password = password, email = email, first_name = first, last_name = last))


  suspend fun login(username: String, password: String) {
    val res = api.login(LoginRequest(username, password))
    tokens.save(res.access, res.refresh)
  }

  suspend fun logout() = tokens.clear()

  suspend fun isUsernameAvailable(username: String): Boolean? =
    runCatching { api.checkUsernameAvailable(username).available }.getOrNull()

  suspend fun isEmailAvailable(email: String): Boolean? =
    runCatching { api.checkEmailAvailable(email).available }.getOrNull()

  suspend fun changePassword(old: String, new: String) {
    val resp = api.changePassword(ChangePasswordRequest(old_password = old, new_password = new))
    if (!resp.isSuccessful) throw HttpException(resp)
  }
}
