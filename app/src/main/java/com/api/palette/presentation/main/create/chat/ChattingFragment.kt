package com.api.palette.presentation.main.create.chat

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.api.palette.R
import com.api.palette.application.PaletteApplication
import com.api.palette.domain.chat.model.ChatAnswer
import com.api.palette.domain.chat.model.ChatQuestion
import com.api.palette.domain.chat.model.PromptData
import com.api.palette.data.socket.WebSocketManager
import com.api.palette.data.socket.data.BaseResponseMessage
import com.api.palette.domain.socket.model.MessageResponse
import com.api.palette.databinding.FragmentChattingBinding
import com.api.palette.presentation.base.BaseControllable
import com.api.palette.presentation.main.create.chat.adapter.ChattingRecyclerAdapter
import com.api.palette.presentation.main.create.chat.viewmodel.ChatViewModel
import com.api.palette.presentation.main.create.room.viewmodel.RoomViewModel
import com.api.palette.presentation.util.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Timer

@AndroidEntryPoint
class ChattingFragment(
    private val roomId: String,
    private val title: String,
    private val isFirst: Boolean = false
) : Fragment() {

    private var _binding: FragmentChattingBinding? = null
    private val binding get() = _binding!!

    private val recyclerAdapter: ChattingRecyclerAdapter by lazy { ChattingRecyclerAdapter() }
    private var chatList = mutableListOf<MessageResponse>()
    private var qnaList = mutableListOf<PromptData>()
    private var isLoading = false

    private lateinit var webSocketManager: WebSocketManager
    private lateinit var sendType: String
    private val pingTimer = Timer()

    private val chatViewModel: ChatViewModel by viewModels()
    private val roomViewModel: RoomViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChattingBinding.inflate(inflater, container, false)

        setupBackPressedCallback()
        setupWebSocket()

        initView()
        initEditText()
        return binding.root
    }

    private fun setupBackPressedCallback() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (chatList.isNotEmpty() && qnaList.isNotEmpty()) {
                        requireActivity().supportFragmentManager.popBackStack()
                    }
                }
            }
        )
    }

    private fun setupWebSocket() {
        try {
            webSocketManager = WebSocketManager(PaletteApplication.prefs.token, roomId)
            if (isFirst) {
                val connectionTime = System.currentTimeMillis()
                webSocketManager.setOnConnect {
                    viewLifecycleOwner.lifecycleScope.launch {
                        val asyncTask = async {
                            while (isFirst && !firstMessageReceived() && chatList.isEmpty()) {
                                if (System.currentTimeMillis() - connectionTime > 2000) {
                                    delay(500L)
                                    loadChatData()
                                }
                            }
                        }
                        asyncTask.await()
                        recyclerAdapter.setData(chatList)
                    }
                }
            }
            webSocketManager.setOnMessageReceivedListener { chatMessage ->
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
                            handleCurrentPositionVisible(chatMessage.generating, chatMessage.position.toString())
                        }
                        is BaseResponseMessage.ImageProgressMessage -> {
                            handleProgressBar(chatMessage.value, chatMessage.max)
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
    }

    private fun firstMessageReceived(): Boolean = chatList.isNotEmpty()

    private fun initView() {
        binding.chattingToolbar.title = title
        loadQnaData()

        binding.chattingToolbar.setNavigationOnClickListener {
            if (chatList.isNotEmpty() && qnaList.isNotEmpty()) {
                requireActivity().supportFragmentManager.popBackStack()
            }
        }

        binding.chattingToolbar.setOnClickListener { showChangeTitleDialog() }
        binding.chattingSubmitButton.setOnClickListener { sendData() }

        binding.regenButton.setOnClickListener {
            handleRegenButtonVisible(false)
            roomViewModel.regenRoom(PaletteApplication.prefs.token, roomId) { result ->
                if (result.isSuccess) {
                    shortToast("재생성 완료")
                } else {
                    shortToast("재생성 실패: ${result.exceptionOrNull()?.message}")
                }
            }
        }

        binding.chattingRecycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (binding.chattingRecycler.canScrollVertically(-1)) return
                if (isLoading || chatList.isEmpty()) return
                isLoading = true
                val firstMessageTime = chatList.first().datetime.toString()
                loadMoreChats(firstMessageTime)
            }
        })

        binding.chattingRecycler.apply {
            adapter = recyclerAdapter
            layoutManager = LinearLayoutManager(context)
        }
        (requireActivity() as? BaseControllable)?.bottomVisible(false)
    }

    private fun initEditText() {
        binding.chattingEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s.isNullOrBlank()) {
                    binding.chattingSubmitButton.setBackgroundResource(R.drawable.bac_circle_light_gray)
                    binding.chattingSubmitButton.setImageResource(R.drawable.ic_action_send)
                } else {
                    binding.chattingSubmitButton.setBackgroundResource(R.drawable.bac_circle_primary)
                    binding.chattingSubmitButton.setImageResource(R.drawable.ic_send_confirmed)
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private suspend fun loadChatData() {
        chatViewModel.loadChatList(
            token = PaletteApplication.prefs.token,
            roomId = roomId,
            before = null,
            size = 10
        ) { shortToast("채팅 목록 로딩 오류: ${it.message}") }
        delay(300L)
        chatList.clear()
        chatViewModel.chatList.value?.let { chatList.addAll(it.reversed()) }
    }

    private fun loadQnaData() {
        viewLifecycleOwner.lifecycleScope.launch {
            val qnaLoader = async {
                chatViewModel.loadQnAList(
                    token = PaletteApplication.prefs.token,
                    roomId = roomId
                ) { shortToast("QnA 로딩 오류: ${it.message}") }
            }
            val chatLoader = async { loadChatData() }
            listOf(qnaLoader, chatLoader).awaitAll()

            qnaList.clear()
            qnaList.addAll(chatViewModel.qnaList.value.orEmpty())

            recyclerAdapter.setQnAList(qnaList)
            recyclerAdapter.setData(chatList)
            binding.chattingRecycler.scrollToPosition(chatList.size)

            val qna = if (chatList.isEmpty()) {
                qnaList.firstOrNull()
            } else {
                qnaList.find { it.id == chatList.last().promptId } ?: qnaList.firstOrNull()
            }
            qna?.let { managementInputTool(it) }
        }
    }

    private fun loadMoreChats(before: String) {
        chatViewModel.loadChatList(
            token = PaletteApplication.prefs.token,
            roomId = roomId,
            before = before,
            size = 10
        ) { shortToast("추가 채팅 로딩 오류: ${it.message}") }
        viewLifecycleOwner.lifecycleScope.launch {
            delay(300L)
            val newChats = chatViewModel.chatList.value?.reversed()?.toMutableList() ?: mutableListOf()
            if (newChats.isEmpty()) {
                isLoading = false
                return@launch
            }
            val firstExistingTime = chatList.first().datetime
            val toAdd = newChats.filter { it.datetime < firstExistingTime }
            chatList.addAll(0, toAdd)
            recyclerAdapter.setData(chatList)
            isLoading = false
        }
    }

    private fun sendData() {
        if (binding.chattingEditText.text.isEmpty()) return
        val input = binding.chattingEditText.text.toString()

        val chat: ChatAnswer = when (sendType) {
            "SELECTABLE" -> ChatAnswer.SelectableAnswer(choiceId = input, type = sendType)
            "GRID" -> ChatAnswer.GridAnswer(choice = input.split(",").map { it.toInt() }, type = sendType)
            "USER_INPUT" -> {
                binding.chattingTextBox.visibility = View.GONE
                ChatAnswer.UserInputAnswer(input = input, type = sendType)
            }
            else -> return
        }

        chatViewModel.sendChat(
            token = PaletteApplication.prefs.token,
            roomId = roomId,
            chat = chat
        ) {
            if (it.isSuccess) {
                binding.chattingEditText.text.clear()
            } else {
                shortToast("채팅 전송 실패: ${it.exceptionOrNull()?.message}")
            }
        }
    }

    private fun handleChatMessage() {
        if (chatList.isEmpty()) return
        val lastMessage = chatList.last()

        if (lastMessage.promptId != null) {
            val qna = qnaList.find { it.id == lastMessage.promptId } ?: qnaList.firstOrNull()
            handleCurrentPositionVisible(false)
            qna?.let { managementInputTool(it) }
        } else if (lastMessage.regenScope) {
            handleCurrentPositionVisible(false, "")
            handleRegenButtonVisible(true)
        }
        binding.chattingRecycler.smoothScrollToPosition(recyclerAdapter.itemCount)
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

    private fun handleCurrentPositionVisible(visibleState: Boolean, position: String = "") {
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
                currentPositionText.text = "제작 중.."
                handleProgressBar(0, 6)
            }
            if (position == "complete") {
                positionLabel.visibility = View.GONE
                currentPositionText.text = "제작 완료!"
            }
        }
    }

    private fun handleProgressBar(value: Int, max: Int) {
        with(binding) {
            if (max == value) {
                progressBar.visibility = View.GONE
                handleCurrentPositionVisible(true, "complete")
            } else {
                progressBar.visibility = View.VISIBLE
                progressBar.setBigMax(6)
                progressBar.animateTo(value)
            }
        }
    }

    private fun handleRegenButtonVisible(visibleState: Boolean) {
        binding.regenButton.visibility = if (visibleState) View.VISIBLE else View.GONE
    }

    private fun updateSelectableUI(qna: PromptData.Selectable) {
        val selectableQuestion = qna.question as? ChatQuestion.SelectableQuestion
        var selectedChoice = selectableQuestion?.choices?.get(0)?.id ?: "DISPLAY"
        binding.chattingSelectLayout.removeAllViews()

        val cardView = CardView(requireContext()).apply {
            radius = 64f
            cardElevation = 12f
            setCardBackgroundColor(ContextCompat.getColor(context, R.color.white))
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { setMargins(32, 0, 32, 16) }
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

        val pickerLayout = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            setPadding(16, 16, 16, 16)
        }

        val preViewLayout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            ).apply { setMargins(0, 0, 32, 0) }
        }

        val numberPicker = NumberPicker(context).apply {
            wrapSelectorWheel = true
            descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
            selectableQuestion?.choices?.let { choices ->
                minValue = 0
                maxValue = choices.size - 1
                displayedValues = choices.map { it.displayName }.toTypedArray()
            }
            val newCardView = createCardForChoice(selectedChoice)
            newCardView?.let { preViewLayout.addView(it) }

            setOnValueChangedListener { _, _, newVal ->
                selectableQuestion?.choices?.let { choices ->
                    selectedChoice = choices[newVal].id
                    preViewLayout.removeAllViews()
                    val updatedCard = createCardForChoice(selectedChoice)
                    updatedCard?.let { preViewLayout.addView(it) }
                }
            }
        }

        pickerLayout.apply {
            addView(preViewLayout)
            addView(numberPicker)
        }

        val instructionText = TextView(context).apply {
            text = "선택지 중 한가지를 선택해주세요."
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
            background = ContextCompat.getDrawable(context, R.drawable.bac_button_solid)
            typeface = ResourcesCompat.getFont(context, R.font.pretendard_semibold)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 25, 0, 25)
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

        cardInnerLayout.apply {
            addView(instructionText)
            addView(pickerLayout)
            addView(submitButton)
        }
        cardView.addView(cardInnerLayout)
        binding.chattingSelectLayout.addView(cardView)
    }

    private fun createCardForChoice(selectedChoice: String): CardView? {
        return when (selectedChoice) {
            "DISPLAY" -> createCardView(requireContext(), 16f, 9f, "16:9 DISPLAY")
            "PAPER"   -> createCardView(requireContext(), 1f, 1.41f, "1:1.41 PAPER")
            "SQUARE"  -> createCardView(requireContext(), 1f, 1f, "1:1 SQUARE")
            "TABLET"  -> createCardView(requireContext(), 4f, 3f, "4:3 TABLET")
            else -> null
        }
    }

    private fun createCardView(context: Context, widthRatio: Float, heightRatio: Float, textValue: String): CardView {
        return CardView(context).apply {
            radius = 16f
            setCardBackgroundColor(ContextCompat.getColor(context, R.color.lightGray))
            layoutParams = LinearLayout.LayoutParams(
                400,
                (400 * heightRatio / widthRatio).toInt()
            ).apply { setMargins(16, 16, 16, 16) }
            addView(TextView(context).apply {
                text = textValue
                textSize = 16f
                gravity = Gravity.CENTER
                setTextColor(ContextCompat.getColor(context, R.color.primaryColor))
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
                )
            })
        }
    }

    private fun updateGridUI(qna: PromptData.Grid) {
        val gridQuestion = qna.question
        val maxCount = gridQuestion.maxCount
        hideKeyboard()
        binding.chattingSelectLayout.removeAllViews()

        val cardView = CardView(requireContext()).apply {
            radius = 64f
            cardElevation = 12f
            setCardBackgroundColor(ContextCompat.getColor(context, R.color.white))
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { setMargins(32, 16, 16, 32) }
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
            text = "원하는 제목의 위치를 선택해주세요"
            textSize = 18f
            gravity = Gravity.START
            setTextColor(ContextCompat.getColor(context, R.color.black))
            typeface = ResourcesCompat.getFont(context, R.font.pretendard_bold)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
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
            ).apply { setMargins(0, 16, 0, 0) }
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

        val selectedPositions = mutableListOf<Int>()
        val buttonSize = resources.getDimensionPixelSize(R.dimen.grid_button_size)

        for (i in 0 until gridQuestion.ySize) {
            for (j in 0 until gridQuestion.xSize) {
                val position = i * gridQuestion.xSize + j
                val button = Button(context).apply {
                    background = ContextCompat.getDrawable(context, R.drawable.bac_grid_item_unselected)
                    layoutParams = GridLayout.LayoutParams().apply {
                        rowSpec = GridLayout.spec(i)
                        columnSpec = GridLayout.spec(j)
                        width = buttonSize
                        height = buttonSize
                        setMargins(10, 10, 10, 10)
                    }
                    setOnClickListener {
                        if (position in selectedPositions) {
                            selectedPositions.remove(position)
                            background = ContextCompat.getDrawable(context, R.drawable.bac_grid_item_unselected)
                        } else {
                            if (selectedPositions.size < maxCount) {
                                selectedPositions.add(position)
                                background = ContextCompat.getDrawable(context, R.drawable.bac_grid_item_selected)
                            }
                        }
                    }
                }
                gridLayout.addView(button)
            }
        }

        val submitButton = Button(context).apply {
            text = "선택하기"
            textSize = 16f
            setTextColor(ContextCompat.getColor(context, R.color.white))
            typeface = ResourcesCompat.getFont(context, R.font.pretendard_medium)
            background = ContextCompat.getDrawable(context, R.drawable.bac_button_solid)
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

        cardInnerLayout.apply {
            addView(instructionText)
            addView(maxCountText)
            addView(gridLayout)
            addView(submitButton)
        }
        cardView.addView(cardInnerLayout)
        binding.chattingSelectLayout.addView(cardView)
    }

    private fun updateChattingEditText(selectedPositions: List<Int>) {
        val orderedPositions = selectedPositions.joinToString(",")
        binding.chattingEditText.setText(orderedPositions)
    }

    private fun showChangeTitleDialog() {
        val builder = androidx.appcompat.app.AlertDialog.Builder(requireContext())
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_edit_text, null)
        val input = dialogView.findViewById<EditText>(R.id.etChangeTitle)
        val applyButton = dialogView.findViewById<TextView>(R.id.tv_apply)

        input.setText(binding.chattingToolbar.title)
        input.setOnFocusChangeListener { _, hasFocus ->
            input.backgroundTintList = if (hasFocus) {
                ContextCompat.getColorStateList(requireContext(), R.color.blue)
            } else {
                ContextCompat.getColorStateList(requireContext(), R.color.black)
            }
        }

        builder.setView(dialogView)
        val dialog = builder.create()

        applyButton.setOnClickListener {
            val newTitle = input.text.toString()
            if (newTitle.isBlank()) {
                shortToast("제목을 입력해주세요")
            } else {
                roomViewModel.setRoomTitle(
                    token = PaletteApplication.prefs.token,
                    title = com.api.palette.data.room.data.TitleData(newTitle),
                    roomId = roomId
                ) { result ->
                    if (result.isSuccess) {
                        binding.chattingToolbar.title = newTitle
                        shortToast("방 제목이 변경되었습니다.")
                    } else {
                        shortToast("방 제목 변경 실패: ${result.exceptionOrNull()?.message}")
                    }
                }
            }
            dialog.dismiss()
        }

        dialog.setCancelable(true)
        dialog.window?.setBackgroundDrawable(android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT))
        dialog.show()
    }

    private fun hideKeyboard() {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.chattingEditText.windowToken, 0)
    }

    private fun startPing() {
        pingTimer.schedule(object : java.util.TimerTask() {
            override fun run() {
                if (::webSocketManager.isInitialized) {
                    webSocketManager.send("ping")
                }
            }
        }, 0, 30000)
    }

    private fun stopPing() {
        pingTimer.cancel()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        webSocketManager.stop()
        stopPing()
        if (chatList.isEmpty()) {
            (requireActivity() as? BaseControllable)?.deleteRoom(
                token = PaletteApplication.prefs.token,
                roomId = roomId
            )
        }
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        (requireActivity() as? BaseControllable)?.bottomVisible(true)
    }
}
