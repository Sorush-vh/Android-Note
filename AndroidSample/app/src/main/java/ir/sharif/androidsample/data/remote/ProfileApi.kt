package ir.sharif.androidsample.data.remote

import ir.sharif.androidsample.data.dto.ProfileDto
import ir.sharif.androidsample.data.dto.UpdateProfileRequest
import ir.sharif.androidsample.data.dto.ChangePasswordRequest
import retrofit2.http.*

import ir.sharif.androidsample.data.dto.MeResponse
import ir.sharif.androidsample.data.dto.AvailabilityDto
import okhttp3.MultipartBody
import retrofit2.http.GET
import retrofit2.http.Body
import retrofit2.http.PUT


interface ProfileApi {
  @GET("api/accounts/me/")
  suspend fun me(): MeResponse

  @PATCH("api/accounts/me/")
  suspend fun update(@Body req: UpdateProfileRequest): MeResponse

  @GET("api/accounts/check_email/")
  suspend fun checkEmailAvailable(@Query("email") email: String): AvailabilityDto

  // in ProfileApi.kt
  @POST("api/accounts/change-password/")
  suspend fun changePassword(@Body req: ChangePasswordRequest)

  @Multipart
  @POST("api/accounts/me/avatar/")
  suspend fun uploadAvatar(@Part avatar: MultipartBody.Part): MeResponse
}
