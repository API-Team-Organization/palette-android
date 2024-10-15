package com.example.palette.data.chat.qna

import com.example.palette.data.socket.PromptType
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

@Serializable(with = ChatAnswer.ChatAnswerSerializer::class)
sealed class ChatAnswer {
    @Serializable
    data class SelectableAnswer(
        val choiceId: String,
        val type: String
    ) : ChatAnswer()

    @Serializable
    data class GridAnswer(
        val choice: List<Int>,
        val type: String
    ) : ChatAnswer()

    @Serializable
    data class UserInputAnswer(
        val input: String,
        val type: String
    ) : ChatAnswer()

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