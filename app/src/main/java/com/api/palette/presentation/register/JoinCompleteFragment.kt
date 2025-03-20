package com.api.palette.presentation.register

import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.api.palette.R
import com.api.palette.databinding.FragmentJoinCompleteBinding
import com.api.palette.ui.util.shortToast
import dagger.hilt.android.AndroidEntryPoint
import androidx.fragment.app.viewModels

@AndroidEntryPoint
class JoinCompleteFragment : Fragment() {
    private var _binding: FragmentJoinCompleteBinding? = null
    private val binding get() = _binding!!
    private val registerViewModel: RegisterViewModel by viewModels({ requireActivity() })

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentJoinCompleteBinding.inflate(inflater, container, false)
        applyGoodGradation()
        binding.btnStart.setOnClickListener {
            registerViewModel.register()
        }
        registerViewModel.registerSuccess.observe(viewLifecycleOwner) { success ->
            if (success) {
                shortToast("회원가입이 완료되었습니다. 로그인해주세요.")
                findNavController().navigate(R.id.action_joinCompleteFragment_to_loginFragment)
            }
        }
        registerViewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            shortToast(error)
        }
        return binding.root
    }

    private fun applyGoodGradation() {
        val textShader = LinearGradient(0f, 0f, 100f, 100f,
            intArrayOf(Color.parseColor("#6389E9"), Color.parseColor("#555FE8")),
            floatArrayOf(0f, 1f),
            Shader.TileMode.CLAMP)
        binding.good.paint.shader = textShader
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
