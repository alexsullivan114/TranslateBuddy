package com.alexsullivan.translatebuddy

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavType
import androidx.navigation.navArgument

sealed class TopLevelScreen(val route: String, @StringRes val stringId: Int, val icon: ImageVector) {
  object Grouping : TopLevelScreen("Grouping", R.string.groupings, Icons.Filled.Star)
  object List : TopLevelScreen("List", R.string.translations, Icons.Filled.List)
}

sealed class Screen(val route: String, val arguments: List<String> = emptyList()) {
  object AddGrouping : Screen(
    "AddGrouping/{groupingId}",
    listOf("groupingId")
  ) {
    fun routeWithId(groupingId: String): String {
      return route.replace("{groupingId}", groupingId)
    }
  }

  val namedArguments = arguments.map { navArgument(it) { type = NavType.StringType } }
}
