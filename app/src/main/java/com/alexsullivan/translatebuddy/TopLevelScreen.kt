package com.alexsullivan.translatebuddy

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.graphics.vector.ImageVector

sealed class TopLevelScreen(val route: String, @StringRes val stringId: Int, val icon: ImageVector) {
  object Grouping : TopLevelScreen("Grouping", R.string.groupings, Icons.Filled.Star)
  object List : TopLevelScreen("List", R.string.translations, Icons.Filled.List)
}

sealed class Screen(val route: String) {
  object AddGrouping : Screen("AddGrouping")
}
