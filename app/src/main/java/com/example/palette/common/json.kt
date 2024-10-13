package com.example.palette.common

import com.example.palette.data.socket.BaseResponseMessage
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

val module = SerializersModule {
    polymorphic(BaseResponseMessage::class) {
        subclass(BaseResponseMessage.ChatMessage::class)
        subclass(BaseResponseMessage.ErrorMessage::class)
    }
}

val json = Json {
//    ignoreUnknownKeys = true
//    isLenient = true
//    allowSpecialFloatingPointValues = true
//    encodeDefaults = true
//    classDiscriminator = "type"
//    serializersModule = module

    ignoreUnknownKeys = true
    isLenient = true
    allowSpecialFloatingPointValues = true
    encodeDefaults = true
}