package ir.sharif.androidsample.compose

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class Stateless : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Log.d("Stateless", "setContent")
            HelloScreen3()
        }
    }
}

@Composable
fun HelloScreen3() {
    Log.d("Stateless", "HelloScreen3")
    var name by remember { mutableStateOf("") }
    StatelessContent(name = name, onNameChange = { name = it })
}

@Composable
fun StatelessContent(name: String, onNameChange: (String) -> Unit) {
    Log.d("Stateless", "StatelessContent")
    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "Hello, $name",
            modifier = Modifier.padding(bottom = 8.dp),
            fontSize = 14.sp
        )
        OutlinedTextField(value = name, onValueChange = onNameChange, label = { Text("Name") })
    }
}


@Preview(showBackground = true)
@Composable
fun HelloScreen3Preview() {
    HelloScreen3()
}