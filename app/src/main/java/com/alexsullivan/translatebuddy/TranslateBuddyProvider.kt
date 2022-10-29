package com.alexsullivan.translatebuddy

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews
import com.alexsullivan.translatebuddy.storage.TranslationBuddyPreferences


class TranslateBuddyProvider : AppWidgetProvider() {
  override fun onReceive(context: Context, intent: Intent) {
    super.onReceive(context, intent)
    if (intent.action == NEXT_TRANSLATION_ACTION) {
      val prefs = TranslationBuddyPreferences(context)
      val currentItem = prefs.getCurrentTranslation()
      val translations = prefs.getTranslations()
      Log.d("DEBUGGG:", "Translations: $translations")
      val newIndex = (translations.indexOf(currentItem) + 1) % translations.lastIndex
      Log.d("DEBUGGG:", "New index: $newIndex")
      val newTranslation = translations[newIndex]
      val views = RemoteViews(context.packageName, R.layout.translate_widget)
      views.setTextViewText(R.id.nepali_text, newTranslation.nepali)
      views.setTextViewText(R.id.english_text, newTranslation.english)
      prefs.saveCurrentTranslation(newTranslation)
      val newIntent = getPendingSelfIntent(context, NEXT_TRANSLATION_ACTION)
      views.setOnClickPendingIntent(R.id.widget_root, newIntent)

      val ids: IntArray = AppWidgetManager.getInstance(context)
        .getAppWidgetIds(ComponentName(context, TranslateBuddyProvider::class.java))

      val appWidgetManager = AppWidgetManager.getInstance(context)
      appWidgetManager.updateAppWidget(ids[0], views)
    }
  }

  companion object {
    const val NEXT_TRANSLATION_ACTION = "NextTranslationAction"
    fun updateInitialWidget(context: Context, appWidgetId: Int) {
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