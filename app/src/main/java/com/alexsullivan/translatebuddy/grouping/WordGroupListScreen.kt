@file:OptIn(ExperimentalFoundationApi::class)

package com.alexsullivan.translatebuddy.grouping

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.alexsullivan.translatebuddy.Screen
import com.alexsullivan.translatebuddy.drive.Translation
import com.alexsullivan.translatebuddy.storage.TranslationBuddyPreferences

@Composable
fun TranslationGroupingScreen(navController: NavController) {
  val preferences = TranslationBuddyPreferences(LocalContext.current)
  var selectedWordGroupId by remember { mutableStateOf(preferences.getSelectedWordGroup()) }
  var wordGroups by remember { mutableStateOf(preferences.getWordGroups()) }
  var potentialDeletedWordGroup: WordGroup? by remember { mutableStateOf(null) }
  Scaffold(
    floatingActionButton = { AddGrouping(navController) },
    topBar = { TopAppBar(title = { Text("Groupings") }) }) { it ->
    LazyColumn(modifier = Modifier.padding(it)) {
      items(wordGroups) { wordGroup ->
        WordGroupItem(wordGroup,
          onLongClick = { selectedGroup -> potentialDeletedWordGroup = selectedGroup },
          onClick = { navController.navigate(Screen.AddGrouping.routeWithId(it.id)) },
          selected = selectedWordGroupId == wordGroup.id,
          onChecked = {
            selectedWordGroupId = if (it) {
              preferences.setSelectedWordGroup(wordGroup)
              wordGroup.id
            } else {
              preferences.setSelectedWordGroup(null)
              null
            }
          }
        )
      }
    }
    if (potentialDeletedWordGroup != null) {
      ConfirmDeleteDialog(
        wordGroup = potentialDeletedWordGroup!!,
        onCancel = { potentialDeletedWordGroup = null },
        onDelete = {
          preferences.removeWordGroup(it)
          potentialDeletedWordGroup = null
          wordGroups = preferences.getWordGroups()
        })
    }
  }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun WordGroupItem(
  wordGroup: WordGroup,
  onLongClick: (WordGroup) -> Unit,
  onClick: (WordGroup) -> Unit,
  selected: Boolean,
  onChecked: (Boolean) -> Unit
) {
  Card(
    backgroundColor = MaterialTheme.colors.background, contentColor = contentColorFor(
      MaterialTheme.colors.background
    ), modifier = Modifier
      .fillMaxWidth()
      .padding(8.dp)
      .combinedClickable(onLongClick = { onLongClick(wordGroup) }, onClick = { onClick(wordGroup) })
  ) {
    Column {
      Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
      ) {
        Text(wordGroup.name, style = MaterialTheme.typography.h4, modifier = Modifier.padding(8.dp))
        Checkbox(checked = selected, onCheckedChange = { onChecked(it) })
      }
      for (translation in wordGroup.translations) {
        TranslationItem(translation)
      }
    }
  }
}

@Composable
private fun ConfirmDeleteDialog(
  wordGroup: WordGroup,
  onDelete: (WordGroup) -> Unit,
  onCancel: () -> Unit
) {
  AlertDialog(
    onDismissRequest = { /*TODO*/ },
    title = { Text("Confirm deletion") },
    text = { Text("Are you sure you want to delete ${wordGroup.name}?") },
    buttons = {
      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
        Button(onClick = { onCancel() }) {
          Text("Cancel")
        }
        Spacer(modifier = Modifier.width(8.dp))
        Button(onClick = { onDelete(wordGroup) }) {
          Text("Confirm")
        }
        Spacer(modifier = Modifier.width(8.dp))
      }
    }
  )
}

@Composable
private fun TranslationItem(translation: Translation) {
  Text(
    text = "${translation.english} -> ${translation.nepali}",
    style = MaterialTheme.typography.body1,
    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
  )
}

@Composable
fun AddGrouping(navController: NavController) {
  FloatingActionButton(onClick = { navController.navigate(Screen.AddGrouping.route) }) {
    Icon(imageVector = Icons.Filled.Add, contentDescription = "Add grouping")
  }
}
