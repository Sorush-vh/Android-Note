// NoteDetailViewModel.kt  (single copy!)
package ir.sharif.androidsample.ui.notes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ir.sharif.androidsample.data.dto.NoteUpsert
import ir.sharif.androidsample.data.model.NoteEnvelope
import ir.sharif.androidsample.data.repository.NotesRepository
import ir.sharif.androidsample.di.ServiceLocator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class NoteDetailViewModel(
  private val repo: NotesRepository = ServiceLocator.notesRepo
) : ViewModel() {

  data class State(
    val loading: Boolean = false,
    val error: String? = null,
    val note: NoteEnvelope? = null
  )

  private val _state = MutableStateFlow(State())
  val state = _state.asStateFlow()

  fun load(id: String) {
    viewModelScope.launch {
      _state.value = State(loading = true)
      runCatching { repo.get(id) }
        .onSuccess { _state.value = State(note = it) }
        .onFailure { e -> _state.value = State(error = e.message ?: "Load failed") }
    }
  }

  suspend fun create(body: NoteUpsert): NoteEnvelope = repo.create(body)
  suspend fun replace(id: String, body: NoteUpsert): NoteEnvelope = repo.replace(id, body)
  suspend fun patch(id: String, patch: Map<String, Any?>): NoteEnvelope = repo.patch(id, patch)
  suspend fun delete(id: String) = repo.delete(id)
}
