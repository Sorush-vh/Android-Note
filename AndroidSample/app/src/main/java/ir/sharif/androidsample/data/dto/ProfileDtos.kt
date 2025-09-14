package ir.sharif.androidsample.data.dto


data class MeResponse(
  val username: String,
  val email: String,
  val first_name: String?,
  val last_name: String?,
  val avatar_url: String? = null
)

data class ProfileDto(
  val id: Int,
  val username: String,
  val email: String,
  val first_name: String?,
  val last_name: String?
)

data class UpdateProfileRequest(
  val first_name: String?,
  val last_name: String?,
  val email: String
)
