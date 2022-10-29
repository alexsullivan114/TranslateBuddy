package com.alexsullivan.translatebuddy.drive

import android.content.Context
import android.util.Log
import com.alexsullivan.translatebuddy.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes

class GetTranslationsUseCase {
  operator fun invoke(context: Context): List<Translation> {
    val csv = downloadFileFromGDrive("1r9rN0z1y7Ab5gwWe28PRI8lUqgVQ_TuF6UcfP_YSQ8Y", context)
    val translations = csv.split('\n').map { translationPair ->
      val pair = translationPair.split(',')
      Translation(pair[0], pair[1])
    }
    return translations
  }

  private fun getDriveService(context: Context): Drive {
    GoogleSignIn.getLastSignedInAccount(context)?.let { googleAccount ->
      val credential = GoogleAccountCredential.usingOAuth2(context, listOf(DriveScopes.DRIVE))
      credential.selectedAccount = googleAccount.account!!
      return Drive
        .Builder(
          AndroidHttp.newCompatibleTransport(),
          JacksonFactory.getDefaultInstance(),
          credential
        )
        .setApplicationName(context.getString(R.string.app_name))
        .build()
    }

    throw RuntimeException("Not logged in")
  }

  private fun downloadFileFromGDrive(id: String, context: Context): String {
    val inputStream = getDriveService(context).Files().export(id, "text/csv").executeMediaAsInputStream()
    return inputStream.bufferedReader().use { it.readText() }
  }

  private fun accessDriveFiles(context: Context) {
    val googleDriveService = getDriveService(context)
      var pageToken: String? = null
      do {
        val result = googleDriveService.files().list().apply {
          spaces = "drive"
          fields = "nextPageToken, files(id, name)"
          pageToken = this.pageToken
        }.execute()
        for (file in result.files) {
          Log.d("DEBUGGG:", "File: name=${file.name} id=${file.id}")
        }
      } while (pageToken != null)
    }
}