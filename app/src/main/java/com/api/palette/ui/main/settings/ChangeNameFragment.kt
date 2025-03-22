package com.api.palette.ui.main.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.api.palette.R
import com.api.palette.databinding.FragmentChangeNameBinding
import com.api.palette.ui.util.shortToast
import com.api.palette.viewmodel.InfoViewModel

class ChangeNameFragment : Fragment() {

    private lateinit var binding: FragmentChangeNameBinding
    private val infoViewModel: InfoViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentChangeNameBinding.inflate(inflater, container, false)
        (requireActivity() as? com.api.palette.ui.base.BaseControllable)?.bottomVisible(false)
        binding.etChangeName.setOnFocusChangeListener { _, hasFocus ->
            binding.etChangeName.backgroundTintList = ContextCompat.getColorStateList(requireContext(), if (hasFocus) R.color.blue else R.color.black)
        }
        binding.ivArrowBack.setOnClickListener { requireActivity().onBackPressedDispatcher.onBackPressed() }
        binding.changeNameBtn.setOnClickListener {
            val username = binding.etChangeName.text.toString().trim()
            if (username.isEmpty()) {
                shortToast("이름을 입력해주세요")
                return@setOnClickListener
            }
            infoViewModel.changeName(username)
            requireActivity().supportFragmentManager.popBackStack()
        }
        return binding.root
    }
}
