package com.api.palette.data.chat.serializer

import com.api.palette.domain.chat.model.PromptData
import com.api.palette.data.socket.data.PromptType
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.*

object PromptDataSerializer : JsonContentPolymorphicSerializer<PromptData>(PromptData::class) {
    override fun selectDeserializer(element: JsonElement): DeserializationStrategy<PromptData> {
        val type = element.jsonObject["type"]?.jsonPrimitive?.content
            ?.let { PromptType.valueOf(it) }
            ?: throw SerializationException("Missing or invalid 'type': $element")

        return when (type) {
            PromptType.SELECTABLE -> PromptData.Selectable.serializer()
            PromptType.GRID       -> PromptData.Grid.serializer()
            PromptType.USER_INPUT -> PromptData.UserInput.serializer()
        }
    }
}
