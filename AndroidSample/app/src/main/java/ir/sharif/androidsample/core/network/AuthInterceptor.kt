package ir.sharif.androidsample.core.network

import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val tokenProvider: suspend () -> String?) : Interceptor {
  override fun intercept(chain: Interceptor.Chain): Response {
    val req = chain.request()
    if (req.header("Authorization") != null) return chain.proceed(req)

    val access = try { kotlinx.coroutines.runBlocking { tokenProvider() } } catch (_: Exception) { null }
    val authed = if (!access.isNullOrBlank())
      req.newBuilder().header("Authorization", "Bearer $access").build()
    else req

    return chain.proceed(authed)
  }
}
