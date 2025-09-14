package ir.sharif.androidsample.data.remote

import ir.sharif.androidsample.data.dto.*
import ir.sharif.androidsample.data.model.NoteKind
import retrofit2.http.*

interface NotesApi {
  @GET("api/notes/")
  suspend fun list(
    @Query("pinned") pinned: Boolean? = null,
    @Query("search") search: String? = null
  ): List<NoteDto>

  @GET("api/notes/recent/")
  suspend fun recent(): List<NoteDto>   // <— RAW LIST

  @GET("api/notes/finished/")
  suspend fun finished(): List<NoteDto> // <— RAW LIST

  @GET("api/notes/{id}/")
  suspend fun get(@Path("id") id: String): NoteDto

  @POST("api/notes/")
  suspend fun create(@Body body: NoteUpsert): NoteDto

  @PUT("api/notes/{id}/")
  suspend fun replace(@Path("id") id: String, @Body body: NoteUpsert): NoteDto

  @DELETE("api/notes/{id}/")
  suspend fun delete(@Path("id") id: String)
  @PATCH("api/notes/{id}/")
  suspend fun patch(
    @Path("id") id: String,
    @Body patch: Map<String, @JvmSuppressWildcards Any?>   // 👈 important (see #2)
  ): NoteDto

}
