package com.alexsullivan.translatebuddy.grouping

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.alexsullivan.translatebuddy.Screen
import com.alexsullivan.translatebuddy.storage.TranslationBuddyPreferences

@Composable
fun TranslationGroupingScreen(navController: NavController) {
  val wordGroups = TranslationBuddyPreferences(LocalContext.current).getWordGroups()
  Scaffold(
    floatingActionButton = { AddGrouping(navController) },
    topBar = { TopAppBar(title = { Text("Groupings") }) }) {
    LazyColumn(modifier = Modifier.padding(it)) {
      items(wordGroups) {
        Text(it.toString())
      }
    }
  }
}

@Composable
fun AddGrouping(navController: NavController) {
  FloatingActionButton(onClick = { navController.navigate(Screen.AddGrouping.route) }) {
    Icon(imageVector = Icons.Filled.Add, contentDescription = "Add grouping")
  }
}
