package com.api.palette.ui.main.create.chat

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
import android.widget.NumberPicker
import android.widget.TextView
import androidx.activity.addCallback
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.api.palette.R
import com.api.palette.application.PaletteApplication
import com.api.palette.data.chat.ChatRequestManager
import com.api.palette.data.chat.qna.ChatAnswer
import com.api.palette.data.chat.qna.ChatQuestion
import com.api.palette.data.chat.qna.PromptData
import com.api.palette.data.error.CustomException
import com.api.palette.data.room.RoomRequestManager
import com.api.palette.data.room.data.TitleData
import com.api.palette.data.socket.BaseResponseMessage
import com.api.palette.data.socket.ChatResource
import com.api.palette.data.socket.MessageResponse
import com.api.palette.data.socket.WebSocketManager
import com.api.palette.databinding.FragmentChattingBinding
import com.api.palette.ui.base.BaseControllable
import com.api.palette.ui.main.create.chat.adapter.ChattingRecyclerAdapter
import com.api.palette.ui.util.log
import com.api.palette.ui.util.logE
import com.api.palette.ui.util.shortToast
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import java.util.Timer
import java.util.TimerTask

class ChattingFragment(
    private val roomId: Int,
    private val title: String,
    private val isFirst: Boolean = false,
) : Fragment() {
    private lateinit var binding: FragmentChattingBinding
    private val recyclerAdapter: ChattingRecyclerAdapter by lazy {
        ChattingRecyclerAdapter()
    }
    private var chatList: MutableList<MessageResponse> = mutableListOf()
    private var qnaList: MutableList<PromptData> = mutableListOf()
    private var isLoading = false
    private lateinit var webSocketManager: WebSocketManager
    private lateinit var sendType: String
    private val pingTimer = Timer()

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
                                if (System.currentTimeMillis() - connection > 2000) {
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
                    when (chatMessage) {
                        is BaseResponseMessage.ChatMessage -> {
                            chatList.add(
                                MessageResponse(
                                    id = chatMessage.id,
                                    promptId = chatMessage.promptId,
                                    message = chatMessage.message,
                                    roomId = chatMessage.roomId,
                                    userId = chatMessage.userId,
                                    datetime = chatMessage.datetime,
                                    resource = chatMessage.resource,
                                    regenScope = chatMessage.regenScope,
                                    isAi = chatMessage.isAi
                                )
                            )
                            recyclerAdapter.setData(chatList)

                            handleChatMessage()
                        }

                        is BaseResponseMessage.GenerateStatusMessage -> {
                            log("generating : ${chatMessage.generating}")
//                            if (chatMessage.generating) {
//                                handleLoadingVisible(true)
//                            } else {
//                                handleLoadingVisible(false)
//                            }
                            handleCurrentPositionVisible(
                                chatMessage.generating,
                                chatMessage.position.toString(),
                            )
                        }

                        else -> return@launch
                    }
                }
            }
            webSocketManager.start()
            startPing()
        } catch (e: Exception) {
            logE("WebSocketManager 생성 중 오류 발생: ${e.localizedMessage}")
        }

        initView()
        initEditText()
        return binding.root
    }

    private fun initView() {
        binding.chattingToolbar.title = title

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            if (chatList.isEmpty() || qnaList.isEmpty()) return@addCallback
            requireActivity().supportFragmentManager.popBackStack()
        }

        loadQnaData()

        // 백 스택에서 프래그먼트 제거
        binding.chattingToolbar.setNavigationOnClickListener {
            if (chatList.isEmpty() || qnaList.isEmpty()) return@setNavigationOnClickListener
            requireActivity().supportFragmentManager.popBackStack()
        }

        binding.chattingToolbar.setOnClickListener { showChangeTitleDialog() }

        binding.chattingSubmitButton.setOnClickListener {
            sendData()
        }

        binding.regenButton.setOnClickListener {
            handleRegenButtonVisible(false)
            viewLifecycleOwner.lifecycleScope.launch {
                val response = RoomRequestManager.regenRoom(PaletteApplication.prefs.token, roomId)
                if (!response.isSuccessful) {
                    shortToast("재생성 실패: wi-fi를 확인해주세요")
                }
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

    private fun sendData() {
        if (binding.chattingEditText.text.isEmpty()) return

        val chat: ChatAnswer
        val input = binding.chattingEditText.text.toString()

        when (sendType) {
            "SELECTABLE" -> {
                chat = ChatAnswer.SelectableAnswer(
                    choiceId = input,
                    type = sendType
                )
            }

            "GRID" -> {
                chat = ChatAnswer.GridAnswer(
                    choice = input.split(",").map { it.toInt() },
                    type = sendType
                )
            }

            "USER_INPUT" -> {
                chat = ChatAnswer.UserInputAnswer(
                    input = input,
                    type = sendType
                )
            }

            else -> return
        }

        viewLifecycleOwner.lifecycleScope.launch {
            ChatRequestManager.createChat(
                PaletteApplication.prefs.token,
                roomId = roomId,
                chat = chat
            )
            binding.chattingEditText.text.clear()
        }
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
                loadChatData()
            }

            listOf(qnaLoader, chatLoader).awaitAll()

            recyclerAdapter.setQnAList(qnaList)
            recyclerAdapter.setData(chatList)
            binding.chattingRecycler.scrollToPosition(chatList.size - 1)

            log("ChattingFragment initView \nqnaList: $qnaList\nchatList: $chatList")



            if (chatList.isEmpty()) {
                managementInputTool(qnaList[0])
                return@launch
            }
            handleChatMessage()
        }
    }

    private fun managementInputTool(qna: PromptData) {
        when (qna) {
            is PromptData.Selectable -> {
                binding.chattingSelectLayout.visibility = View.VISIBLE
                binding.chattingTextBox.visibility = View.GONE
                sendType = "SELECTABLE"
                updateSelectableUI(qna)
            }

            is PromptData.Grid -> {
                binding.chattingSelectLayout.visibility = View.VISIBLE
                binding.chattingTextBox.visibility = View.GONE
                sendType = "GRID"
                updateGridUI(qna)
            }

            is PromptData.UserInput -> {
                sendType = "USER_INPUT"
                binding.chattingTextBox.visibility = View.VISIBLE
                binding.chattingSelectLayout.visibility = View.GONE
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

            if (newChats.isNullOrEmpty()) return@launch

            newChats.reverse()
            chatList.addAll(0, newChats)
            recyclerAdapter.setData(chatList)

            isLoading = false // 로딩 종료 플래그 설정
        }
    }

    private fun initEditText() {
        binding.chattingEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                // EditText 내용이 변경된 후 호출됩니다.
                if (s.isNullOrBlank()) {
                    binding.chattingSubmitButton.setBackgroundResource(R.drawable.bac_circle_gray)
                    binding.chattingSubmitButton.setImageResource(R.drawable.ic_send)
                } else {
                    binding.chattingSubmitButton.setBackgroundResource(R.drawable.bac_circle_blue)
                    binding.chattingSubmitButton.setImageResource(R.drawable.ic_send_ok)
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun handleChatMessage() {
        if (chatList.isEmpty()) return

        binding.chattingRecycler.smoothScrollToPosition(recyclerAdapter.itemCount - 1)
        if (!chatList.last().isAi) return // 내 채팅일 경우 핸들링 X
        val lastMessage = chatList.last()

        if (lastMessage.resource == ChatResource.PROMPT) { // prompt 질의응답 식일 경우
            val qna = qnaList.find { it.id == lastMessage.promptId!! }!!
            handleCurrentPositionVisible(false)
//            handleLoadingVisible(false)
            managementInputTool(qna)
        } else { // 그냥 메세지일 경우
            if (lastMessage.regenScope) {
//                handleLoadingVisible(false)
                handleCurrentPositionVisible(false, "")
                handleRegenButtonVisible(true)
            }
        }
    }

    private fun handleCurrentPositionVisible(
        visibleState: Boolean,
        position: String = "",
    ) {
        with(binding) {
            positionBox.visibility = if (visibleState) View.VISIBLE else View.GONE
            positionLabel.visibility = if (position == "init") {
                currentPositionText.text = "대기열 받아오는 중.."
                View.GONE
            } else {
                currentPositionText.text = position
                View.VISIBLE
            }
            if (position == "0") {
                positionLabel.visibility = View.GONE
                currentPositionText.text = "그리는 중.."
                (positionBox.layoutParams as? ViewGroup.MarginLayoutParams)?.bottomMargin = 20
            }
        }
    }

    private fun handleRegenButtonVisible(visibleState: Boolean) {
        binding.regenButton.visibility = if (visibleState) View.VISIBLE else View.GONE
    }

    private fun handleLoadingVisible(visibleState: Boolean) {
        if (visibleState && chatList.last().resource != ChatResource.INTERNAL_IMAGE_LOADING) {
            chatList.add(
                MessageResponse(
                    id = "",
                    promptId = null,
                    message = "",
                    roomId = roomId,
                    userId = 0,
                    datetime = Clock.System.now(),
                    resource = ChatResource.INTERNAL_IMAGE_LOADING,
                    isAi = true,
                    regenScope = false
                )
            )
            recyclerAdapter.setData(chatList)
            binding.chattingRecycler.smoothScrollToPosition(recyclerAdapter.itemCount - 1)
        } else {
            val index = chatList.indexOfFirst { it.resource == ChatResource.INTERNAL_IMAGE_LOADING }
            if (index == -1) return

            chatList.removeAt(index)
            handleCurrentPositionVisible(false)
            handleRegenButtonVisible(true)
            recyclerAdapter.setData(chatList)
        }
    }

    private fun updateSelectableUI(qna: PromptData.Selectable) {
        val selectableQuestion = qna.question as? ChatQuestion.SelectableQuestion

        var selectedChoice = selectableQuestion?.choices?.get(0)?.id ?: "DISPLAY"
        with(binding) {
            chattingSelectLayout.removeAllViews()

            val cardView = CardView(requireContext()).apply {
                radius = 64f
                cardElevation = 12f

                setCardBackgroundColor(ContextCompat.getColor(context, R.color.white))
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(32, 0, 32, 16)
                }
            }

            // 카드뷰 내부 레이아웃
            val cardInnerLayout = LinearLayout(context).apply {
                orientation = LinearLayout.VERTICAL
                gravity = Gravity.CENTER
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                setPadding(64, 64, 64, 64)
            }

            val pickerLayout = LinearLayout(context).apply {
                orientation = LinearLayout.VERTICAL
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )

                setPadding(16, 16, 16, 16)
            }

            val numberPicker = NumberPicker(context).apply {
                wrapSelectorWheel = true
                descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS

                selectableQuestion?.choices?.let { choices ->
                    minValue = 0
                    maxValue = choices.size - 1
                    displayedValues =
                        choices.map { it.displayName }.toTypedArray()  // 리스트를 문자열 배열로 변환하여 설정
                }

                // 값 선택 시 이벤트 리스너 설정
                setOnValueChangedListener { _, _, newVal ->
                    // newVal은 선택된 값의 인덱스
                    selectableQuestion?.choices?.let { choices ->
                        selectedChoice = choices[newVal].id  // 선택된 항목
                        setPadding(15, 15, 15, 15) // 카드뷰 내부 패딩 (텍스트, 그리드, 버튼 간 여백)
                    }
                }
            }

            pickerLayout.addView(numberPicker)

            val instructionText = TextView(context).apply {
                text = "원하시는 포스터의 비율을 선택해 주세요."
                textSize = 18f
                gravity = Gravity.START
                setTextColor(ContextCompat.getColor(context, R.color.black))
                typeface = ResourcesCompat.getFont(context, R.font.pretendard_bold)
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            }

            val submitButton = Button(context).apply {
                text = "선택하기"
                textSize = 16f
                setTextColor(ContextCompat.getColor(context, R.color.white))
                background = ContextCompat.getDrawable(context, R.drawable.bac_button)
                typeface = ResourcesCompat.getFont(context, R.font.pretendard_semibold)
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(0, 25, 0, 25) // 버튼 상단 마진 추가
                    gravity = Gravity.CENTER
                }
                setPadding(100, 20, 100, 20)

                stateListAnimator = null
                elevation = 0f

                setOnClickListener {
                    binding.chattingEditText.setText(selectedChoice)
                    binding.chattingSelectLayout.visibility = View.GONE
                    sendData()
                }
            }

            cardInnerLayout.addView(instructionText)
            cardInnerLayout.addView(pickerLayout)
            cardInnerLayout.addView(submitButton)

            cardView.addView(cardInnerLayout)

            chattingSelectLayout.addView(cardView)
        }
    }

    private fun updateGridUI(qna: PromptData.Grid) {
        val gridQuestion = qna.question as? ChatQuestion.GridQuestion
        val maxCount: Int = gridQuestion!!.maxCount
        hideKeyboard()
        with(binding) {
            chattingSelectLayout.removeAllViews()

            val cardView = CardView(requireContext()).apply {
                radius = 64f
                cardElevation = 12f
                setCardBackgroundColor(ContextCompat.getColor(context, R.color.white))
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(32, 16, 16, 32)
                }
            }

            val cardInnerLayout = LinearLayout(context).apply {
                orientation = LinearLayout.VERTICAL
                gravity = Gravity.CENTER
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                setPadding(64, 64, 64, 64)
            }

            val instructionText = TextView(context).apply {
                text = "원하는 위치를 순서대로 선택해주세요"
                textSize = 18f
                gravity = Gravity.START
                setTextColor(ContextCompat.getColor(context, R.color.black))
                typeface = ResourcesCompat.getFont(context, R.font.pretendard_bold)
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            }

            val gridLayout = GridLayout(context).apply {
                rowCount = gridQuestion.ySize
                columnCount = gridQuestion.xSize
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(0, 32, 0, 32)
                    gravity = Gravity.CENTER
                }
            }

            val maxCountText = TextView(context).apply {
                text = "최대 선택 개수: $maxCount"
                textSize = 14f
                gravity = Gravity.START
                setTextColor(ContextCompat.getColor(context, R.color.black))
                typeface = ResourcesCompat.getFont(context, R.font.pretendard_medium)
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(0, 16, 0, 0)
                }
            }

            val selectedPositions = mutableListOf<Int>()

            gridQuestion.let {
                val buttonSize = resources.getDimensionPixelSize(R.dimen.grid_button_size)

                for (i in 0 until it.ySize) {
                    for (j in 0 until it.xSize) {
                        val position = i * it.xSize + j // 버튼의 인덱스 계산 (행 * 열 수 + 열)

                        val button = Button(context).apply {
                            background =
                                ContextCompat.getDrawable(
                                    context,
                                    R.drawable.bac_grid_item_unselect
                                )
                            layoutParams = GridLayout.LayoutParams().apply {
                                rowSpec = GridLayout.spec(i)
                                columnSpec = GridLayout.spec(j)
                                width = buttonSize
                                height = buttonSize
                                setMargins(10, 10, 10, 10)
                            }

                            setOnClickListener { _ ->
                                if (position in selectedPositions) {
                                    selectedPositions.remove(position)
                                    background = ContextCompat.getDrawable(
                                        context,
                                        R.drawable.bac_grid_item_unselect
                                    )
                                } else {
                                    if (selectedPositions.size < maxCount) {
                                        maxCountText.setTextColor(
                                            ContextCompat.getColor(
                                                context,
                                                R.color.red
                                            )
                                        )
                                        selectedPositions.add(position)
                                        background = ContextCompat.getDrawable(
                                            context,
                                            R.drawable.bac_grid_item_select
                                        )
                                    }
                                }

                                if (selectedPositions.size != maxCount) {
                                    maxCountText.setTextColor(
                                        ContextCompat.getColor(
                                            context,
                                            R.color.black
                                        )
                                    )
                                }
                            }
                        }
                        gridLayout.addView(button)
                    }
                }
            }

            val submitButton = Button(context).apply {
                text = "선택하기"
                textSize = 16f
                setTextColor(ContextCompat.getColor(context, R.color.white))
                typeface = ResourcesCompat.getFont(context, R.font.pretendard_medium)
                background = ContextCompat.getDrawable(context, R.drawable.bac_button)
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(16, 32, 16, 0)
                    gravity = Gravity.CENTER
                }
                setPadding(100, 20, 100, 20)

                stateListAnimator = null
                elevation = 0f

                setOnClickListener {
                    if (selectedPositions.isEmpty()) {
                        shortToast("최소 1개는 선택해야 합니다")
                    } else {
                        updateChattingEditText(selectedPositions)
                        binding.chattingSelectLayout.visibility = View.GONE
                        sendData()
                    }
                }
            }

            cardInnerLayout.addView(instructionText)
            cardInnerLayout.addView(maxCountText)
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
        dialog.setCancelable(true)

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
    }

    @SuppressLint("ServiceCast")
    private fun hideKeyboard() {
        val imm =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.chattingEditText.windowToken, 0)
    }

    private fun startPing() {
        pingTimer.schedule(object : TimerTask() {
            override fun run() {
                if (::webSocketManager.isInitialized) {
                    webSocketManager.send("ping") // 서버에 ping 메시지 전송
                }
            }
        }, 0, 30000) // 30초마다 Ping 전송
    }

    private fun stopPing() {
        pingTimer.cancel()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        webSocketManager.stop() // 프래그먼트 종료 시 WebSocket 연결 해제
        stopPing()

        if (chatList.isEmpty()) {
            (requireActivity() as? BaseControllable)?.deleteRoom(roomId = roomId)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        (requireActivity() as? BaseControllable)?.bottomVisible(true)
    }
}