package ir.sharif.androidsample.core.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import ir.sharif.androidsample.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

private fun moshi(): Moshi =
  Moshi.Builder()
    .addLast(KotlinJsonAdapterFactory())
    .build()

private fun logger() = HttpLoggingInterceptor().apply {
  level = HttpLoggingInterceptor.Level.BODY
}

fun plainRetrofit(): Retrofit =
  Retrofit.Builder()
    .baseUrl(BuildConfig.BASE_URL)
    .addConverterFactory(MoshiConverterFactory.create(moshi()))
    .client(OkHttpClient.Builder().addInterceptor(logger()).build())
    .build()

fun authedRetrofit(client: OkHttpClient): Retrofit =
  Retrofit.Builder()
    .baseUrl(BuildConfig.BASE_URL)
    .addConverterFactory(MoshiConverterFactory.create(moshi()))
    .client(client)
    .build()
