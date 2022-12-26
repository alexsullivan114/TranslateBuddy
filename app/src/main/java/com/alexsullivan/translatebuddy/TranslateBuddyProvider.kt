package com.alexsullivan.translatebuddy

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews
import com.alexsullivan.translatebuddy.drive.Translation
import com.alexsullivan.translatebuddy.storage.TranslationBuddyPreferences


class TranslateBuddyProvider : AppWidgetProvider() {
  override fun onReceive(context: Context, intent: Intent) {
    super.onReceive(context, intent)
    Log.d("DEBUGGG:", "In onReceive with intent action ${intent.action}")
    val prefs = TranslationBuddyPreferences(context)
    if (intent.action == NEXT_TRANSLATION_ACTION) {
      val newTranslation = getNextTranslation(prefs)
      Log.d("DEBUGGG:", "New translation: $newTranslation")
      updateWidgetWithTranslation(newTranslation, context)
      prefs.saveCurrentTranslation(newTranslation)
    } else {
      updateWidgetWithTranslation(getNextTranslation(prefs), context)
    }
  }

  private fun updateWidgetWithTranslation(newTranslation: Translation, context: Context) {
    val views = RemoteViews(context.packageName, R.layout.translate_widget)
    views.setTextViewText(R.id.nepali_text, newTranslation.nepali)
    views.setTextViewText(R.id.english_text, newTranslation.english)
    val newIntent = getPendingSelfIntent(context, NEXT_TRANSLATION_ACTION)
    views.setOnClickPendingIntent(R.id.widget_root, newIntent)

    val ids: IntArray = AppWidgetManager.getInstance(context)
      .getAppWidgetIds(ComponentName(context, TranslateBuddyProvider::class.java))

    val appWidgetManager = AppWidgetManager.getInstance(context)
    ids.forEach { appWidgetManager.updateAppWidget(it, views) }
  }

  private fun getNextTranslation(prefs: TranslationBuddyPreferences): Translation {
    val currentItem = prefs.getCurrentTranslation()
    val translations = getSelectedGroupTranslations(prefs)
    Log.d("DEBUGGG:", "Old translation: $currentItem")
    Log.d("DEBUGGG:", "Old index: ${translations.indexOf(currentItem)}")
    Log.d("DEBUGGG:", "Translations last index: ${translations.lastIndex}")
    val newIndex = (translations.indexOf(currentItem) + 1) % translations.size
    Log.d("DEBUGGG:", "New index: $newIndex")
    return translations[newIndex]
  }

  private fun getSelectedGroupTranslations(prefs: TranslationBuddyPreferences): List<Translation> {
    return prefs.getWordGroups()
      .firstOrNull { it.id == prefs.getSelectedWordGroupId() }?.translations
      ?: prefs.getTranslations()
  }

  companion object {
    const val NEXT_TRANSLATION_ACTION = "NextTranslationAction"
    fun updateInitialWidget(context: Context, appWidgetId: Int) {
      Log.d("DEBUGGG:", "In update initial widget")
      val appWidgetManager = AppWidgetManager.getInstance(context)
      val views = RemoteViews(context.packageName, R.layout.translate_widget)
      val intent = getPendingSelfIntent(context, NEXT_TRANSLATION_ACTION)
      views.setOnClickPendingIntent(R.id.widget_root, intent)
      appWidgetManager.updateAppWidget(appWidgetId, views)
    }

    private fun getPendingSelfIntent(context: Context, action: String): PendingIntent {
      val intent = Intent(context, TranslateBuddyProvider::class.java)
      intent.action = action
      return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
    }
  }
}
