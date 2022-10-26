package com.alexsullivan.translatebuddy

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews


class TranslateBuddyProvider : AppWidgetProvider() {
  override fun onReceive(context: Context, intent: Intent) {
    super.onReceive(context, intent)
    if (intent.action == NEXT_TRANSLATION_ACTION) {
      Log.d("DEBUGGG:", "In action block")
      val views = RemoteViews(context.packageName, R.layout.translate_widget)
      views.setTextViewText(R.id.nepali_text, "Woofers Nepali")
      views.setTextViewText(R.id.english_text, "Woofers English")
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