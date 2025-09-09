package ir.sharif.androidsample.lifecycle

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import ir.sharif.androidsample.R
import ir.sharif.androidsample.ui.theme.AndroidSampleTheme

class LifeCycleActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("LifeCycleActivity", "onCreate")
        enableEdgeToEdge()
        setContent {
            AndroidSampleTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CounterButton(modifier = Modifier)
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d("LifeCycleActivity", "onStart")
    }

    override fun onResume() {
        super.onResume()
        Log.d("LifeCycleActivity", "onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.d("LifeCycleActivity", "onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.d("LifeCycleActivity", "onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("LifeCycleActivity", "onDestroy")
    }

    @Composable
    fun CounterButton(modifier: Modifier) {
        var count by remember { mutableStateOf(0) }

        Column(modifier = modifier) {
            Text(text = "Count: $count")
            Button(onClick = { count++ }) {
                Text(text = stringResource(R.string.button_counter_text))
            }
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun CounterButtonPreview() {
        AndroidSampleTheme {
            CounterButton(modifier = Modifier)
        }
    }
}