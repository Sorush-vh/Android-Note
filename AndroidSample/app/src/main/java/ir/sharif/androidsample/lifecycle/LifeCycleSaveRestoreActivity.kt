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

class LifeCycleSaveRestoreActivity : ComponentActivity() {
    private var countState = 0
    private val COUNT_KEY = "counter"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("LifeCycleSaveRestoreActivity", "onCreate")
        enableEdgeToEdge()
        countState = savedInstanceState?.getInt(COUNT_KEY) ?: 0
        setContent {
            AndroidSampleTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CounterButton(
                            modifier = Modifier,
                            initialCount = countState,
                            onCounterChange = {
                                countState = it
                            })
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d("LifeCycleSaveRestoreActivity", "onStart")
    }

    override fun onResume() {
        super.onResume()
        Log.d("LifeCycleSaveRestoreActivity", "onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.d("LifeCycleSaveRestoreActivity", "onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.d("LifeCycleSaveRestoreActivity", "onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("LifeCycleSaveRestoreActivity", "onDestroy")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Log.d("LifeCycleSaveRestoreActivity", "onSaveInstanceState")
        outState.putInt(COUNT_KEY, countState)
    }

    @Composable
    fun CounterButton(modifier: Modifier, initialCount: Int, onCounterChange: (Int) -> Unit) {
        var count by remember { mutableStateOf(initialCount) }

        Column(modifier = modifier) {
            Text(text = "Count: $count")
            Button(onClick = {
                count++
                onCounterChange(count)
            }) {
                Text(text = stringResource(R.string.button_counter_text))
            }
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun CounterButtonPreview() {
        AndroidSampleTheme {
            CounterButton(modifier = Modifier, 0, {})
        }
    }
}