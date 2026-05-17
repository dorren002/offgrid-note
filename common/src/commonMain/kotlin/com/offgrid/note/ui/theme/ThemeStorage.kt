package com.offgrid.note.ui.theme

import androidx.compose.ui.graphics.Color

object ThemeStorage {
    private const val PREFS_NAME = "offgrid_theme"
    private const val KEY_BACKGROUND = "theme_background"
    private const val KEY_BORDER = "theme_border"
    private const val KEY_TEXT = "theme_text"
    private const val KEY_IS_DARK = "theme_is_dark"
    private const val KEY_SCENE_ID = "current_scene_id"
    
    private var prefs: SharedPreferencesWrapper? = null
    
    fun init(prefsWrapper: SharedPreferencesWrapper) {
        this.prefs = prefsWrapper
    }
    
    fun saveTheme(theme: OffgridThemeData) {
        prefs?.apply {
            putLong(KEY_BACKGROUND, theme.background.value.toLong())
            putLong(KEY_BORDER, theme.border.value.toLong())
            putLong(KEY_TEXT, theme.text.value.toLong())
            putBoolean(KEY_IS_DARK, theme.isDark)
        }
    }
    
    fun loadTheme(): OffgridThemeData? {
        return try {
            prefs?.let {
                val background = it.getLong(KEY_BACKGROUND, Long.MIN_VALUE)
                val border = it.getLong(KEY_BORDER, Long.MIN_VALUE)
                val text = it.getLong(KEY_TEXT, Long.MIN_VALUE)
                
                if (background == Long.MIN_VALUE || border == Long.MIN_VALUE || text == Long.MIN_VALUE) {
                    return null
                }
                
                val isDark = it.getBoolean(KEY_IS_DARK, true)
                OffgridThemeData(
                    background = Color(background.toULong()),
                    border = Color(border.toULong()),
                    text = Color(text.toULong()),
                    isDark = isDark
                )
            }
        } catch (e: Exception) {
            null
        }
    }
    
    fun saveSceneId(sceneId: String) {
        prefs?.putString(KEY_SCENE_ID, sceneId)
    }
    
    fun loadSceneId(): String? {
        return try {
            prefs?.getString(KEY_SCENE_ID, null)
        } catch (e: Exception) {
            null
        }
    }
}

interface SharedPreferencesWrapper {
    fun getLong(key: String, default: Long): Long
    fun putLong(key: String, value: Long)
    fun getBoolean(key: String, default: Boolean): Boolean
    fun putBoolean(key: String, value: Boolean)
    fun getString(key: String, default: String?): String?
    fun putString(key: String, value: String)
}