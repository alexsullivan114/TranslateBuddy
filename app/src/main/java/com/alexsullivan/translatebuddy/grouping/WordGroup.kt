package com.alexsullivan.translatebuddy.grouping

import com.alexsullivan.translatebuddy.drive.Translation

data class WordGroup(val id: String, val name: String, val translations: List<Translation>)
