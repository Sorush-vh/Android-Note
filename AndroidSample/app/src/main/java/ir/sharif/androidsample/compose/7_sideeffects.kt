package ir.sharif.androidsample.compose

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LifecycleEventObserver
import kotlinx.coroutines.delay

/** DOC: https://developer.android.com/develop/ui/compose/side-effects */

private const val TAG = "SideEffects"

class SideEffects : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Surface(modifier = Modifier.fillMaxSize()) {
                Log.d(TAG, "setContent")
                LaunchedEffectScreen()
//                DisposableEffectScreen()
//                SideEffectScreen()
            }
        }
    }
}

@Composable
fun LaunchedEffectScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val counter = remember { mutableIntStateOf(0) }
        val text = remember { mutableStateOf("") }

        // LaunchedEffect starts a coroutine that is scoped to the composition
        // It's automatically cancelled and restarted if the key (counter) changes.
        // Press the button rapidly to see the scope is getting restarted everytime.
        LaunchedEffect(key1 = counter.intValue) {
            text.value = "LaunchedEffect started with counter=${counter.intValue}"
            // Simulating some asynchronous work
            delay(1000)
            text.value = "LaunchedEffect completed for counter=${counter.intValue}"
        }

        Text("LaunchedEffect Example")
        Spacer(modifier = Modifier.height(16.dp))
        Text("Counter: ${counter.intValue}")
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { counter.intValue++ }) {
            Text("Increment Counter")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = text.value)
    }
}

@Composable
fun DisposableEffectScreen() {
    val lifecycleOwner = LocalLifecycleOwner.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // DisposableEffect is used for cleanup operations
        // It runs when the composable enters the composition and provides a way to clean up when it leaves
        DisposableEffect(lifecycleOwner) {
            val observer = LifecycleEventObserver { _, event ->
                Log.d(TAG, "Lifecycle event: $event")
            }

            // Add the observer
            lifecycleOwner.lifecycle.addObserver(observer)

            // This is returned when the effect leaves composition
            onDispose {
                Log.d(TAG, "DisposableEffect: Disposing lifecycle observer")
                lifecycleOwner.lifecycle.removeObserver(observer)
            }
        }

        Text("DisposableEffect Example")
        Spacer(modifier = Modifier.height(16.dp))
        Text("This screen demonstrates DisposableEffect")
        Spacer(modifier = Modifier.height(16.dp))
        Text("It adds and removes a lifecycle observer")
        Spacer(modifier = Modifier.height(16.dp))
        Text("Press Home button to move application to background")
        Text("so DisposableEffect#onDispose gets called")
        Spacer(modifier = Modifier.height(16.dp))
        Text("Check Logcat for 'DisposableEffect' messages")
    }
}

@Composable
fun SideEffectScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val counter = remember { mutableIntStateOf(0) }
        val text = remember { mutableStateOf("") }

        SideEffect {
            text.value = "SideEffect: Composition completed with counter=${counter.intValue}"
        }

        Text("SideEffect Example")
        Spacer(modifier = Modifier.height(16.dp))
        Text("Counter: ${counter.intValue}")
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { counter.intValue++ }) {
            Text("Increment Counter")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = text.value)
    }
}
