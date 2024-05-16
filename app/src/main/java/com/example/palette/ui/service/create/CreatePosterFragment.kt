package com.example.palette.ui.service.create

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.palette.databinding.FragmentCreatePosterBinding

class CreatePosterFragment : Fragment() {
    private lateinit var binding : FragmentCreatePosterBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCreatePosterBinding.inflate(inflater, container, false)


        // Inflate the layout for this fragment
        return binding.root
    }
}