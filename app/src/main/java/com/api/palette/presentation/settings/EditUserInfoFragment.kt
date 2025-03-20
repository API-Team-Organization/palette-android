package com.api.palette.presentation.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.api.palette.R
import com.api.palette.databinding.FragmentEditUserInfoBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditUserInfoFragment : Fragment() {
    private var _binding: FragmentEditUserInfoBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentEditUserInfoBinding.inflate(inflater, container, false)

        binding.changePassword.setOnClickListener {
            findNavController().navigate(R.id.action_editUserInfoFragment_to_changePasswordFragment)
        }

        binding.changeName.setOnClickListener {
            findNavController().navigate(R.id.action_editUserInfoFragment_to_changeNameFragment)
        }

        binding.changeBirthDate.setOnClickListener {
            findNavController().navigate(R.id.action_editUserInfoFragment_to_changeBirthDateFragment)
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
