package com.example.palette.data.socket

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import java.lang.reflect.Type

class BaseResponseMessageAdapter : JsonDeserializer<PromptData> {
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): PromptData {
        val jsonObject = json.asJsonObject
        val type = jsonObject.get("resource").asString

        return when {
            type.endsWith(PromptType.USER_INPUT.name) -> context.deserialize(json, PromptData.UserInput::class.java)
            type.endsWith(PromptType.SELECTABLE.name) -> context.deserialize(json, PromptData.Selectable::class.java)
            type.endsWith(PromptType.GRID.name) -> context.deserialize(json, PromptData.Grid::class.java)
            else -> throw JsonParseException("Unknown type: $type")
        }
    }
}

// BEFORE CHANGE CHAT LOGIC PROMPT

//class PromptDataMessageAdapter : JsonDeserializer<PromptData> {
//    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): BaseResponseMessage {
//        val jsonObject = json.asJsonObject
//        val type = jsonObject.get("type").asString
//
//        return when (type) {
//            MessageType.NEW_CHAT.name -> context.deserialize(json, BaseResponseMessage.ChatMessage::class.java)
//            MessageType.ERROR.name -> context.deserialize(json, BaseResponseMessage.ErrorMessage::class.java)
//            else -> throw JsonParseException("Unknown type: $type")
//        }
//    }
//}