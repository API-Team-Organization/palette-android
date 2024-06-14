package com.example.palette.ui.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import app.rive.runtime.kotlin.RiveAnimationView
import app.rive.runtime.kotlin.controllers.RiveFileController
import app.rive.runtime.kotlin.core.ExperimentalAssetLoader
import app.rive.runtime.kotlin.core.RiveEvent
import com.example.palette.MainActivity
import com.example.palette.R
import com.example.palette.application.PaletteApplication
import com.example.palette.common.Constant
import com.example.palette.data.auth.AuthRequestManager
import com.example.palette.data.base.BaseVoidResponse
import com.example.palette.databinding.ActivityServiceBinding
import com.example.palette.ui.base.BottomControllable
import com.example.palette.ui.main.create.CreateMediaFragment
import com.example.palette.ui.main.settings.SettingFragment
import com.example.palette.ui.main.work.WorkFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response

@OptIn(ExperimentalAssetLoader::class)
class ServiceActivity : AppCompatActivity(), BottomControllable {
    private val binding by lazy { ActivityServiceBinding.inflate(layoutInflater) }
    private val riveAnimationView: RiveAnimationView by lazy(LazyThreadSafetyMode.NONE) {
        binding.bottomBar
    }

    val eventListener = object : RiveFileController.RiveEventListener {
        override fun notifyEvent(event: RiveEvent) {
            val scope = CoroutineScope(Dispatchers.Main)
            scope.launch {
                val session = AuthRequestManager
                    .sessionRequest(PaletteApplication
                        .prefs
                        .token)

                PaletteApplication.prefs.token = session.headers()["X-AUTH-Token"]?: ""
                if (PaletteApplication.prefs.token == "")
                    showSessionDialog(this@ServiceActivity)
            }

            when (event.name) {
                "click_home" -> changeFragment(CreateMediaFragment())
                "click_search" -> changeFragment(WorkFragment())
                "click_setting" -> changeFragment(SettingFragment())
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        changeFragment(CreateMediaFragment())

        // RiveAnimationView에서 click_home 이벤트를 강제로 실행
        riveAnimationView.addEventListener(eventListener)

        setContentView(binding.root)
    }

    // bottomVisible 메서드 정의
    override fun bottomVisible(visibility: Boolean) {
        binding.bottomBar.visibility = if (visibility) View.VISIBLE else View.GONE
    }

    private fun changeFragment(fragment: Fragment) {
        Log.d("MainFragment", "changeFragment is running")
        val fragmentManager = this.supportFragmentManager // 또는 requireActivity().supportFragmentManager (Fragment 내에서)
        val transaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.mainContent, fragment)

        transaction.commit()
    }

    private fun showSessionDialog(context: Context) {
        val builder = AlertDialog.Builder(context)

        builder.setTitle("세션 만료")
        builder.setMessage("세션이 만료되었습니다. 로그인 후, 이용해 주세요.")

        // "예" 버튼 추가
        builder.setPositiveButton("OK") { dialog, which ->
            val intent = Intent(this, MainActivity::class.java)

            startActivity(intent)
            dialog.dismiss()
        }

        // 다이얼로그 외부 클릭이나 뒤로가기 버튼 비활성화
        builder.setCancelable(false)

        val dialog = builder.create()
        dialog.show()
    }

    override fun onRestart() {
        super.onRestart()

        val scope = CoroutineScope(Dispatchers.Main)
        scope.launch {
            val session = AuthRequestManager
                .sessionRequest(PaletteApplication
                    .prefs
                    .token)

            PaletteApplication.prefs.token = session.headers()["X-AUTH-Token"]?: ""
            if (PaletteApplication.prefs.token == "")
                showSessionDialog(this@ServiceActivity)
        }
    }
}
