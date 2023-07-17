package com.example.habit.data.local.TypeConverters

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type
import java.time.LocalDate
import java.time.format.DateTimeParseException

class LocalDateDeserializer : JsonDeserializer<LocalDate> {
    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): LocalDate? {
        return try {
            if (json != null) {
                LocalDate.parse(json.asString)
            } else {
                null
            }
        } catch (e: DateTimeParseException) {
            null
        }
    }
}