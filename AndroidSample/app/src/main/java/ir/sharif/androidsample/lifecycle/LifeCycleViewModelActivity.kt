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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ir.sharif.androidsample.R
import ir.sharif.androidsample.ui.theme.AndroidSampleTheme
import androidx.lifecycle.viewmodel.compose.viewModel

class LifeCycleViewModelActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("LifeCycleViewModelActivity", "onCreate")
        enableEdgeToEdge()
        setContent {
            val viewModel = viewModel<CounterViewModel>()
            val state = viewModel.counter.collectAsStateWithLifecycle()
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
                            value = state.value,
                            onCounterChange = viewModel::count
                        )
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d("LifeCycleViewModelActivity", "onStart")
    }

    override fun onResume() {
        super.onResume()
        Log.d("LifeCycleViewModelActivity", "onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.d("LifeCycleViewModelActivity", "onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.d("LifeCycleViewModelActivity", "onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("LifeCycleViewModelActivity", "onDestroy")
    }

    @Composable
    fun CounterButton(modifier: Modifier, value: Int, onCounterChange: () -> Unit) {
        Column(modifier = modifier) {
            Text(text = "Count: $value")
            Button(onClick = {
                onCounterChange()
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