package com.example.palette.ui.register

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.palette.R
import com.example.palette.databinding.FragmentJoinNameBinding
import java.util.regex.Pattern

class JoinNameFragment : Fragment() {
    private lateinit var binding : FragmentJoinNameBinding
    private val registerViewModel: RegisterViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentJoinNameBinding.inflate(inflater, container, false)

        binding.btnComplete.setOnClickListener {
            checkName()
        }

        return binding.root
    }

    private fun checkName() {
        with(binding) {
            val name = etJoinName.text.toString()

            if (name.isEmpty()) {
                checkNameFailed(etJoinName)
                failedNameEmpty.visibility = View.VISIBLE
                failedNameFormat.visibility = View.GONE
            } else {
                val isNameValid = nameRegularExpression(name)
                Log.d("isNameValid", "${nameRegularExpression(name)}")

                if (isNameValid) {
                    registerViewModel.username.observe(viewLifecycleOwner) {
                        binding.etJoinName.setText(
                            it
                        )
                    }
                    registerViewModel.setUsername(binding.etJoinName.text.toString())
                    findNavController().navigate(R.id.action_joinNameFragment_to_joinCompleteFragment)
                } else {
                    checkNameFailed(etJoinName)
                    failedNameFormat.visibility = View.VISIBLE
                    failedNameEmpty.visibility = View.GONE
                }
            }
        }
    }

    private fun checkNameFailed(name: EditText) {
        name.background = ContextCompat.getDrawable(name.context, R.drawable.bac_edit_text_failed)
        name.requestFocus()
        name.selectAll()
    }

    private fun nameRegularExpression(name: String): Boolean {
        val namePattern = "^[가-힣]*\$"
        val pattern = Pattern.compile(namePattern)
        val matcher = pattern.matcher(name)
        return matcher.find()
    }
}