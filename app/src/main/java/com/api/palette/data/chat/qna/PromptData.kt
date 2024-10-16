package com.api.palette.data.chat.qna

import com.api.palette.data.socket.PromptType
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

@Serializable(with = PromptData.PromptDataSerializer::class)
sealed interface PromptData {
    val type: PromptType
    val question: ChatQuestion
    val answer: ChatAnswer?
    val promptName: String
    val id: String

    @Serializable
    data class Selectable(
        override val id: String,
        override val promptName: String,
        override val question: ChatQuestion.SelectableQuestion,
        override val answer: ChatAnswer.SelectableAnswer? = null
    ) : PromptData {
        override val type: PromptType = PromptType.SELECTABLE

        @Serializable
        data class Choice(val id: String, val displayName: String)
    }

    @Serializable
    data class Grid(
        override val id: String,
        override val promptName: String,
        override val question: ChatQuestion.GridQuestion,
        override val answer: ChatAnswer.GridAnswer? = null
    ) : PromptData {
        override val type: PromptType = PromptType.GRID
    }

    @Serializable
    data class UserInput(
        override val id: String,
        override val promptName: String,
        override val answer: ChatAnswer.UserInputAnswer? = null
    ) : PromptData {
        override val question = ChatQuestion.UserInputQuestion
        override val type: PromptType = PromptType.USER_INPUT
    }

    object PromptDataSerializer : JsonContentPolymorphicSerializer<PromptData>(PromptData::class) {
        override fun selectDeserializer(element: JsonElement): DeserializationStrategy<PromptData> {
            val m = element.jsonObject["type"]?.jsonPrimitive?.content?.let { PromptType.valueOf(it) }
                ?: throw SerializationException("$element")
            return when (m) {
                PromptType.SELECTABLE -> Selectable.serializer()
                PromptType.GRID -> Grid.serializer()
                PromptType.USER_INPUT -> UserInput.serializer()
            }
        }

    }
}