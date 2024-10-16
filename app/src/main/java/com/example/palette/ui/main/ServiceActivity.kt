package com.example.palette.ui.main

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import app.rive.runtime.kotlin.RiveAnimationView
import app.rive.runtime.kotlin.controllers.RiveFileController
import app.rive.runtime.kotlin.core.RiveEvent
import com.example.palette.MainActivity
import com.example.palette.R
import com.example.palette.application.PaletteApplication
import com.example.palette.data.auth.AuthRequestManager
import com.example.palette.data.room.RoomRequestManager
import com.example.palette.databinding.ActivityServiceBinding
import com.example.palette.ui.base.BaseControllable
import com.example.palette.ui.main.create.room.CreateMediaFragment
import com.example.palette.ui.main.settings.SettingFragment
import com.example.palette.ui.main.work.WorkFragment
import com.example.palette.ui.util.changeFragment
import com.example.palette.ui.util.log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.UnknownHostException

enum class BottomTab(
    val eventName: String
) {
    HOME("click_home"),
    SEARCH("click_search"),
    SETTING("click_setting");

    companion object {
        fun from(eventName: String) = entries.find { it.eventName == eventName }
    }
}

class ServiceActivity : AppCompatActivity(), BaseControllable {
    private val binding by lazy { ActivityServiceBinding.inflate(layoutInflater) }
    private val riveAnimationView: RiveAnimationView by lazy(LazyThreadSafetyMode.NONE) {
        binding.bottomBar
    }
    private var currentTab: BottomTab? = null
    private val createMediaFragment = CreateMediaFragment()
    private val workFragment = WorkFragment()
    private val settingFragment = SettingFragment()

    private val eventListener = object : RiveFileController.RiveEventListener {
        override fun notifyEvent(event: RiveEvent) {
            val scope = CoroutineScope(Dispatchers.Main)
            scope.launch {
                try {
                    val response = AuthRequestManager.sessionRequest(PaletteApplication.prefs.token)
                    if (response.isSuccessful) {
                        // 성공 시 처리
                        log(PaletteApplication.prefs.token)
                    } else {
                        // 실패 시 처리
                        sessionDialog(this@ServiceActivity)
                    }
                } catch (e: UnknownHostException) {
                    log("네트워크 연결 문제: ${e.message}")
                    withContext(Dispatchers.Main) {
                        showNetworkErrorDialog()
                    }
                }
            }

            val tab = BottomTab.from(event.name) ?: return
            handleTabClick(tab)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        changeFragment(createMediaFragment, supportFragmentManager)

        log(PaletteApplication.prefs.token)

        // RiveAnimationView에서 click_home 이벤트를 강제로 실행
        riveAnimationView.addEventListener(eventListener)

        setContentView(binding.root)
    }

    private fun handleTabClick(event: BottomTab) {
        if (currentTab != event) {
            currentTab = event
            changeFragment(getFragment(event), supportFragmentManager)
        }
    }

    private fun getFragment(event: BottomTab) = when (event) {
        BottomTab.HOME -> createMediaFragment
        BottomTab.SEARCH -> workFragment
        BottomTab.SETTING -> settingFragment
    }

    // bottomVisible 메서드 정의
    override fun bottomVisible(visibility: Boolean) {
        binding.bottomBar.visibility = if (visibility) View.VISIBLE else View.GONE
    }

    override fun sessionDialog(context: Context) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_session, null)

        val dialog = AlertDialog.Builder(context)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        val tvSession: TextView = dialogView.findViewById(R.id.tv_session)

        PaletteApplication.prefs.clearToken()

        tvSession.setOnClickListener {
            val intent = Intent(context, MainActivity::class.java)
            context.startActivity(intent)

            (context as? Activity)?.finish()

            dialog.dismiss()
        }

        dialog.show()
    }


    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    override fun deleteRoom(token: String, roomId: Int) {
        lifecycleScope.launch {
            try {
                val response = RoomRequestManager.deleteRoom(PaletteApplication.prefs.token, roomId)
                log("ServiceActivity onDestroyView 지워졌는지 확인 ${response.isSuccessful}")
            } catch (e: Exception) {
                log("ServiceActivity onDestroyView error: $e")
            }
        }
        finish() //인텐트 종료

        overrideActivityTransition(OVERRIDE_TRANSITION_OPEN, 0, 0) //인텐트 효과 없애기
        startActivity(intent) //액티비티 열기
        overrideActivityTransition(OVERRIDE_TRANSITION_CLOSE, 0, 0) //인텐트 효과 없애기

    }

    // 네트워크 오류 Dialog 표시 함수
    private fun showNetworkErrorDialog() {
        val dialog = AlertDialog.Builder(this)
            .setTitle("네트워크 오류")
            .setMessage("인터넷 연결을 확인해 주세요. 앱을 종료합니다.")
            .setPositiveButton("종료") { _, _ ->
                finishAffinity() // 앱 종료
            }
            .setCancelable(false) // Dialog 바깥을 터치해도 닫히지 않음
            .create()

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