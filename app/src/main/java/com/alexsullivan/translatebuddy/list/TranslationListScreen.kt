package com.alexsullivan.translatebuddy.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.alexsullivan.translatebuddy.drive.Translation
import com.alexsullivan.translatebuddy.storage.TranslationBuddyPreferences

@Composable
fun TranslationListScreen(navController: NavController) {
  Scaffold(topBar = { TopAppBar(title = { Text("Translations") }) }) {
    val translations = TranslationBuddyPreferences(LocalContext.current).getTranslations()
    LazyColumn(
      modifier = Modifier.padding(it),
      contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      items(translations) { translation ->
        TranslationItem(translation)
        Divider()
      }
    }
  }
}

@Composable
private fun TranslationItem(translation: Translation) {
  Text(
    text = "${translation.english} -> ${translation.nepali}",
    style = TextStyle(fontSize = 24.sp)
  )
}
