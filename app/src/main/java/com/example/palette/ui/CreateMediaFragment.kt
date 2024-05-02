package com.example.palette.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.palette.R
import com.example.palette.databinding.FragmentCreateMediaBinding
import com.example.palette.databinding.FragmentJoinEmailBinding

class CreateMediaFragment : Fragment() {
    private lateinit var binding : FragmentCreateMediaBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentCreateMediaBinding.inflate(inflater, container, false)

        return binding.root
    }
}