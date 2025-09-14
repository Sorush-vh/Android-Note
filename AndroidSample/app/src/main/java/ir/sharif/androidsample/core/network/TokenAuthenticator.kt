package ir.sharif.androidsample.core.network

import ir.sharif.androidsample.data.dto.RefreshRequest
import ir.sharif.androidsample.data.remote.AuthApi
import ir.sharif.androidsample.data.store.TokenStore
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

class TokenAuthenticator(
  private val tokens: TokenStore,
  private val authApi: AuthApi
) : Authenticator {
  override fun authenticate(route: Route?, response: Response): Request? {
    // Avoid infinite loops
    if (response.request.header("Authorization")?.startsWith("Bearer ") != true) return null

    val refresh = runBlocking { tokens.refresh() } ?: return null
    val newAccess = try { runBlocking { authApi.refresh(RefreshRequest(refresh)).access } }
    catch (_: Exception) { runBlocking { tokens.clear() }; return null }

    runBlocking { tokens.updateAccess(newAccess) }

    return response.request.newBuilder()
      .header("Authorization", "Bearer $newAccess")
      .build()
  }
}
