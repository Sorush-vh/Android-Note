package ir.sharif.androidsample.compose


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.ArrowBack
// ---------- Reusable: put two composables side-by-side with spacing ----------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppScaffold(
  title: String,
  showBack: Boolean = false,
  onBack: (() -> Unit)? = null,
  bottomBar: (@Composable () -> Unit)? = null,
  content: @Composable (PaddingValues) -> Unit
) {
  Scaffold(
    topBar = {
      SmallTopAppBar(
        title = { Text(title) },
        navigationIcon = {
          if (showBack && onBack != null) {
            IconButton(onClick = onBack) {
              Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
            }
          }
        }
      )
    },
    bottomBar = { bottomBar?.invoke() },
    // 👇 key line: tells Scaffold to pad for status+nav bars
    contentWindowInsets = WindowInsets.safeDrawing
  ) { innerPadding ->
    // apply the padding it gives you
    Box(
      Modifier
        .fillMaxSize()
        .padding(innerPadding)          // reserve top/bottom
        .consumeWindowInsets(innerPadding) // avoid double padding in nested scaffolds
    ) {
      content(innerPadding)
    }
  }
}
