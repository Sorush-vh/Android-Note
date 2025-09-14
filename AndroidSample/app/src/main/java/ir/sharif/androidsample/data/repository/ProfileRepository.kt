package ir.sharif.androidsample.data.repository

import ir.sharif.androidsample.data.remote.ProfileApi
import ir.sharif.androidsample.data.dto.ProfileDto
import ir.sharif.androidsample.data.dto.UpdateProfileRequest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import ir.sharif.androidsample.data.dto.MeResponse

class ProfileRepository(private val api: ProfileApi) {
  suspend fun me(): MeResponse = api.me()

  suspend fun update(firstName: String?, lastName: String?, email: String): MeResponse =
    api.update(UpdateProfileRequest(first_name = firstName, last_name = lastName, email = email))

  suspend fun isEmailAvailable(email: String): Boolean? =
    runCatching { api.checkEmailAvailable(email).available }.getOrNull()
  // in ProfileRepository.kt
  suspend fun changePassword(old: String, new: String) {
    api.changePassword(ir.sharif.androidsample.data.dto.ChangePasswordRequest(old, new))
  }

  suspend fun uploadAvatar(bytes: ByteArray, filename: String): MeResponse {
    val body = bytes.toRequestBody("image/*".toMediaType())
    val part = MultipartBody.Part.createFormData("avatar", filename, body)
    return api.uploadAvatar(part)
  }
}
