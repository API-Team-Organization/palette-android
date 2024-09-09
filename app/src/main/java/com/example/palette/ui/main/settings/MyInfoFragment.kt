package com.example.palette.ui.main.settings

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.palette.databinding.FragmentMyInfoBinding

class MyInfoFragment : Fragment() {

    private lateinit var binding: FragmentMyInfoBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMyInfoBinding.inflate(inflater, container, false)

        return binding.root
    }
}