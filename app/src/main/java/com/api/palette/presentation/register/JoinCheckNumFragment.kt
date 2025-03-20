package com.api.palette.presentation.register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.fragment.app.Fragment
import com.api.palette.R
import com.api.palette.databinding.FragmentJoinCheckNumBinding
import com.api.palette.application.PaletteApplication
import com.api.palette.ui.util.shortToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class JoinCheckNumFragment : Fragment() {
    private var _binding: FragmentJoinCheckNumBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentJoinCheckNumBinding.inflate(inflater, container, false)
        binding.nEtJoinEmail.setText(PaletteApplication.prefs.userId)
        binding.btnCheckNum.setOnClickListener {
            if (binding.etJoinCheckNum.text.toString().trim().isNotEmpty()) {
                findNavController().navigate(R.id.action_joinCheckNumFragment_to_joinNameFragment)
            } else {
                shortToast("인증번호를 입력해주세요.")
            }
        }
        binding.tvResend.setOnClickListener { shortToast("인증번호가 재전송되었습니다.") }
        binding.etJoinCheckNum.setOnFocusChangeListener { _, hasFocus ->
            binding.etJoinCheckNum.backgroundTintList = if (hasFocus)
                resources.getColorStateList(R.color.blue) else resources.getColorStateList(R.color.black)
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
