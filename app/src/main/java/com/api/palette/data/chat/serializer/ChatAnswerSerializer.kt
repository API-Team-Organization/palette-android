package com.api.palette.data.chat.serializer

import com.api.palette.domain.chat.model.ChatAnswer
import com.api.palette.data.socket.data.PromptType
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.*

object ChatAnswerSerializer : JsonContentPolymorphicSerializer<ChatAnswer>(ChatAnswer::class) {
    override fun selectDeserializer(element: JsonElement): DeserializationStrategy<ChatAnswer> {
        val type = element.jsonObject["type"]?.jsonPrimitive?.content
            ?.let { PromptType.valueOf(it) }
            ?: throw SerializationException("Invalid or missing 'type': $element")

        return when (type) {
            PromptType.SELECTABLE -> ChatAnswer.SelectableAnswer.serializer()
            PromptType.GRID -> ChatAnswer.GridAnswer.serializer()
            PromptType.USER_INPUT -> ChatAnswer.UserInputAnswer.serializer()
        }
    }
}
