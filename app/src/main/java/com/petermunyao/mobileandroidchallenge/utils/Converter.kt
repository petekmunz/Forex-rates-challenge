package com.petermunyao.mobileandroidchallenge.utils

import androidx.room.TypeConverter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import java.util.*
import kotlin.collections.HashMap

object Converter {
    @TypeConverter
    @JvmStatic
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    @JvmStatic
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    @JvmStatic
    fun stringToMapString(value: String): Map<String, String> {
        val classType =
            Types.newParameterizedType(Map::class.java, String::class.java, String::class.java)
        val moshi = Moshi.Builder().build()
        val jsonAdapter = moshi.adapter<Map<String, String>>(classType)
        return jsonAdapter.fromJson(value)!!
    }

    @TypeConverter
    @JvmStatic
    fun mapStringToString(value: Map<String, String>?): String {
        val classType =
            Types.newParameterizedType(Map::class.java, String::class.java, String::class.java)
        val moshi = Moshi.Builder().build()
        val jsonAdapter = moshi.adapter<Map<String, String>>(classType)
        return if (value == null) "" else jsonAdapter.toJson(value)
    }

    @TypeConverter
    @JvmStatic
    fun stringToMapFloat(value: String): Map<String, Float> {
        val classType =
            Types.newParameterizedType(Map::class.java, String::class.java, Float::class.java)
        val moshi = Moshi.Builder().build()
        val jsonAdapter = moshi.adapter<Map<String, Float>>(classType)
        return jsonAdapter.fromJson(value)!!
    }

    @TypeConverter
    @JvmStatic
    fun mapFloatToString(value: Map<String, Float>?): String {
        val classType =
            Types.newParameterizedType(Map::class.java, String::class.java, Float::class.java)
        val moshi = Moshi.Builder().build()
        val jsonAdapter = moshi.adapter<Map<String, Float>>(classType)
        return if (value == null) "" else jsonAdapter.toJson(value)
    }
}