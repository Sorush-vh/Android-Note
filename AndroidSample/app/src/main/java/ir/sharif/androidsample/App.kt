package ir.sharif.androidsample

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.DoneAll
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import ir.sharif.androidsample.data.model.NoteKind
import ir.sharif.androidsample.di.ServiceLocator
import ir.sharif.androidsample.ui.finished.FinishedScreen
import ir.sharif.androidsample.ui.notes.*
import ir.sharif.androidsample.ui.profile.ChangePasswordScreen
import ir.sharif.androidsample.ui.profile.EditProfileScreen
import ir.sharif.androidsample.ui.profile.ProfileScreen
import ir.sharif.androidsample.ui.search.SearchScreen

data class BottomDest(val route: String, val label: String, val icon: ImageVector)

private val bottomDests = listOf(
  BottomDest("home",     "Home",     Icons.Outlined.Home),
  BottomDest("finished", "Finished", Icons.Outlined.DoneAll),
  BottomDest("search",   "Search",   Icons.Outlined.Search),
  BottomDest("settings", "Settings", Icons.Outlined.Settings),
)

// Build a route for a note kind, optionally with id
private fun routeFor(kind: NoteKind, id: String? = null): String {
  val base = when (kind) {
    NoteKind.IDEAS    -> "ideas_note"
    NoteKind.GOALS    -> "goals_note"
    NoteKind.SHOPPING -> "shopping_note"
    NoteKind.ROUTINE  -> "routine_note"
  }
  return if (id.isNullOrBlank()) base else "$base?id=$id"
}

/** Screens that render their own note-detail bottom bar (hide global bar & FAB). */
private val noteDetailRoutes = setOf(
  "shopping_note", "ideas_note", "goals_note", "routine_note", "new_note"
)

private val settingsRoutes = setOf("settings", "edit_profile", "change_password")

@Composable
fun App(onLoggedOut: () -> Unit = {}) {
  val nav = rememberNavController()

  Scaffold(
    bottomBar = {
      val entry by nav.currentBackStackEntryAsState()
      val patternRoute = entry?.destination?.route
      val currentRoute = patternRoute?.substringBefore('?') // works with pattern routes like "ideas_note?id={id}"
      val hideGlobal = currentRoute in noteDetailRoutes || currentRoute in settingsRoutes
      if (!hideGlobal) {
        NavigationBar {
          val current = entry?.destination
          bottomDests.forEach { dest ->
            val selected = current?.hierarchy?.any { it.route == dest.route } == true
            NavigationBarItem(
              selected = selected,
              onClick = {
                nav.navigate(dest.route) {
                  popUpTo(nav.graph.findStartDestination().id) { saveState = true }
                  launchSingleTop = true
                  restoreState = true
                }
              },
              icon = { Icon(dest.icon, contentDescription = dest.label) },
              label = { Text(dest.label) }
            )
          }
        }
      }
    },
    floatingActionButton = {
      val entry by nav.currentBackStackEntryAsState()
      val currentRoute = entry?.destination?.route?.substringBefore('?')
      val hideGlobal = currentRoute in noteDetailRoutes || currentRoute in settingsRoutes
      if (!hideGlobal) {
        FloatingActionButton(
          onClick = { nav.navigate("new_note") },
          containerColor = MaterialTheme.colorScheme.primary,
          contentColor = MaterialTheme.colorScheme.onPrimary
        ) { Icon(Icons.Filled.Add, contentDescription = "Add") }
      }
    },
    floatingActionButtonPosition = FabPosition.Center
  ) { inner ->
    NavHost(navController = nav, startDestination = "home", modifier = Modifier.padding(inner)) {

      // Home
      composable("home") {
        NotesScreen(
          onOpenNote = { note -> nav.navigate(routeFor(note.kind, note.id)) },
          vm = androidx.lifecycle.viewmodel.compose.viewModel(factory = ServiceLocator.vmFactory())
        )
      }

      // Finished
      composable("finished") {
        FinishedScreen(
          onOpenNote = { note -> nav.navigate(routeFor(note.kind, note.id)) },
          vm = androidx.lifecycle.viewmodel.compose.viewModel(factory = ServiceLocator.vmFactory())
        )
      }

      // Search
      composable("search") {
        SearchScreen(
          onOpenNote = { note -> nav.navigate(routeFor(note.kind, note.id)) },
          vm = androidx.lifecycle.viewmodel.compose.viewModel(factory = ServiceLocator.vmFactory())
        )
      }

      // Settings/profile
      composable("settings") {
        ProfileScreen(
          onBack = { nav.popBackStack() },
          onLoggedOut = onLoggedOut,
          onEditProfile = { nav.navigate("edit_profile") },
          onChangePassword = { nav.navigate("change_password") }
        )
      }
      composable("edit_profile") {
        EditProfileScreen(onBack = { nav.popBackStack() }, onEmailChanged = { onLoggedOut() })
      }
      composable("change_password") {
        ChangePasswordScreen(onBack = { nav.popBackStack() }, onLoggedOut = onLoggedOut)
      }

      // New note picker
      composable("new_note") {
        NewNoteScreen(
          onBack = { nav.popBackStack() },
          onPick = { kind -> nav.navigate(routeFor(kind)) } // no id yet
        )
      }

      // ---- 4 detail screens: each accepts optional ?id= and passes it to the screen ----
      composable(
        route = "shopping_note?id={id}",
        arguments = listOf(navArgument("id") { nullable = true })
      ) { backStack ->
        val id = backStack.arguments?.getString("id")
        ShoppingNoteScreen(
          noteId = id,
          onBack = { nav.popBackStack() },
          onDelete = {
            // after deletion, go back to home (or just pop)
            nav.popBackStack()
          }
        )
      }

      composable(
        route = "ideas_note?id={id}",
        arguments = listOf(navArgument("id") { nullable = true })
      ) { backStack ->
        val id = backStack.arguments?.getString("id")
        IdeasNoteScreen(
          noteId = id,
          onBack = { nav.popBackStack() },
          onDelete = { nav.popBackStack() }
        )
      }

      composable(
        route = "goals_note?id={id}",
        arguments = listOf(navArgument("id") { nullable = true })
      ) { backStack ->
        val id = backStack.arguments?.getString("id")
        GoalsNoteScreen(
          noteId = id,
          onBack = { nav.popBackStack() },
          onDelete = { nav.popBackStack() }
        )
      }

      composable(
        route = "routine_note?id={id}",
        arguments = listOf(navArgument("id") { nullable = true })
      ) { backStack ->
        val id = backStack.arguments?.getString("id")
        RoutineNoteScreen(
          noteId = id,
          onBack = { nav.popBackStack() },
          onDelete = { nav.popBackStack() }
        )
      }
    }
  }
}
