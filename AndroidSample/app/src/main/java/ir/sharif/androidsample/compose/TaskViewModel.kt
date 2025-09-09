package ir.sharif.androidsample.compose

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class TaskViewModel : ViewModel() {
  private val _tasks = MutableStateFlow<List<Task>>(emptyList())
  val tasks: StateFlow<List<Task>> = _tasks.asStateFlow()

  fun addTask(task: Task = Task()) {
    _tasks.update { it + task }
  }

  fun remove(id: Int) {
    _tasks.update { list -> list.filterNot { it.id == id } }
  }

  fun updateTitle(id: Int, newTitle: String) = modify(id) { it.copy(title = newTitle) }
  fun updateDesc(id: Int, newDesc: String)   = modify(id) { it.copy(description = newDesc) }
  fun setDone(id: Int, done: Boolean)        = modify(id) { it.copy(isDone = done) }

  private fun modify(id: Int, transform: (Task) -> Task) {
    _tasks.update { list ->
      val i = list.indexOfFirst { it.id == id }
      if (i == -1) list else list.toMutableList().also { it[i] = transform(it[i]) }
    }
  }
}
