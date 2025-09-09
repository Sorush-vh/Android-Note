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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

private const val TAG = "Remember"

class Remember : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Log.d(TAG, "setContent")
            NoRememberScreen()
//            RememberScreen()
//            RememberSaveableScreen()
        }
    }
}

@Composable
fun NoRememberScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        var counter = 0
        Log.d(TAG, "NoRememberScreen: counter value: $counter")

        Text("counter: $counter")
        Button(
            onClick = {
                counter++
                Log.d(TAG, "Increment counter button clicked, counter value: $counter")
            },
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Text("Increment counter")
        }
    }
}

@Composable
fun RememberScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Using remember
        val counter = remember { mutableIntStateOf(0) }
        Log.d(TAG, "RememberScreen: counter value: ${counter.intValue}")

        Text("counter: ${counter.intValue}")
        Button(
            onClick = { counter.intValue++ },
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Text("Increment counter")
        }
    }
}

@Composable
fun RememberSaveableScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Using remember - will be reset on configuration changes
        val regularCounter = remember { mutableIntStateOf(0) }

        // Using rememberSaveable - will survive configuration changes
        val savedCounter = rememberSaveable { mutableIntStateOf(0) }
        Log.d(
            TAG,
            "RememberSaveableScreen: counter regularCounter: ${regularCounter.intValue} , savedCounter: ${savedCounter.intValue}"
        )


        Text("Regular counter (remember): ${regularCounter.value}")
        Button(
            onClick = { regularCounter.intValue++ },
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Text("Increment regular counter")
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text("Saved counter (rememberSaveable): ${savedCounter.value}")
        Button(
            onClick = { savedCounter.intValue++ },
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Text("Increment saved counter")
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text("Rotate the device to see the difference:")
        Text("- Regular counter will reset")
        Text("- Saved counter will preserve its value")
    }
}
