package com.example.palette.ui.signup

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.palette.R
import com.example.palette.databinding.FragmentJoinEmailBinding
import com.example.palette.databinding.FragmentStartBinding

class JoinEmailFragment : Fragment() {
    private lateinit var binding : FragmentJoinEmailBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentJoinEmailBinding.inflate(inflater, container, false)

        binding.btnCheckNum.setOnClickListener {
            val bundle = Bundle()
            Log.d("ddasdf", "${binding.etJoinEmail.text}")
            bundle.putString("email", binding.etJoinEmail.text.toString())

            val passBundleBFragment = JoinCheckNumFragment()
            passBundleBFragment.arguments = bundle
            findNavController().navigate(R.id.action_joinEmailFragment_to_joinCheckNumFragment, bundle)
        }

        return binding.root
    }
}