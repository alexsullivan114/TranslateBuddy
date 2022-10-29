package com.alexsullivan.translatebuddy

import android.app.job.JobScheduler
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.Text
import androidx.lifecycle.lifecycleScope
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.alexsullivan.translatebuddy.drive.GetTranslationsUseCase
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
      Text("Signing in...")
    }

    signIn()
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