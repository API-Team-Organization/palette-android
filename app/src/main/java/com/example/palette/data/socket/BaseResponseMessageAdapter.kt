package com.example.palette.data.socket

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import java.lang.reflect.Type

class BaseResponseMessageAdapter : JsonDeserializer<BaseResponseMessage> {
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): BaseResponseMessage {
        val jsonObject = json.asJsonObject
        val type = jsonObject.get("type").asString

        return when (type) {
            MessageType.NEW_CHAT.name -> context.deserialize(json, BaseResponseMessage.ChatMessage::class.java)
            MessageType.ERROR.name -> context.deserialize(json, BaseResponseMessage.ErrorMessage::class.java)
            else -> throw JsonParseException("Unknown type: $type")
        }
    }
}