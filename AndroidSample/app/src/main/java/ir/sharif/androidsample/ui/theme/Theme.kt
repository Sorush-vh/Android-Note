package ir.sharif.androidsample.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

data class AndroidSampleColors(
    val color1: Color = Color.Black,
    val color2: Color = Color.Black,
    val color3: Color = Color.Black,
)

val LocalColorProvider = staticCompositionLocalOf {
    AndroidSampleColors()
}

/**
 * Access the color shades anywhere in your Composables like this:
 *
 * Example usage:
 * ```
 * @Composable
 * fun MyComponent() {
 *     val colorProvider = LocalColorProvider.current
 *
 *     Box(
 *         modifier = Modifier
 *             .background(colorProvider.redShades.red400)
 *             .padding(16.dp)
 *     ) {
 *         Text(
 *             text = "Red Text",
 *             color = colorProvider.blueShades.blue700
 *         )
 *     }
 * }
 * ```
 */

@Composable
fun AndroidSampleTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    CompositionLocalProvider(
        LocalColorProvider provides AndroidSampleColors(
            color1 = Red700,
            color2 = Green700,
            color3 = Blue700,
        )
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
