package com.example.palette.data.chat.qna

import com.example.palette.data.socket.PromptType
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

@Serializable
sealed class ChatAnswer(val type: PromptType) {
    @Serializable
    data class SelectableAnswer(val choiceId: String) : ChatAnswer(PromptType.SELECTABLE)

    @Serializable
    data class GridAnswer(val choice: List<Int>) : ChatAnswer(PromptType.GRID)

    @Serializable
    data class UserInputAnswer(val input: String) : ChatAnswer(PromptType.USER_INPUT)

    object ChatAnswerSerializer : JsonContentPolymorphicSerializer<ChatAnswer>(ChatAnswer::class) {
        override fun selectDeserializer(element: JsonElement): DeserializationStrategy<ChatAnswer> {
            val m = element.jsonObject["type"]?.jsonPrimitive?.content?.let { PromptType.valueOf(it) }
                ?: throw SerializationException("$element")
            return when (m) {
                PromptType.SELECTABLE -> SelectableAnswer.serializer()
                PromptType.GRID -> GridAnswer.serializer()
                PromptType.USER_INPUT -> UserInputAnswer.serializer()
            }
        }
    }
}