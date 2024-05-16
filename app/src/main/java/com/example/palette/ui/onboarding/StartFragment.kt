package com.example.palette.ui.onboarding

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.palette.R
import com.example.palette.common.Constant
import com.example.palette.databinding.FragmentStartBinding

class StartFragment : Fragment() {
    private lateinit var binding : FragmentStartBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment

        binding = FragmentStartBinding.inflate(inflater, container, false)
        initView()

        return binding.root
    }

    private fun initView() {
        val pref = requireActivity().getSharedPreferences("notFirst", Context.MODE_PRIVATE)
        val notFirst = pref.getBoolean("notFirst", true)

        if (notFirst) {
            Log.d(Constant.TAG, "StartFragment notFirst: ${notFirst}")
            findNavController().navigate(R.id.action_startFragment_to_loginFragment)
        }
        binding.signInText.setOnClickListener {
            findNavController().navigate(R.id.action_startFragment_to_loginFragment)
        }
        binding.startButton.setOnClickListener {
            findNavController().navigate(R.id.action_startFragment_to_joinEmailFragment)
        }
    }
}