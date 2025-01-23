package com.nsicyber.notificatipsum.data.local

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.nsicyber.notificatipsum.domain.model.RepeatInterval
import com.nsicyber.notificatipsum.domain.model.WeekDay
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class Converters {
    private val gson = Gson()
    private val weekDaySetType = object : TypeToken<Set<WeekDay>>() {}.type
    private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

    @TypeConverter
    fun fromTimestamp(value: String?): LocalDateTime? {
        return value?.let { LocalDateTime.parse(it, dateFormatter) }
    }

    @TypeConverter
    fun dateToTimestamp(date: LocalDateTime?): String? {
        return date?.format(dateFormatter)
    }

    @TypeConverter
    fun fromWeekDaySet(value: Set<WeekDay>?): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toWeekDaySet(value: String?): Set<WeekDay> {
        return try {
            gson.fromJson(value, weekDaySetType) ?: emptySet()
        } catch (e: Exception) {
            emptySet()
        }
    }

    @TypeConverter
    fun fromRepeatInterval(value: RepeatInterval?): String {
        return (value ?: RepeatInterval.NONE).name
    }

    @TypeConverter
    fun toRepeatInterval(value: String?): RepeatInterval {
        return try {
            value?.let { RepeatInterval.valueOf(it) } ?: RepeatInterval.NONE
        } catch (e: Exception) {
            RepeatInterval.NONE
        }
    }
} 