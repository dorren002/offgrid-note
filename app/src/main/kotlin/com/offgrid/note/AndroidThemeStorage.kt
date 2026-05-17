package com.offgrid.note

import android.content.Context
import com.offgrid.note.ui.theme.SharedPreferencesWrapper
import com.offgrid.note.ui.theme.ThemeStorage

object AndroidThemeStorage {
    fun init(context: Context) {
        val prefs = context.getSharedPreferences("offgrid_theme", Context.MODE_PRIVATE)
        ThemeStorage.init(object : SharedPreferencesWrapper {
            override fun getLong(key: String, default: Long): Long {
                return prefs.getLong(key, default)
            }
            
            override fun putLong(key: String, value: Long) {
                prefs.edit().putLong(key, value).apply()
            }
            
            override fun getBoolean(key: String, default: Boolean): Boolean {
                return prefs.getBoolean(key, default)
            }
            
            override fun putBoolean(key: String, value: Boolean) {
                prefs.edit().putBoolean(key, value).apply()
            }
            
            override fun getString(key: String, default: String?): String? {
                return prefs.getString(key, default)
            }
            
            override fun putString(key: String, value: String) {
                prefs.edit().putString(key, value).apply()
            }
        })
    }
}