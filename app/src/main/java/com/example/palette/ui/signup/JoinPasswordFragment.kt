package com.example.palette.ui.signup

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.palette.R
import com.example.palette.databinding.FragmentJoinEmailBinding
import com.example.palette.databinding.FragmentJoinPasswordBinding

class JoinPasswordFragment : Fragment() {
    private lateinit var binding : FragmentJoinPasswordBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentJoinPasswordBinding.inflate(inflater, container, false)

        binding.btnNext.setOnClickListener {
            findNavController().navigate(R.id.action_joinPasswordFragment_to_joinBirthFragment)
        }

        return binding.root
    }
}