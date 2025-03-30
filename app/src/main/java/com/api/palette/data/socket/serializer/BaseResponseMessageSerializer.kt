package com.api.palette.data.socket.serializer

import com.api.palette.data.socket.data.BaseResponseMessage
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

class BaseResponseMessageSerializer :
    JsonContentPolymorphicSerializer<BaseResponseMessage>(BaseResponseMessage::class) {
    override fun selectDeserializer(element: JsonElement): DeserializationStrategy<out BaseResponseMessage> {
        return when (element.jsonObject["type"]?.jsonPrimitive?.content) {
            "NEW_CHAT" -> BaseResponseMessage.ChatMessage.serializer()
            "ERROR" -> BaseResponseMessage.ErrorMessage.serializer()
            "GENERATE_STATUS" -> BaseResponseMessage.GenerateStatusMessage.serializer()
            "IMAGE_PROGRESS" -> BaseResponseMessage.ImageProgressMessage.serializer()
            else -> throw SerializationException("Unknown type: ${element.jsonObject["type"]}")
        }
    }
}
