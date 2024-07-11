package com.example.palette.ui.register

import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.palette.R
import com.example.palette.application.PaletteApplication
import com.example.palette.common.Constant.TAG
import com.example.palette.common.HeaderUtil
import com.example.palette.data.auth.RegisterRequest
import com.example.palette.data.auth.RegisterRequestManager
import com.example.palette.databinding.FragmentJoinCompleteBinding
import com.example.palette.ui.util.shortToast
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.net.SocketTimeoutException

class JoinCompleteFragment : Fragment() {
    private lateinit var binding : FragmentJoinCompleteBinding
    private val registerViewModel: RegisterViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentJoinCompleteBinding.inflate(inflater, container, false)

        goodGradation()

        binding.btnStart.setOnClickListener {
            if (true) { // 회원가입 성공하면
                findNavController().navigate(R.id.action_joinCompleteFragment_to_loginFragment)
            }
        }

        return binding.root
    }

    private fun goodGradation() {
        val good = binding.good
        val textShader = LinearGradient(0f, 0f, 100f, 100f,
            intArrayOf(Color.parseColor("#6389E9"), Color.parseColor("#555FE8")),
            floatArrayOf(0f, 1f), Shader.TileMode.CLAMP
        )

        good.paint.shader = textShader
    }
}