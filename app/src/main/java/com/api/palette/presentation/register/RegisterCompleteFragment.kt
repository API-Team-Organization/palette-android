package com.api.palette.presentation.register

import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.api.palette.R
import com.api.palette.databinding.FragmentJoinCompleteBinding

class RegisterCompleteFragment : Fragment() {
    private var _binding: FragmentJoinCompleteBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentJoinCompleteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        applyGradientToText()
        binding.btnStart.setOnClickListener {
            findNavController().navigate(R.id.action_joinCompleteFragment_to_loginFragment)
        }
    }

    private fun applyGradientToText() {
        val shader = LinearGradient(
            0f, 0f, 100f, 100f,
            intArrayOf(Color.parseColor("#6389E9"), Color.parseColor("#555FE8")),
            null,
            Shader.TileMode.CLAMP
        )
        binding.good.paint.shader = shader
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
