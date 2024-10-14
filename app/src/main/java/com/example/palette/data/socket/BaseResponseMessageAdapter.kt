package com.example.palette.data.socket

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

//object BaseResponseMessageSerializer :
//    JsonContentPolymorphicSerializer<BaseResponseMessage>(BaseResponseMessage::class) {
//    override fun selectDeserializer(element: JsonElement): DeserializationStrategy<BaseResponseMessage> {
//        val type = element.jsonObject["type"]?.jsonPrimitive?.content
//        return when (type) {
//            "NEW_CHAT" -> BaseResponseMessage.ChatMessage.serializer()
//            "ERROR" -> BaseResponseMessage.ErrorMessage.serializer()
//            else -> throw SerializationException("Unknown type: $type")
//        }
//    }
//}

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