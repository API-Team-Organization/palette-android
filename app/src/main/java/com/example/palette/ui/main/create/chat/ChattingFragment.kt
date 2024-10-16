package com.example.palette.ui.main.create.chat

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.GridLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.palette.R
import com.example.palette.application.PaletteApplication
import com.example.palette.data.chat.ChatRequestManager
import com.example.palette.data.chat.qna.ChatAnswer
import com.example.palette.data.chat.qna.ChatQuestion
import com.example.palette.data.chat.qna.PromptData
import com.example.palette.data.error.CustomException
import com.example.palette.data.room.RoomRequestManager
import com.example.palette.data.room.data.TitleData
import com.example.palette.data.socket.BaseResponseMessage
import com.example.palette.data.socket.MessageResponse
import com.example.palette.data.socket.WebSocketManager
import com.example.palette.databinding.FragmentChattingBinding
import com.example.palette.ui.base.BaseControllable
import com.example.palette.ui.main.create.chat.adapter.ChattingRecyclerAdapter
import com.example.palette.ui.util.log
import com.example.palette.ui.util.logE
import com.example.palette.ui.util.shortToast
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ChattingFragment(
    private val roomId: Int,
    private val title: String,
    private val isFirst: Boolean = false,
    private val messageList: MutableList<MessageResponse>
) : Fragment() {
    private lateinit var binding: FragmentChattingBinding
    private val recyclerAdapter: ChattingRecyclerAdapter by lazy {
        ChattingRecyclerAdapter()
    }
    private var chatList: MutableList<MessageResponse> = messageList
    private var qnaList: MutableList<PromptData> = mutableListOf()
    private var isLoading = false
    private lateinit var webSocketManager: WebSocketManager
    private var sendData: String = "basic value"
    private lateinit var sendType: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        var firstMsgReceived = false
        binding = FragmentChattingBinding.inflate(inflater, container, false)

        // WebSocket 연결
        try {
            webSocketManager = WebSocketManager(PaletteApplication.prefs.token, roomId)
            if (isFirst) {
                val connection = System.currentTimeMillis()
                webSocketManager.setOnConnect {
                    viewLifecycleOwner.lifecycleScope.launch {
                        val m = async {
                            while (isFirst && !firstMsgReceived && chatList.isEmpty()) {
                                if (System.currentTimeMillis() - connection > 3000) {
                                    delay(500L)
                                    loadChatData()
                                }
                            }
                        }
                        m.join()

                        recyclerAdapter.setData(chatList)
                    }
                }
            }
            webSocketManager.setOnMessageReceivedListener { chatMessage ->
                // UI 스레드에서 안전하게 업데이트
                log("ChattingFragment onCreateView handleChatMessage 호출됨")
                firstMsgReceived = true
                viewLifecycleOwner.lifecycleScope.launch {
                    handleChatMessage(chatMessage)
                }
            }
            webSocketManager.start()
        } catch (e: Exception) {
            logE("WebSocketManager 생성 중 오류 발생: ${e.localizedMessage}")
        }

        initView()
        initEditText()
        return binding.root
    }

    private fun initView() {
        binding.chattingToolbar.title = title

        loadQnaData()

        // 백 스택에서 프래그먼트 제거
        binding.chattingToolbar.setNavigationOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        binding.chattingToolbar.setOnClickListener { showChangeTitleDialog() }

        binding.chattingSubmitButton.setOnClickListener {
            if (binding.chattingEditText.text.isEmpty()) return@setOnClickListener

            val chat: ChatAnswer

            when (sendType) {
                "SELECTABLE" -> {
                    chat = ChatAnswer.SelectableAnswer(
                        choiceId = sendData,
                        type = "SELECTABLE"
                    )
                }

                "GRID" -> {
                    chat = ChatAnswer.GridAnswer(
                        choice = binding.chattingEditText.text.split(",").map { it.toInt() },
                        type = sendType
                    )
                }

                "USER_INPUT" -> {
                    chat = ChatAnswer.UserInputAnswer(
                        input = binding.chattingEditText.text.toString(),
                        type = sendType
                    )
                }

                else -> return@setOnClickListener
            }

            viewLifecycleOwner.lifecycleScope.launch {
                sendMessage(roomId, chat) // Retrofit으로 채팅 메시지 전송
                binding.chattingEditText.text.clear()
            }
        }

        binding.chattingRecycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                // 최상단에 도달하고 데이터 로드 중이 아니며, 데이터를 모두 로드하지 않았다면
                if (binding.chattingRecycler.canScrollVertically(-1)) return
                if (isLoading) return
                if (chatList.isEmpty()) return

                isLoading = true // 로딩 시작 플래그 설정

                log("chatList : $chatList")
                val firstMessageTime = chatList[0].datetime
                log("firstMessageTime : $firstMessageTime")
                loadMoreChats(firstMessageTime.toString())
            }
        })

        binding.chattingRecycler.apply {
            adapter = recyclerAdapter
            layoutManager = LinearLayoutManager(
                context,
                LinearLayoutManager.VERTICAL,
                false
            )
        }
        (requireActivity() as? BaseControllable)?.bottomVisible(false)
    }

    private suspend fun loadChatData() {
        try {
            val it = ChatRequestManager.getChatList( // getChatList failed -> return EmptyList
                token = PaletteApplication.prefs.token,
                roomId = roomId,
                before = null
            )?.data ?: emptyList()

            chatList.addAll((it).reversed())
        } catch (e: CustomException) {
            shortToast(e.errorResponse.message)
        }
    }

    private fun loadQnaData() {
        viewLifecycleOwner.lifecycleScope.launch {
            val qnaLoader = async {
                val qna = try {
                    ChatRequestManager.getQnAList(PaletteApplication.prefs.token, roomId)
                } catch (e: CustomException) {
                    shortToast(e.message!!)
                    null
                }
                qnaList.addAll(qna?.data ?: emptyList())
            }

            val chatLoader = async {
                if (messageList.isEmpty()) {
                    loadChatData()
                } else {
                    null
                }
            }

            listOf(qnaLoader, chatLoader).awaitAll()

            recyclerAdapter.setQnAList(qnaList)
            recyclerAdapter.setData(chatList)
            binding.chattingRecycler.scrollToPosition(chatList.size - 1)

            log("ChattingFragment initView \nqnaList: $qnaList\nchatList: $chatList")
            val qna: PromptData?

            if (chatList.isEmpty()) {
                qna = qnaList[0]
            } else {
                val lastMessage = chatList.last()
                if (chatList.last().promptId == null) return@launch
                qna = qnaList.find { it.id == lastMessage.promptId }!!
            }

            managementInputTool(qna)
        }
    }

    private fun managementInputTool(qna: PromptData) {
        when (qna) {
            is PromptData.Selectable -> {
                sendType = "SELECTABLE"
                val selectableQuestion = qna.question as? ChatQuestion.SelectableQuestion
                with(binding) {
                    chattingSelectLayout.visibility = View.VISIBLE
                    chattingSelectLayout.removeAllViews()
                    selectableQuestion?.choices?.forEach { choice ->
                        val button = Button(context).apply {
                            text = choice.displayName
                            background = ContextCompat.getDrawable(context, R.drawable.bac_auth)
                            elevation = 0f
                            val layoutParams = LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                            ).apply {
                                setMargins(8, 0, 8, 0)
                            }
                            this.layoutParams = layoutParams
                            setOnClickListener {
                                binding.chattingEditText.setText(choice.displayName)
                                sendData = choice.id
                            }
                        }
                        chattingSelectLayout.addView(button)
                    }
                }
            }

            is PromptData.Grid -> {
                sendType = "GRID"
            }

            is PromptData.UserInput -> {
                sendType = "USER_INPUT"
            }
        }
    }

    private fun loadMoreChats(before: String) {
        viewLifecycleOwner.lifecycleScope.launch {
            val newChats = ChatRequestManager.getChatList(
                token = PaletteApplication.prefs.token,
                roomId = roomId,
                before = before,
            )?.data

            if (newChats.isNullOrEmpty()) {
                shortToast("더 이상 불러올 채팅이 없습니다.")
            } else {
                newChats.reverse()
                chatList.addAll(0, newChats)
                recyclerAdapter.setData(chatList)
            }

            isLoading = false // 로딩 종료 플래그 설정
        }
    }

    private fun initEditText() {
        binding.chattingEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                // EditText 내용이 변경된 후 호출됩니다.
                if (s.isNullOrBlank()) {
                    binding.chattingSubmitButton.setImageResource(R.drawable.ic_arrow_upward_gray)
                } else {
                    binding.chattingSubmitButton.setImageResource(R.drawable.ic_arrow_upward)
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private suspend fun sendMessage(roomId: Int, data: ChatAnswer) {
        val chat: ChatAnswer

        when (data) {
            is ChatAnswer.GridAnswer -> {
                chat = ChatAnswer.GridAnswer(
                    choice = data.choice,
                    type = "GRID"
                )
            }

            is ChatAnswer.SelectableAnswer -> {
                chat = ChatAnswer.SelectableAnswer(
                    choiceId = data.choiceId,
                    type = "SELECTABLE"
                )
            }

            is ChatAnswer.UserInputAnswer -> {
                chat = ChatAnswer.UserInputAnswer(
                    input = data.input,
                    type = "USER_INPUT"
                )
            }
        }

        log("서버로 보내는 값이다 : $chat")

        ChatRequestManager.createChat(
            PaletteApplication.prefs.token,
            roomId = roomId,
            chat = chat
        )
    }

    private fun handleChatMessage(chatMessage: BaseResponseMessage.ChatMessage) {
        chatList.add(
            MessageResponse(
                id = chatMessage.id,
                promptId = chatMessage.promptId,
                message = chatMessage.message,
                roomId = chatMessage.roomId,
                userId = chatMessage.userId,
                datetime = chatMessage.datetime,
                resource = chatMessage.resource,
                isAi = chatMessage.isAi
            )
        )
        recyclerAdapter.setData(chatList)

        if (chatList.isEmpty()) return

        if (chatList.last().isAi && chatList.last().promptId != null) {
            val lastMessage = chatList.last()
            val qna = qnaList.find { it.id == lastMessage.promptId }!!

            managementInputTool(qna)
        }

        binding.chattingRecycler.smoothScrollToPosition(recyclerAdapter.itemCount - 1)

        updateAnswerMethod()
    }

    private fun updateAnswerMethod() {
        if (chatList.isEmpty()) return

        val lastMessage = chatList.last()
        val qna = qnaList.find { it.id == lastMessage.promptId } ?: return

        when (qna) {
            is PromptData.Selectable -> {
                updateSelectableUI(qna)
            }
            is PromptData.Grid -> {
                updateGridUI(qna)
            }
            is PromptData.UserInput -> {
                binding.chattingSelectLayout.visibility = View.GONE
            }
        }
    }

    private fun updateSelectableUI(qna: PromptData.Selectable) {
        val selectableQuestion = qna.question as? ChatQuestion.SelectableQuestion
        with(binding) {
            chattingSelectLayout.visibility = View.VISIBLE
            chattingSelectLayout.removeAllViews()
            selectableQuestion?.choices?.forEach { choice ->
                val button = Button(context).apply {
                    text = choice.displayName
                    background = ContextCompat.getDrawable(context, R.drawable.bac_auth)
                    elevation = 0f
                    val layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        setMargins(8, 0, 8, 0)
                    }
                    this.layoutParams = layoutParams
                    setOnClickListener {
                        binding.chattingEditText.setText(choice.displayName)
                        sendData = choice.id
                    }
                }
                chattingSelectLayout.addView(button)
            }
        }
    }

    private fun updateGridUI(qna: PromptData.Grid) {
        val gridQuestion = qna.question as? ChatQuestion.GridQuestion
        hideKeyboard()
        with(binding) {
            chattingSelectLayout.visibility = View.VISIBLE
            chattingTextBox.visibility = View.GONE
            chattingSelectLayout.removeAllViews()

            val cardView = CardView(requireContext()).apply {
                radius = 24f
                cardElevation = 12f
                setCardBackgroundColor(ContextCompat.getColor(context, R.color.white))
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(16, 16, 16, 16)
                }
            }

            val cardInnerLayout = LinearLayout(context).apply {
                orientation = LinearLayout.VERTICAL
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                setPadding(16, 16, 16, 16)
            }

            val instructionText = TextView(context).apply {
                text = "원하는 위치를 순서대로 선택해주세요"
                textSize = 18f
                setTextColor(ContextCompat.getColor(context, R.color.black))
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            }

            val gridLayout = GridLayout(context).apply {
                rowCount = gridQuestion?.xSize ?: 3
                columnCount = gridQuestion?.ySize ?: 3
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(0, 16, 0, 0)
                    gravity = Gravity.CENTER
                }
            }

            val selectedPositions = mutableListOf<Int>()

            gridQuestion?.let {
                val buttonSize = resources.getDimensionPixelSize(R.dimen.grid_button_size)

                for (i in 0 until (it.xSize * it.ySize)) {
                    val button = Button(context).apply {
                        background = ContextCompat.getDrawable(context, R.drawable.bac_grid_item_unselect)
                        layoutParams = GridLayout.LayoutParams().apply {
                            rowSpec = GridLayout.spec(i / it.ySize)
                            columnSpec = GridLayout.spec(i % it.ySize)
                            width = buttonSize
                            height = buttonSize
                            setMargins(10, 10, 10, 10)
                        }

                        setOnClickListener { _ ->
                            if (i in selectedPositions) {
                                selectedPositions.remove(i)
                                background = ContextCompat.getDrawable(context, R.drawable.bac_grid_item_unselect)
                            } else {
                                selectedPositions.add(i)
                                background = ContextCompat.getDrawable(context, R.drawable.bac_grid_item_select)
                            }
                        }
                    }
                    gridLayout.addView(button)
                }
            }

            val submitButton = Button(context).apply {
                text = "답변하기"
                textSize = 16f
                setTextColor(ContextCompat.getColor(context, R.color.white))
                background = ContextCompat.getDrawable(context, R.drawable.bac_button)
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(16, 32, 16, 0)
                    gravity = Gravity.CENTER
                }
                setPadding(0, 20, 0, 20)

                setOnClickListener {
                    updateChattingEditText(selectedPositions)

                    chattingSelectLayout.visibility = View.GONE
                }
            }

            cardInnerLayout.addView(instructionText)
            cardInnerLayout.addView(gridLayout)
            cardInnerLayout.addView(submitButton)

            cardView.addView(cardInnerLayout)

            chattingSelectLayout.addView(cardView)
        }
    }


    private fun updateChattingEditText(selectedPositions: List<Int>) {
        val orderedPositions = selectedPositions.joinToString(",")
        binding.chattingEditText.setText(orderedPositions)
    }

    private fun showChangeTitleDialog() {
        val builder = AlertDialog.Builder(requireContext())

        val inflater = LayoutInflater.from(requireContext())
        val dialogView = inflater.inflate(R.layout.dialog_edit_text, null)

        val input = dialogView.findViewById<EditText>(R.id.etChangeTitle)
        val applyButton = dialogView.findViewById<TextView>(R.id.tv_apply)

        input.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                input.backgroundTintList =
                    ContextCompat.getColorStateList(requireContext(), R.color.blue)
            } else {
                input.backgroundTintList =
                    ContextCompat.getColorStateList(requireContext(), R.color.black)
            }
        }

        builder.setView(dialogView)

        val dialog = builder.create()

        applyButton.setOnClickListener {
            val newTitle = input.text.toString()

            if (newTitle.isBlank()) {
                shortToast("제목을 입력해주세요")
                return@setOnClickListener
            } else {
                viewLifecycleOwner.lifecycleScope.launch {
                    RoomRequestManager.setRoomTitle(
                        PaletteApplication.prefs.token,
                        title = TitleData(newTitle),
                        roomId = roomId
                    )
                    binding.chattingToolbar.title = newTitle
                }
                dialog.dismiss()
            }
        }
        dialog.setCancelable(false)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
    }

    @SuppressLint("ServiceCast")
    private fun hideKeyboard() {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.chattingEditText.windowToken, 0)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        webSocketManager.stop() // 프래그먼트 종료 시 WebSocket 연결 해제

        if (chatList.isEmpty()) {
            (requireActivity() as? BaseControllable)?.deleteRoom(roomId = roomId)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        (requireActivity() as? BaseControllable)?.bottomVisible(true)
    }
}