package com.example.palette.data.socket

import com.google.gson.*
import java.lang.reflect.Type
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class ZonedDateTimeAdapter : JsonSerializer<ZonedDateTime>, JsonDeserializer<ZonedDateTime> {
    override fun serialize(
        src: ZonedDateTime,
        typeOfSrc: Type,
        context: JsonSerializationContext
    ): JsonElement {
        return JsonPrimitive(src.format(DateTimeFormatter.ISO_ZONED_DATE_TIME))
    }

    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): ZonedDateTime {
        return ZonedDateTime.parse(json.asString, DateTimeFormatter.ISO_ZONED_DATE_TIME)
    }
}