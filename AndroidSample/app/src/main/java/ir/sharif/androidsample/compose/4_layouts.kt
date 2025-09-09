package ir.sharif.androidsample.compose

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ir.sharif.androidsample.R
import kotlinx.coroutines.launch

class ComposeLayout : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Log.d("ComposeLayout", "setContent")
            LayoutScreen()
        }
    }
}

@Composable
fun LayoutScreen() {
    ButtonSample()
    ImageSample()
    RowSample()
    ColumnSample()
    ScaffoldSample()
}

@Composable
fun ButtonSample() {
    Button(onClick = { /* Do something */ }) {
        Text("Click Me")
    }
}

@Composable
fun ImageSample() {
    Image(
        painter = painterResource(id = R.drawable.editing_content),
        contentDescription = "Edit Image",
        modifier = Modifier.size(100.dp)
    )
}

@Composable
fun RowSample() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text("Item 1")
        Text("Item 2")
        Text("Item 3")
    }
}

@Composable
fun ColumnSample() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = "First Line",
            textAlign = TextAlign.Center
        )
        Text("Second Line")
    }
}

@Composable
private fun ScaffoldSample() {
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    Scaffold(
        topBar = {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "Top Bar",
                textAlign = TextAlign.Center
            )
        },
        bottomBar = {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "Bottom Bar",
                textAlign = TextAlign.Center
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Text("Hello, Scaffold!", fontSize = 24.sp)
            Button(onClick = {
                coroutineScope.launch {
                    snackbarHostState.showSnackbar("Hello, this is a Snackbar!")
                }
            }) {
                Text("Show Snackbar")
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun LayoutScreenPreview() {
    LayoutScreen()
}


