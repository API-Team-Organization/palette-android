package com.api.palette.presentation.main

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.*
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
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
import com.api.palette.presentation.base.BaseControllable
import com.api.palette.presentation.main.create.room.CreateMediaFragment
import com.api.palette.presentation.main.settings.SettingFragment
import com.api.palette.presentation.main.work.WorkFragment
import com.api.palette.presentation.util.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import java.net.UnknownHostException

enum class BottomTab(val eventName: String) {
    HOME("click_home"),
    SEARCH("click_search"),
    SETTING("click_setting");

    companion object {
        fun from(eventName: String) = entries.find { it.eventName == eventName }
    }
}

@AndroidEntryPoint
class ServiceActivity : AppCompatActivity(), BaseControllable {

    private val binding by lazy { ActivityServiceBinding.inflate(layoutInflater) }
    private val riveAnimationView: RiveAnimationView by lazy { binding.bottomBar }

    private val createMediaFragment = CreateMediaFragment()
    private val workFragment = WorkFragment()
    private val settingFragment = SettingFragment()

    private lateinit var vibrator: Vibrator
    private var currentTab: BottomTab? = null
    private var doubleBackToExitPressedOnce = false
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        riveAnimationView.addEventListener(tabClickListener)

        changeFragment(createMediaFragment, supportFragmentManager)
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
        log(PaletteApplication.prefs.token)
    }

    private val tabClickListener = object : RiveFileController.RiveEventListener {
        override fun notifyEvent(event: RiveEvent) {
            checkSession()
            BottomTab.from(event.name)?.let {
                handleTabClick(it)
                vibrateOnClick()
            }
        }
    }

    private fun checkSession() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val response = AuthRequestManager.sessionRequest(PaletteApplication.prefs.token)
                if (!response.isSuccessful) sessionDialog(this@ServiceActivity)
            } catch (e: UnknownHostException) {
                showNetworkErrorDialog()
            }
        }
    }

    private fun handleTabClick(tab: BottomTab) {
        if (currentTab == tab) return

        val transaction = supportFragmentManager.beginTransaction()
        transaction.setCustomAnimations(getEnterAnim(tab), R.anim.anim_fade_out_200ms)
        transaction.replace(binding.mainContent.id, getFragment(tab)).commit()

        currentTab = tab
    }

    private fun getEnterAnim(tab: BottomTab) = when (tab) {
        BottomTab.SEARCH -> R.anim.anim_slide_in_left_fade_in
        BottomTab.HOME -> if (currentTab == BottomTab.SEARCH) R.anim.anim_slide_in_right_fade_in
        else R.anim.anim_slide_in_left_fade_in
        BottomTab.SETTING -> R.anim.anim_slide_in_right_fade_in
    }

    private fun getFragment(tab: BottomTab) = when (tab) {
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
        val dialog = AlertDialog.Builder(context)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        dialogView.findViewById<TextView>(R.id.tv_session).setOnClickListener {
            PaletteApplication.prefs.clearToken()
            context.startActivity(Intent(context, MainActivity::class.java))
            (context as? Activity)?.finish()
            dialog.dismiss()
        }

        dialog.show()
    }

    override fun deleteRoom(token: String, roomId: String) {
        lifecycleScope.launch {
            try {
                RoomRequestManager.deleteRoom(token, roomId)
            } catch (e: Exception) {
                log("deleteRoom error: $e")
            } finally {
                recreateActivity()
            }
        }
    }

    private fun recreateActivity() {
        finish()
        overridePendingTransition(0, 0)
        startActivity(intent)
        overridePendingTransition(0, 0)
    }

    private fun showNetworkErrorDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_network_error, null)
        val dialog = AlertDialog.Builder(this).setView(dialogView).setCancelable(false).create()
        dialogView.findViewById<TextView>(R.id.tv_session).setOnClickListener {
            finishAffinity()
            dialog.dismiss()
        }
        dialog.show()
    }

    override fun onRestart() {
        super.onRestart()
        checkSession()
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (!isRootFragment(supportFragmentManager)) {
                supportFragmentManager.popBackStack()
            } else {
                if (doubleBackToExitPressedOnce) {
                    finish()
                } else {
                    doubleBackToExitPressedOnce = true
                    shortToast("한 번 더 누르면 종료됩니다.")
                    handler.postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
                }
            }
        }
    }
}
