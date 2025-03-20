package com.api.palette.presentation.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.api.palette.R
import com.api.palette.application.PaletteApplication
import com.api.palette.databinding.FragmentChangeBirthDateBinding
import com.api.palette.presentation.info.InfoViewModel
import com.api.palette.ui.util.shortToast
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@AndroidEntryPoint
class ChangeBirthDateFragment : Fragment() {

    private var _binding: FragmentChangeBirthDateBinding? = null
    private val binding get() = _binding!!
    private val infoViewModel: InfoViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentChangeBirthDateBinding.inflate(inflater, container, false)
        binding.ivArrowBack.setOnClickListener { requireActivity().onBackPressedDispatcher.onBackPressed() }
        binding.changeBirthDateBtn.setOnClickListener {
            val birthDate = getSelectedDate()
            if (birthDate.isEmpty()) {
                shortToast("생년월일을 설정해주세요.")
                return@setOnClickListener
            }
            PaletteApplication.prefs.clearUser()
            infoViewModel.changeBirthDate(PaletteApplication.prefs.token, birthDate)
        }
        observeViewModel()
        return binding.root
    }

    private fun observeViewModel() {
        infoViewModel.updateSuccess.observe(viewLifecycleOwner) { success ->
            if (success) {
                shortToast("프로필이 성공적으로 업데이트되었습니다.")
                requireActivity().supportFragmentManager.popBackStack()
            }
        }
        infoViewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            shortToast(error)
        }
    }

    private fun getSelectedDate(): String {
        val datePicker = binding.changeBirthDateSpinner
        val day = datePicker.dayOfMonth
        val month = datePicker.month
        val year = datePicker.year
        val calendar = Calendar.getInstance().apply { set(year, month, day) }
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return format.format(calendar.time)
    }

    override fun onPause() {
        super.onPause()
        (activity as? com.api.palette.presentation.main.ServiceActivity)?.findViewById<View>(R.id.bottomBar)?.visibility = View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
