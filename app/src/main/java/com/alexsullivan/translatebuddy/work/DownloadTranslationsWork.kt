package com.alexsullivan.translatebuddy.work

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.alexsullivan.translatebuddy.drive.GetTranslationsUseCase
import com.alexsullivan.translatebuddy.drive.Translation
import com.alexsullivan.translatebuddy.storage.TranslationBuddyPreferences
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

class DownloadTranslationsWork(appContext: Context, workerParams: WorkerParameters) :
  Worker(appContext, workerParams) {
  override fun doWork(): Result {
    val translations = GetTranslationsUseCase().invoke(applicationContext)
    TranslationBuddyPreferences(applicationContext).saveTranslations(translations)
    return Result.success()
  }
}