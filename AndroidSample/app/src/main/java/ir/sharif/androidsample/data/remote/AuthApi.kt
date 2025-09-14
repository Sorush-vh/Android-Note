package ir.sharif.androidsample.data.remote

import ir.sharif.androidsample.data.dto.*
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface AuthApi {
  @POST("api/accounts/token/")
  suspend fun login(@Body body: LoginRequest): LoginResponse

  @POST("api/accounts/signup/")
  suspend fun signup(@Body body: SignupRequest): Map<String, Any?> // backend returns {"detail": "..."}; we don't use it

  @POST("api/accounts/token/refresh/")
  suspend fun refresh(@Body body: RefreshRequest): RefreshResponse

  @GET("api/accounts/check_username/")
  suspend fun checkUsernameAvailable(@Query("username") username: String): AvailabilityDto

  @GET("api/accounts/check_email/")
  suspend fun checkEmailAvailable(@Query("email") email: String): AvailabilityDto

  @POST("api/accounts/change-password/")
  suspend fun changePassword(@Body req: ChangePasswordRequest): retrofit2.Response<Unit>

}
