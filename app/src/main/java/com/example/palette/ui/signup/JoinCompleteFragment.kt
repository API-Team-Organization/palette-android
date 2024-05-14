package com.example.palette.ui.signup

import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.navigation.fragment.findNavController
import com.example.palette.R
import com.example.palette.databinding.FragmentJoinCompleteBinding

class JoinCompleteFragment : Fragment() {
    private lateinit var binding : FragmentJoinCompleteBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentJoinCompleteBinding.inflate(inflater, container, false)

        goodGradation()

        binding.btnStart.setOnClickListener {
            findNavController().navigate(R.id.action_joinCompleteFragment_to_loginFragment)
        }

        exitPopup()

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

    private fun exitPopup() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                AlertDialog.Builder(requireContext()).apply {
                    setTitle("팔레트")
                    setMessage("앱을 종료하시겠습니까?")
                    setPositiveButton("예") { _, _ ->
                        requireActivity().finish()
                    }
                    setNegativeButton("아니요", null)
                    create()
                    show()
                }
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }
}