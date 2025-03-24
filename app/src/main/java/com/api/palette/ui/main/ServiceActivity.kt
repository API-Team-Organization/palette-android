package com.api.palette.ui.main

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import app.rive.runtime.kotlin.RiveAnimationView
import app.rive.runtime.kotlin.controllers.RiveFileController
import app.rive.runtime.kotlin.core.RiveEvent
import com.api.palette.MainActivity
import com.api.palette.R
import com.api.palette.application.PaletteApplication
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

enum class BottomTab(val eventName: String) {
    HOME("click_home"),
    SEARCH("click_search"),
    SETTING("click_setting");

    companion object {
        fun from(eventName: String) = values().find { it.eventName == eventName }
    }
}

class ServiceActivity : AppCompatActivity(), BaseControllable {

    private val binding by lazy { ActivityServiceBinding.inflate(layoutInflater) }
    private val riveAnimationView: RiveAnimationView by lazy { binding.bottomBar }
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
                    val response = com.api.palette.data.auth.AuthRequestManager.sessionRequest(PaletteApplication.prefs.token)
                    if (!response.isSuccessful) {
                        sessionDialog(this@ServiceActivity)
                    }
                } catch (e: UnknownHostException) {
                    log("네트워크 연결 문제: ${e.message}")
                    withContext(Dispatchers.Main) { showNetworkErrorDialog() }
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

        riveAnimationView.addEventListener(eventListener)
        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        setContentView(binding.root)
    }

    private fun handleTabClick(event: BottomTab) {
        if (currentTab != event) {
            val transaction = supportFragmentManager.beginTransaction()
            when (event) {
                BottomTab.SEARCH -> transaction.setCustomAnimations(R.anim.anim_slide_in_left_fade_in, R.anim.anim_fade_out_200ms)
                BottomTab.HOME -> {
                    if (currentTab == BottomTab.SEARCH) {
                        transaction.setCustomAnimations(R.anim.anim_slide_in_right_fade_in, R.anim.anim_fade_out_200ms)
                    } else {
                        transaction.setCustomAnimations(R.anim.anim_slide_in_left_fade_in, R.anim.anim_fade_out_200ms)
                    }
                }
                BottomTab.SETTING -> transaction.setCustomAnimations(R.anim.anim_slide_in_right_fade_in, R.anim.anim_fade_out_200ms)
            }
            transaction.replace(binding.mainContent.id, getFragment(event))
            transaction.commit()
            currentTab = event
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

    override fun bottomVisible(visibility: Boolean) {
        binding.bottomBar.visibility = if (visibility) View.VISIBLE else View.GONE
    }

    override fun sessionDialog(context: Context) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_session, null)
        val dialog = AlertDialog.Builder(context).setView(dialogView).setCancelable(false).create()
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

    private fun showNetworkErrorDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_network_error, null)
        val dialog = AlertDialog.Builder(this).setView(dialogView).setCancelable(false).create()
        val tvExit: TextView = dialogView.findViewById(R.id.tv_session)

        tvExit.setOnClickListener {
            finishAffinity()
            dialog.dismiss()
        }

        dialog.show()
    }

    override fun deleteRoom(token: String, roomId: Int) {
        lifecycleScope.launch {
            try {
                val response = com.api.palette.data.room.RoomRequestManager.deleteRoom(PaletteApplication.prefs.token, roomId)
                log("ServiceActivity deleteRoom success: ${response.isSuccessful}")
            } catch (e: Exception) {
                log("ServiceActivity deleteRoom error: $e")
            }
        }
        finish()
    }

    override fun onRestart() {
        super.onRestart()
        val scope = CoroutineScope(Dispatchers.Main)
        scope.launch {
            val session = com.api.palette.data.auth.AuthRequestManager.sessionRequest(PaletteApplication.prefs.token)
            if (!session.isSuccessful)
                sessionDialog(this@ServiceActivity)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }
}
