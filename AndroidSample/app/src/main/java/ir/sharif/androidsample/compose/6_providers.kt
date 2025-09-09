package ir.sharif.androidsample.compose

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ir.sharif.androidsample.ui.theme.AndroidSampleColors
import ir.sharif.androidsample.ui.theme.AndroidSampleTheme
import ir.sharif.androidsample.ui.theme.LocalColorProvider

private const val TAG = "CompositionProviders"

class CompositionProviders : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Log.d(TAG, "setContent")

            // Uncomment one of these to see different examples
            CompositionLocalProviderExample()
//            DefaultColorsStaticProviderScreen()
//            CustomColorsStaticProviderScreen()
        }
    }
}

val LocalValueProvider = compositionLocalOf<Int> {
    throw NotImplementedError()
}

@Composable
fun CompositionLocalProviderExample() {
    // This demonstrates how CompositionLocalProvider works within a specific tree
    // We provide a default value of 0 for LocalValueProvider
    CompositionLocalProvider(
        LocalValueProvider provides 0
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Access the current value from LocalValueProvider (which is 0)
            val defaultValue = LocalValueProvider.current
            Text("Default value: $defaultValue")

            // Create a new scope with a different value (Int.MAX_VALUE)
            // This overrides the previous value only within this scope
            CompositionLocalProvider(
                LocalValueProvider provides Int.MAX_VALUE
            ) {
                // Access the new value from LocalValueProvider (which is now Int.MAX_VALUE)
                val newValue = LocalValueProvider.current
                Text("New value: $newValue")

                // This nested composable will receive the Int.MAX_VALUE
                // since it's within this CompositionLocalProvider scope
                NestedCompositionLocalContent(
                    identifier = "new value",
                )
            }

            // This nested composable will receive the original value (0)
            // since it's outside the inner CompositionLocalProvider scope
            NestedCompositionLocalContent(
                identifier = "default value",
            )
        }
    }
}

@Composable
fun NestedCompositionLocalContent(identifier: String) {
    // Access the current value from LocalValueProvider
    // The value depends on where this composable is called from
    val value = LocalValueProvider.current
    Text("Value in nested composable ($identifier): $value")
}

@Composable
fun DefaultColorsStaticProviderScreen() {
    // Getting LocalColorProvider this way returns the default AndroidSampleColors.
    // See Theme#AndroidSampleColors for the default colors
    val colors = LocalColorProvider.current
    MyColorsProviderContent(colors)
}

@Composable
fun CustomColorsStaticProviderScreen() {
    // Getting LocalColorProvider this way returns the AndroidSampleColors
    // provided in AndroidSampleTheme.
    // See Theme#AndroidSampleTheme for the custom provided colors
    AndroidSampleTheme {
        val colors = LocalColorProvider.current
        MyColorsProviderContent(colors)
    }
}

@Composable
fun MyColorsProviderContent(colors: AndroidSampleColors) {
    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .size(300.dp)
                .align(Alignment.Center)
                .background(colors.color1),
        ) {
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .align(Alignment.Center)
                    .background(colors.color2),
            ) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .align(Alignment.Center)
                        .background(colors.color3),
                )
            }
        }
    }
}
