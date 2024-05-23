package com.example.palette.ui.main.create

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.palette.R
import com.example.palette.databinding.FragmentChattingBinding
import com.example.palette.ui.main.ServiceActivity

class ChattingFragment : Fragment() {
    private lateinit var binding: FragmentChattingBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChattingBinding.inflate(inflater, container, false)

        initView()
        return binding.root
    }

    private fun initView() {
        (activity as? ServiceActivity)?.bottomVisible(false)

        binding.editGchatMessage.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                // EditText 내용이 변경된 후 호출됩니다.
                if (s.isNullOrBlank()) {
                    binding.buttonGchatSend.setImageResource(R.drawable.ic_send)
                } else {
                    binding.buttonGchatSend.setImageResource(R.drawable.ic_send_ok)

                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // 텍스트가 변경되기 전 호출됩니다. 여기서는 사용하지 않습니다.
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // 텍스트가 변경되는 동안 호출됩니다. 여기서는 사용하지 않습니다.
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        (activity as? ServiceActivity)?.bottomVisible(true)
    }


}