package com.example.palette.ui.main.settings

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.palette.R
import com.example.palette.databinding.FragmentEditUserInfoBinding
import com.example.palette.ui.base.BaseControllable
import com.example.palette.ui.main.ServiceActivity
import com.example.palette.ui.util.changeFragment

class EditUserInfoFragment : Fragment() {

    private lateinit var binding: FragmentEditUserInfoBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditUserInfoBinding.inflate(inflater, container, false)

        (requireActivity() as? BaseControllable)?.bottomVisible(false)

        binding.changePassword.setOnClickListener {
            changeFragment(ChangePasswordFragment())
        }

        binding.changeName.setOnClickListener {
            changeFragment(ChangeNameFragment())
        }

        binding.changeBirthDate.setOnClickListener {
            changeFragment(ChangeBirthDateFragment())
        }

        return binding.root
    }

    override fun onPause() {
        super.onPause()
        (activity as ServiceActivity).findViewById<View>(R.id.bottomBar).visibility = View.VISIBLE
    }
}