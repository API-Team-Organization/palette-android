package com.api.palette.ui.main

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import app.rive.runtime.kotlin.RiveAnimationView
import app.rive.runtime.kotlin.controllers.RiveFileController
import app.rive.runtime.kotlin.core.RiveEvent
import com.api.palette.MainActivity
import com.api.palette.R
import com.api.palette.application.PaletteApplication
import com.api.palette.data.auth.AuthRequestManager
import com.api.palette.data.room.RoomRequestManager
import com.api.palette.databinding.ActivityServiceBinding
import com.api.palette.ui.base.BaseControllable
import com.api.palette.ui.main.create.room.CreateMediaFragment
import com.api.palette.ui.main.settings.SettingFragment
import com.api.palette.ui.main.work.WorkFragment
import com.api.palette.ui.util.changeFragment
import com.api.palette.ui.util.isRootFragment
import com.api.palette.ui.util.log
import com.api.palette.ui.util.shortToast
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
    private lateinit var vibrator: Vibrator

    private var doubleBackToExitPressedOnce = false
    private val handler = Handler(Looper.getMainLooper())

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (!isRootFragment(supportFragmentManager)) {
                supportFragmentManager.popBackStack()
            } else {
                if (doubleBackToExitPressedOnce) {
                    finish()
                    return
                }

                doubleBackToExitPressedOnce = true
                shortToast("한 번 더 누르면 종료됩니다.")

                handler.postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
            }
        }
    }

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
            vibrateOnClick()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        changeFragment(createMediaFragment, supportFragmentManager)

        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)

        log(PaletteApplication.prefs.token)

        // RiveAnimationView에서 click_home 이벤트를 강제로 실행
        riveAnimationView.addEventListener(eventListener)

        vibrator = (getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator)!!

        setContentView(binding.root)
    }

    private fun handleTabClick(event: BottomTab) {
        if (currentTab != event) {
            currentTab = event
            changeFragment(getFragment(event), supportFragmentManager, false)
        }
    }

    private fun getFragment(event: BottomTab) = when (event) {
        BottomTab.HOME -> createMediaFragment
        BottomTab.SEARCH -> workFragment
        BottomTab.SETTING -> settingFragment
    }

    private fun vibrateOnClick() {
        vibrator.vibrate(VibrationEffect.createOneShot(50, 75))
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

    private fun showNetworkErrorDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_network_error, null)

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        val tvExit: TextView = dialogView.findViewById(R.id.tv_session)

        tvExit.setOnClickListener {
            finishAffinity()
            dialog.dismiss()
        }

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

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }
}