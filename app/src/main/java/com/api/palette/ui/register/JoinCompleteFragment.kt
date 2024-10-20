package com.api.palette.ui.register

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

class JoinCompleteFragment : Fragment() {
    private lateinit var binding : FragmentJoinCompleteBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentJoinCompleteBinding.inflate(inflater, container, false)

        goodGradation()

        binding.btnStart.setOnClickListener {
            findNavController().navigate(R.id.action_joinCompleteFragment_to_loginFragment)
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