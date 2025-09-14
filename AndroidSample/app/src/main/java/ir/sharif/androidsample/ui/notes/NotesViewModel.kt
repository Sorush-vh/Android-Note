package ir.sharif.androidsample.ui.notes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ir.sharif.androidsample.data.model.NoteEnvelope
import ir.sharif.androidsample.data.repository.NotesRepository
import ir.sharif.androidsample.di.ServiceLocator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
class NotesViewModel(
  private val repo: NotesRepository = ServiceLocator.notesRepo
) : ViewModel() {

  data class HomeUi(
    val loading: Boolean = false,
    val error: String? = null,
    val pinned: List<NoteEnvelope> = emptyList(),
    val recent: List<NoteEnvelope> = emptyList()
  )

  private val _home = MutableStateFlow(HomeUi())
  val home: StateFlow<HomeUi> = _home

  fun loadHome() = viewModelScope.launch {
    _home.value = _home.value.copy(loading = true, error = null)
    runCatching {
      val pinned = repo.pinned()
      val recent = repo.recent()
      pinned to recent
    }.onSuccess { (p, r) ->
      _home.value = HomeUi(loading = false, pinned = p, recent = r)
    }.onFailure { e ->
      _home.value = _home.value.copy(loading = false, error = e.message ?: "Failed to load notes")
    }
  }

  suspend fun search(q: String) = repo.search(q)
  suspend fun finished() = repo.finished()
}
