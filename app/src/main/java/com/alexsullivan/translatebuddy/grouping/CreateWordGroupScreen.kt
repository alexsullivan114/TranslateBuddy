package com.alexsullivan.translatebuddy.grouping

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.alexsullivan.translatebuddy.drive.Translation
import com.alexsullivan.translatebuddy.storage.TranslationBuddyPreferences
import java.util.*

@Composable
fun CreateWordGroupScreen(navController: NavController, wordGroupId: String? = null) {
  val preferences = TranslationBuddyPreferences(LocalContext.current)
  val initialWordGroup =
    preferences.getWordGroups().firstOrNull { it.id == wordGroupId } ?: WordGroup(
      UUID.randomUUID().toString(),
      "",
      emptyList()
    )
  var wordGroup by remember { mutableStateOf(initialWordGroup) }

  Scaffold(topBar = {
    TopAppBar(title = { Text("Add Grouping") }, actions = {
      IconButton(onClick = {
        preferences.saveWordGroup(wordGroup)
        navController.popBackStack()
      }) {
        Icon(Icons.Filled.Done, "Save")
      }
    })
  }) { paddingValues ->
    Column(modifier = Modifier.padding(paddingValues)) {
      var text by remember { mutableStateOf(wordGroup.name) }
      val focusManager = LocalFocusManager.current
      Spacer(modifier = Modifier.height(8.dp))
      OutlinedTextField(
        value = text,
        label = { Text("Add Grouping") },
        onValueChange = { text = it },
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 8.dp),
        keyboardOptions = KeyboardOptions(
          capitalization = KeyboardCapitalization.Sentences,
          imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(onDone = {
          wordGroup = wordGroup.copy(name = text)
          focusManager.clearFocus(true)
        })
      )
      Spacer(modifier = Modifier.height(8.dp))
      TranslationsList(wordGroup) { translation, checked ->
        wordGroup = if (checked) {
          wordGroup.copy(translations = wordGroup.translations + translation)
        } else {
          wordGroup.copy(translations = wordGroup.translations - translation)
        }
      }
    }
  }
}

@Composable
private fun TranslationsList(
  wordGroup: WordGroup,
  onTranslationToggled: (Translation, Boolean) -> Unit
) {
  val translations = TranslationBuddyPreferences(LocalContext.current).getTranslations()
  LazyColumn(
    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    items(translations) { translation ->
      TranslationItem(
        translation,
        onTranslationToggled,
        wordGroup.translations.contains(translation)
      )
      Divider()
    }
  }
}

@Composable
private fun TranslationItem(
  translation: Translation,
  onToggle: (Translation, Boolean) -> Unit,
  isSelected: Boolean
) {
  Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
    Text(
      text = "${translation.english} -> ${translation.nepali}",
      style = TextStyle(fontSize = 24.sp)
    )
    Checkbox(checked = isSelected, onCheckedChange = { onToggle(translation, it) })
  }
}
