package com.alexsullivan.translatebuddy

import android.app.job.JobScheduler
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.alexsullivan.translatebuddy.drive.GetTranslationsUseCase
import com.alexsullivan.translatebuddy.drive.Translation
import com.alexsullivan.translatebuddy.storage.TranslationBuddyPreferences
import com.alexsullivan.translatebuddy.work.DownloadTranslationsWork
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import com.google.api.services.drive.DriveScopes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class LaunchActivity : ComponentActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      TranslationList()
    }

    signIn()
  }

  @Composable
  private fun TranslationList() {
    val translations = TranslationBuddyPreferences(applicationContext).getTranslations()
    LazyColumn(
      contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      items(translations) { translation ->
        TranslationItem(translation)
        Divider()
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

  private fun isUserSignedIn(): Boolean {
    val account = GoogleSignIn.getLastSignedInAccount(this)
    return account != null
  }
  private fun signIn() {
    if (!isUserSignedIn()) {
      val gso = GoogleSignInOptions
        .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestEmail()
        .requestProfile()
        .requestScopes(Scope(DriveScopes.DRIVE))
        .build()

      val googleSignInClient = GoogleSignIn.getClient(this, gso)
      val signInIntent = googleSignInClient.signInIntent
      startActivityForResult(signInIntent, CONST_SIGN_IN)
    } else {
      registerWork()
    }
  }

  private fun registerWork() {
    val downloadTranslationsRequest =
      PeriodicWorkRequestBuilder<DownloadTranslationsWork>(1, TimeUnit.HOURS).build()
    WorkManager.getInstance(this).enqueue(downloadTranslationsRequest)
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    if (requestCode == CONST_SIGN_IN && data != null) {
      handleSignData(data)
    }
  }

  private fun handleSignData(data: Intent) {
    GoogleSignIn.getSignedInAccountFromIntent(data)
      .addOnCompleteListener {
        if (it.isSuccessful){
          Log.d("DEBUGGG:", "Successful login")
          registerWork()
        } else {
          Log.d("DEBUGGG:", "Failed login")
        }
      }
  }

  companion object {
    const val CONST_SIGN_IN: Int = 1001
  }
}