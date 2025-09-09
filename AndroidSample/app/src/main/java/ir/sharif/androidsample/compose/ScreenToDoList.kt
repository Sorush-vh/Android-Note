package ir.sharif.androidsample.compose

import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.Column
import androidx.lifecycle.viewmodel.compose.viewModel


class TestTodoList : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      ScreenToDoList()
    }
  }
}



@Composable
fun ScreenToDoList(vm: TaskViewModel = viewModel()) {
  val tasks by vm.tasks.collectAsStateWithLifecycle(initialValue = emptyList())

  Column {
    Button(
      onClick = { vm.addTask() },
      modifier = Modifier.padding(16.dp)
    ) { Text("Add Task") }

    LazyColumn {
      items(tasks, key = { it.id }) { task ->
        TaskRow(
          task = task,
          onTitleChange = { vm.updateTitle(task.id, it) },
          onDescriptionChange = { vm.updateDesc(task.id, it) },
          onCheckedChange = { vm.setDone(task.id, it) },
          onRemove = { vm.remove(task.id) }
        )
      }
    }
  }
}

@Composable
fun TaskRow(
  task: Task,
  onTitleChange: (String) -> Unit,
  onDescriptionChange: (String) -> Unit,
  onCheckedChange: (Boolean) -> Unit,
  onRemove: () -> Unit
) {
  Row(modifier = Modifier.padding(8.dp)) {
    OutlinedTextField(
      value = task.title,
      onValueChange = onTitleChange,
      modifier = Modifier
        .weight(1f)
        .padding(end = 8.dp),
      textStyle = TextStyle(
        fontSize = 18.sp,
        fontWeight = FontWeight.SemiBold,
        textDecoration = if (task.isDone) TextDecoration.LineThrough else TextDecoration.None
      ),
      singleLine = true,
      label = { Text("Title") }
    )

    OutlinedTextField(
      value = task.description,
      onValueChange = onDescriptionChange,
      modifier = Modifier
        .weight(1f)
        .padding(end = 8.dp),
      textStyle = TextStyle(
        fontSize = 14.sp,
        textDecoration = if (task.isDone) TextDecoration.LineThrough else TextDecoration.None
      ),
      maxLines = 3,
      label = { Text("Description") }
    )

    Checkbox(
      checked = task.isDone,
      onCheckedChange = onCheckedChange,
      modifier = Modifier.padding(end = 8.dp)
    )

    Button(onClick = onRemove) {
      Text("Remove")
    }
  }
}

@Preview(showBackground = true)
@Composable
fun PreviewTodoList() {
  ScreenToDoList()
}
