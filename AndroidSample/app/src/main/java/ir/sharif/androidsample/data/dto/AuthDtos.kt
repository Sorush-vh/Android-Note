package ir.sharif.androidsample.data.dto

data class LoginRequest(val username: String, val password: String)
data class LoginResponse(val access: String, val refresh: String)
data class SignupRequest(
  val username: String,
  val password: String,
  val email: String,
  val first_name: String?,
  val last_name: String?
)
data class RefreshRequest(val refresh: String)
data class RefreshResponse(val access: String)
