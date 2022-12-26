package com.alexsullivan.translatebuddy.utils

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import com.alexsullivan.translatebuddy.TranslateBuddyProvider

fun refreshWidget(context: Context) {
  val intent = Intent(context, TranslateBuddyProvider::class.java)
  intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
  val ids: IntArray = AppWidgetManager.getInstance(context.applicationContext)
    .getAppWidgetIds(ComponentName(context.applicationContext, TranslateBuddyProvider::class.java))
  intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
  context.sendBroadcast(intent)
}
