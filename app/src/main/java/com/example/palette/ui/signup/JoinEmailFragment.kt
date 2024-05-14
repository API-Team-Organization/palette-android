package com.example.palette.ui.signup

import android.os.Bundle
import android.util.Log
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.example.palette.R
import com.example.palette.databinding.FragmentJoinEmailBinding
import java.util.regex.Pattern

class JoinEmailFragment : Fragment() {
    private lateinit var binding : FragmentJoinEmailBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentJoinEmailBinding.inflate(inflater, container, false)

        binding.btnCheckNum.setOnClickListener {
            checkEmail()
        }

        return binding.root
    }

    private fun checkEmail() {
        val email = binding.etJoinEmail.text.toString().trim()

        with(binding) {
            if (email.isEmpty()) {
                checkEmailFailed(etJoinEmail)
                failedEmailEmpty.visibility = View.VISIBLE
                failedEmailFormat.visibility = View.GONE
            } else {
                val isEmailValid = emailRegularExpression(email)
                Log.d("isEmailValid", "${emailRegularExpression(email)}")

                if (isEmailValid) {
                    val bundle = Bundle()
                    bundle.putString("email", email)

                    val passBundleBFragment = JoinCheckNumFragment()
                    passBundleBFragment.arguments = bundle

                    findNavController().navigate(R.id.action_joinEmailFragment_to_joinCheckNumFragment, bundle)
                } else {
                    checkEmailFailed(etJoinEmail)
                    failedEmailFormat.visibility = View.VISIBLE
                    failedEmailEmpty.visibility = View.GONE
                }
            }
        }
    }

    private fun checkEmailFailed(email: EditText) {
        email.background = ContextCompat.getDrawable(email.context, R.drawable.bac_edit_text_failed)
        email.requestFocus()
        email.selectAll()
    }

    private fun emailRegularExpression(email: String): Boolean {
        val emailPattern = "^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*.[a-zA-Z]{2,3}\$"
        val pattern = Pattern.compile(emailPattern)
        val matcher = pattern.matcher(email)
        return matcher.find()
    }
}