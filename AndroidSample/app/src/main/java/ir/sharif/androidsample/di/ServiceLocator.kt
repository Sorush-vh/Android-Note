package ir.sharif.androidsample.di

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ir.sharif.androidsample.core.network.AuthInterceptor
import ir.sharif.androidsample.core.network.TokenAuthenticator
import ir.sharif.androidsample.core.network.authedRetrofit
import ir.sharif.androidsample.core.network.plainRetrofit
import ir.sharif.androidsample.data.remote.AuthApi
import ir.sharif.androidsample.data.remote.NotesApi
import ir.sharif.androidsample.data.remote.ProfileApi
import ir.sharif.androidsample.data.repository.AuthRepository
import ir.sharif.androidsample.data.repository.NotesRepository
import ir.sharif.androidsample.data.repository.ProfileRepository
import ir.sharif.androidsample.data.store.PrefsStore
import ir.sharif.androidsample.data.store.TokenStore
import ir.sharif.androidsample.ui.auth.AuthViewModel
import ir.sharif.androidsample.ui.notes.NoteDetailViewModel
import ir.sharif.androidsample.ui.notes.NotesViewModel
import ir.sharif.androidsample.ui.profile.ChangePasswordViewModel
import ir.sharif.androidsample.ui.profile.ProfileViewModel
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

object ServiceLocator {
  lateinit var tokens: TokenStore;     private set
  lateinit var authRepo: AuthRepository; private set
  lateinit var notesRepo: NotesRepository; private set
  lateinit var profileRepo: ProfileRepository; private set
  lateinit var prefs: PrefsStore; private set

  fun init(ctx: Context) {
    tokens = TokenStore(ctx)
    prefs  = PrefsStore(ctx)

    // Retrofit without auth – for login/refresh
    val plain = plainRetrofit()
    val authApi = plain.create(AuthApi::class.java)

    // OkHttp with access token + refresh support
    val client = OkHttpClient.Builder()
      .addInterceptor(AuthInterceptor { tokens.access() })
      .authenticator(TokenAuthenticator(tokens, authApi))
      .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY })
      .build()

    // Retrofit with auth – for app APIs
    val authed = authedRetrofit(client)
    notesRepo   = NotesRepository(authed.create(NotesApi::class.java))
    profileRepo = ProfileRepository(authed.create(ProfileApi::class.java))
    authRepo    = AuthRepository(authApi, tokens)
  }

  fun vmFactory(): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = when {
      modelClass.isAssignableFrom(AuthViewModel::class.java) ->
        AuthViewModel(authRepo) as T

      modelClass.isAssignableFrom(NotesViewModel::class.java) ->
        NotesViewModel(notesRepo) as T

      modelClass.isAssignableFrom(NoteDetailViewModel::class.java) ->
        NoteDetailViewModel(notesRepo) as T

      modelClass.isAssignableFrom(ProfileViewModel::class.java) ->
        ProfileViewModel(profileRepo, authRepo) as T

      modelClass.isAssignableFrom(ChangePasswordViewModel::class.java) ->
        ChangePasswordViewModel(profileRepo, authRepo) as T

      else -> throw IllegalArgumentException("Unknown VM class ${modelClass.name}")
    }
  }
}
