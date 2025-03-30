package com.api.palette.presentation.main.settings.editprofile

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.api.palette.databinding.FragmentEditUserInfoBinding
import com.api.palette.presentation.util.changeFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditUserInfoFragment : Fragment() {

    private lateinit var binding: FragmentEditUserInfoBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditUserInfoBinding.inflate(inflater, container, false)
        setupClickListeners()
        return binding.root
    }

    private fun setupClickListeners() {
        binding.changePassword.setOnClickListener {
            changeFragment(ChangePasswordFragment())
        }
        binding.changeName.setOnClickListener {
            changeFragment(ChangeNameFragment())
        }
        binding.changeBirthDate.setOnClickListener {
            changeFragment(ChangeBirthDateFragment())
        }
    }
}
