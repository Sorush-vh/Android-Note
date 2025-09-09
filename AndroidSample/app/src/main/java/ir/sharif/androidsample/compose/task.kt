package ir.sharif.androidsample.compose

data class Task(
  val id: Int = nextId(),
  var title: String = "Add Title",
  var description: String = "Add Description",
  var isDone: Boolean = false
) {
  companion object {
    private var idCounter = 0
    fun nextId(): Int {
      idCounter++
      return idCounter
    }
  }
}
