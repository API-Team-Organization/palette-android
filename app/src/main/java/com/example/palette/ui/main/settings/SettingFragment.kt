package com.example.palette.ui.main.settings

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.palette.MainActivity
import com.example.palette.R
import com.example.palette.application.PaletteApplication
import com.example.palette.application.PreferenceManager
import com.example.palette.application.UserPrefs
import com.example.palette.common.Constant
import com.example.palette.data.auth.AuthRequestManager
import com.example.palette.data.info.InfoRequestManager
import com.example.palette.databinding.FragmentSettingBinding
import com.example.palette.ui.util.shortToast
import kotlinx.coroutines.launch
import retrofit2.HttpException

class SettingFragment : Fragment() {
    private lateinit var binding: FragmentSettingBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentSettingBinding.inflate(inflater, container, false)
        initView()
        return binding.root
    }

    private fun initView() {
        loadProfileInfo()

        binding.logout.setOnClickListener {
            logout()
        }

        binding.appResign.setOnClickListener {
            resignDialog(requireContext())
        }

        binding.edit.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.mainContent, EditUserInfoFragment())
                .addToBackStack(null)
                .commitAllowingStateLoss()
        }
    }

    private fun loadProfileInfo() {
        // 먼저 로컬 저장소에서 데이터를 불러옵니다.
        binding.userEmail.text = UserPrefs.userId
        binding.userName.text = UserPrefs.userName
        binding.userBirthday.text = UserPrefs.userBirthDate

        // 네트워크 요청을 통해 데이터를 업데이트합니다.
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val profileInfo = InfoRequestManager.profileInfoRequest(PaletteApplication.prefs.token)
                profileInfo?.data?.let { data ->
                    binding.userEmail.text = data.email
                    binding.userName.text = data.name
                    binding.userBirthday.text = data.birthDate

                    // 로컬 저장소에 데이터를 저장합니다.
                    UserPrefs.userId = data.email
                    UserPrefs.userName = data.name
                    UserPrefs.userBirthDate = data.birthDate
                }
            } catch (e: Exception) {
                Log.e(Constant.TAG, "Setting profileInfo error : ", e)
            }
        }
    }

    private fun logout() {
        viewLifecycleOwner.lifecycleScope.launch {
            val response = AuthRequestManager.logoutRequest(PaletteApplication.prefs.token)
            Log.d(Constant.TAG, "Logout response.header code : ${response.code()}")
        }

        PaletteApplication.prefs = PreferenceManager(requireContext().applicationContext)
        PaletteApplication.prefs.clearToken()
        UserPrefs.clearUserData()

        val intent = Intent(activity, MainActivity::class.java)
        requireActivity().startActivity(intent)

        activity?.finish()
    }

    private fun resignDialog(context: Context) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("회원탈퇴")
        builder.setMessage("정말 탈퇴하시겠습니까?")

        builder.setPositiveButton("탈퇴") { dialog, _ ->
            resign()
            dialog.dismiss()
        }

        builder.setNegativeButton("취소") { dialog, _ ->
            dialog.dismiss()
        }
        builder.setCancelable(false)

        val dialog = builder.create()
        dialog.show()
    }

    private fun resign() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = AuthRequestManager.resignRequest(PaletteApplication.prefs.token)
                if (response.isSuccessful) {
                    // 회원 탈퇴 성공
                    shortToast("회원 탈퇴 성공")
                    Log.d(Constant.TAG, "Resign success")
                } else {
                    // 회원 탈퇴 실패
                    Log.e(Constant.TAG, "Resign failed: ${response.code()} - ${response.message()}")
                }
            } catch (e: HttpException) {
                shortToast("HttpException")
                Log.e(Constant.TAG, "Resign HTTP error", e)
            } catch (e: Exception) {
                shortToast("Exception")
                Log.e(Constant.TAG, "Resign error", e)
            }
        }
    }
}
