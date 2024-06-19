package com.example.palette.ui.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
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
import com.example.palette.data.auth.AuthRequestManager
import com.example.palette.databinding.ActivityServiceBinding
import com.example.palette.ui.base.BaseControllable
import com.example.palette.ui.main.create.CreateMediaFragment
import com.example.palette.ui.main.settings.SettingFragment
import com.example.palette.ui.main.work.WorkFragment
import com.example.palette.ui.util.log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalAssetLoader::class)
class ServiceActivity : AppCompatActivity(), BaseControllable {
    private val binding by lazy { ActivityServiceBinding.inflate(layoutInflater) }
    private val riveAnimationView: RiveAnimationView by lazy(LazyThreadSafetyMode.NONE) {
        binding.bottomBar
    }

    val eventListener = object : RiveFileController.RiveEventListener {
        override fun notifyEvent(event: RiveEvent) {
            val scope = CoroutineScope(Dispatchers.Main)
            scope.launch {
                val session = AuthRequestManager.sessionRequest(PaletteApplication.prefs.token)

                log(PaletteApplication.prefs.token)
                if (!session.isSuccessful)
                    sessionDialog(this@ServiceActivity)
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

    private fun changeFragment(fragment: Fragment) {
        val fragmentManager = this.supportFragmentManager // 또는 requireActivity().supportFragmentManager (Fragment 내에서)
        val transaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.mainContent, fragment)

        transaction.commit()
    }

    // bottomVisible 메서드 정의
    override fun bottomVisible(visibility: Boolean) {
        binding.bottomBar.visibility = if (visibility) View.VISIBLE else View.GONE
    }

    override fun sessionDialog(context: Context) {
        val builder = AlertDialog.Builder(context)

        builder.setTitle("세션 만료")
        builder.setMessage("세션이 만료되었습니다. 로그인 후, 이용해 주세요.")
        PaletteApplication.prefs.clearToken()

        builder.setPositiveButton("로그인") { dialog, _ ->
            val intent = Intent(context, MainActivity::class.java)

            context.startActivity(intent)
            finish()
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
                .sessionRequest(PaletteApplication.prefs.token)

            if (!session.isSuccessful)
                sessionDialog(this@ServiceActivity)
        }
    }
}