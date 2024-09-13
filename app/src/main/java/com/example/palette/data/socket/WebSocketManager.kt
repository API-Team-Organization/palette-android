package com.example.palette.data.socket

import com.example.palette.ui.util.log
import com.example.palette.ui.util.logE
import com.google.gson.GsonBuilder
import com.google.gson.JsonParseException
import com.google.gson.JsonSyntaxException
import okhttp3.*

class WebSocketManager(token: String, roomId: Int) {
    private val client = OkHttpClient()
    private lateinit var webSocket: WebSocket
    private val gson = GsonBuilder()
        .registerTypeAdapter(BaseResponseMessage::class.java, BaseResponseMessageAdapter())
        .create()
    private var onMessageReceived: ((BaseResponseMessage.ChatMessage) -> Unit)? = null

    private val request: Request = Request.Builder()
        .url("wss://api.paletteapp.xyz/ws/${roomId}")
        .addHeader("X-AUTH-TOKEN", token) // 토큰 추가
        .build()

    private val listener = object : WebSocketListener() {
        override fun onOpen(webSocket: WebSocket, response: Response) {
            log("WebSocket 웹소켓 연결 성공")
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            log("WebSocket 수신된 메시지: $text")

            try {
                if ("ERROR" in text) {
                    val baseMessage =
                        gson.fromJson(text, BaseResponseMessage.ErrorMessage::class.java)
                    handleErrorMessage(baseMessage)
                } else if ("NEW_CHAT" in text) {
                    val baseMessage =
                        gson.fromJson(text, BaseResponseMessage.ChatMessage::class.java)
                    log("WebSocketManager onMessage json변환 value : $baseMessage")
                    onMessageReceived?.invoke(baseMessage)
                }

            } catch (e: JsonSyntaxException) {
                logE("WebSocket 메시지 파싱 오류: ${e.message}")
            } catch (e: JsonParseException) {
                logE("WebSocket JSON 파싱 오류: ${e.message}")
            }
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            logE("WebSocket 웹소켓 오류 발생: ${t.localizedMessage}")
        }
    }

    fun start() {
        webSocket = client.newWebSocket(request, listener)
    }

    fun stop() {
        webSocket.close(1000, "종료")
    }

    fun setOnMessageReceivedListener(listener: (BaseResponseMessage.ChatMessage) -> Unit) {
        log(" WebSocketManager setOnMessageReceivedListener listener $listener")
        this.onMessageReceived = listener
    }

    private fun handleErrorMessage(errorMessage: BaseResponseMessage.ErrorMessage) {
        logE("WebSocket 에러 메시지: ${errorMessage.message}")
        // 에러 처리 로직
    }
}