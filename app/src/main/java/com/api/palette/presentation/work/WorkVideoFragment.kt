package com.api.palette.presentation.work

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.api.palette.databinding.FragmentWorkVideoBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WorkVideoFragment : Fragment() {
    private var _binding: FragmentWorkVideoBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentWorkVideoBinding.inflate(inflater, container, false)
        binding.tvComingSoon.text = "동영상 목록이 없습니다."
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
