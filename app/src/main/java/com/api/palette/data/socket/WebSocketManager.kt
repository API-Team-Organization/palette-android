package com.api.palette.data.socket

import com.api.palette.common.json
import com.api.palette.data.socket.data.BaseResponseMessage
import com.api.palette.presentation.util.log
import com.api.palette.presentation.util.logE
import kotlinx.serialization.SerializationException
import okhttp3.*

class WebSocketManager(token: String, roomId: String) {
    private val client = OkHttpClient()
    private lateinit var webSocket: WebSocket
    private var onMessageReceived: ((BaseResponseMessage) -> Unit)? = null
    private var onConnect: (() -> Unit)? = null

    private val request: Request = Request.Builder()
        .url("wss://api.paletteapp.xyz/ws/$roomId")
        .addHeader("X-AUTH-TOKEN", token)
        .build()

    private val listener = object : WebSocketListener() {
        override fun onOpen(webSocket: WebSocket, response: Response) {
            log("WebSocket 연결 성공")
            onConnect?.invoke()
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            log("WebSocket 수신된 메시지: $text")
            try {
                val baseMessage: BaseResponseMessage = json.decodeFromString(text)
                when (baseMessage) {
                    is BaseResponseMessage.ErrorMessage -> handleErrorMessage(baseMessage)
                    is BaseResponseMessage.ChatMessage -> {
                        log("WebSocketManager onMessage 변환 결과: $baseMessage")
                        onMessageReceived?.invoke(baseMessage)
                    }
                    is BaseResponseMessage.GenerateStatusMessage,
                    is BaseResponseMessage.ImageProgressMessage -> {
                        onMessageReceived?.invoke(baseMessage)
                    }
                }
            } catch (e: SerializationException) {
                logE("WebSocket 메시지 파싱 오류: ${e.message}")
            }
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            logE("WebSocket 오류 발생: ${t.localizedMessage}")
        }
    }

    fun start() {
        webSocket = client.newWebSocket(request, listener)
    }

    fun stop() {
        webSocket.close(1000, "종료")
    }

    fun setOnMessageReceivedListener(listener: (BaseResponseMessage) -> Unit) {
        log("WebSocketManager 메시지 리스너 등록: $listener")
        this.onMessageReceived = listener
    }

    fun setOnConnect(listener: () -> Unit) {
        log("WebSocketManager 연결 리스너 등록: $listener")
        this.onConnect = listener
    }

    private fun handleErrorMessage(errorMessage: BaseResponseMessage.ErrorMessage) {
        logE("WebSocket 에러 메시지: ${errorMessage.message}")
        // 에러 핸들링 로직 추가 가능
    }

    fun send(message: String) {
        if (::webSocket.isInitialized) {
            webSocket.send(message)
            log("WebSocket 메시지 전송: $message")
        } else {
            logE("WebSocket이 초기화되지 않았습니다.")
        }
    }
}
