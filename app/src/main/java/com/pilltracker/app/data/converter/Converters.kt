package com.pilltracker.app.data.converter

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    @TypeConverter
    fun fromStringList(value: List<String>): String {
        return try {
            if (value.isEmpty()) {
                "[]"
            } else {
                Gson().toJson(value)
            }
        } catch (e: Exception) {
            android.util.Log.e("Converters", "Error converting list to string: $value", e)
            "[]"
        }
    }

    @TypeConverter
    fun toStringList(value: String): List<String> {
        return try {
            if (value.isBlank()) {
                emptyList()
            } else {
                val listType = object : TypeToken<List<String>>() {}.type
                Gson().fromJson(value, listType) ?: emptyList()
            }
        } catch (e: Exception) {
            android.util.Log.e("Converters", "Error converting string to list: $value", e)
            emptyList()
        }
    }
}