package com.api.palette.data.socket.serializer

import com.api.palette.data.socket.data.BaseResponseMessage
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.*

class BaseResponseMessageSerializer :
    JsonContentPolymorphicSerializer<BaseResponseMessage>(BaseResponseMessage::class) {

    override fun selectDeserializer(element: JsonElement): DeserializationStrategy<out BaseResponseMessage> {
        val type = element.jsonObject["type"]?.jsonPrimitive?.content
            ?: throw SerializationException("Missing 'type' in: $element")

        return when (type) {
            "NEW_CHAT"        -> BaseResponseMessage.ChatMessage.serializer()
            "ERROR"           -> BaseResponseMessage.ErrorMessage.serializer()
            "GENERATE_STATUS" -> BaseResponseMessage.GenerateStatusMessage.serializer()
            "IMAGE_PROGRESS"  -> BaseResponseMessage.ImageProgressMessage.serializer()
            else              -> throw SerializationException("Unknown type: $type")
        }
    }
}
