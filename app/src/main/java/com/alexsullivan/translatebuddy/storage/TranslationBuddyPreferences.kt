package com.alexsullivan.translatebuddy.storage

import android.content.Context
import android.content.SharedPreferences
import com.alexsullivan.translatebuddy.drive.Translation
import com.alexsullivan.translatebuddy.grouping.WordGroup
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

class TranslationBuddyPreferences(context: Context) {
  private val prefs: SharedPreferences

  init {
    prefs = context.getSharedPreferences("TranslationBuddy", Context.MODE_PRIVATE)
  }

  fun saveTranslations(translations: List<Translation>) {
    val type = Types.newParameterizedType(List::class.java, Translation::class.java)
    val serialized = moshi.adapter<List<Translation>>(type).toJson(translations)
    prefs.edit().putString(TRANSLATIONS_KEY, serialized).apply()
  }

  fun getTranslations(): List<Translation> {
    val serialized = prefs.getString(TRANSLATIONS_KEY, null) ?: return emptyList()
    val type = Types.newParameterizedType(List::class.java, Translation::class.java)
    return moshi.adapter<List<Translation>>(type).fromJson(serialized) ?: emptyList()
  }

  fun saveCurrentTranslation(translation: Translation) {
    val serialized = moshi.adapter(Translation::class.java).toJson(translation)
    prefs.edit().putString(CURRENT_TRANSLATION_KEY, serialized).apply()
  }

  fun getCurrentTranslation(): Translation? {
    val serialized = prefs.getString(CURRENT_TRANSLATION_KEY, null)
    return if (serialized == null) {
      null
    } else {
      moshi.adapter(Translation::class.java).fromJson(serialized)
    }
  }

  fun saveWordGroup(wordGroup: WordGroup) {
    val wordGroups = getWordGroups()
    val newWordGroups = listOf(wordGroup) + wordGroups
    val type = Types.newParameterizedType(List::class.java, WordGroup::class.java)
    val serialized = moshi.adapter<List<WordGroup>>(type).toJson(newWordGroups)
    prefs.edit().putString(WORD_GROUPS_KEY, serialized).apply()
  }

  fun getWordGroups(): List<WordGroup> {
    val serialized = prefs.getString(WORD_GROUPS_KEY, null) ?: return emptyList()
    val type = Types.newParameterizedType(List::class.java, WordGroup::class.java)
    return moshi.adapter<List<WordGroup>>(type).fromJson(serialized) ?: emptyList()
  }

  companion object {
    private const val TRANSLATIONS_KEY = "translations"
    private const val CURRENT_TRANSLATION_KEY = "current_translation"
    private const val WORD_GROUPS_KEY = "groupings"
    private val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
  }
}
