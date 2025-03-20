package com.api.palette.presentation.main

import android.app.Activity
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
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.api.palette.MainActivity
import com.api.palette.R
import com.api.palette.application.PaletteApplication
import com.api.palette.databinding.ActivityServiceBinding
import com.api.palette.presentation.room.CreateMediaFragment
import com.api.palette.presentation.settings.SettingFragment
import com.api.palette.presentation.work.WorkFragment
import com.api.palette.ui.util.shortToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ServiceActivity : AppCompatActivity() {

    private lateinit var binding: ActivityServiceBinding
    private var currentTab: BottomTab? = null
    private val createMediaFragment = CreateMediaFragment()
    private val workFragment = WorkFragment()
    private val settingFragment = SettingFragment()
    private lateinit var vibrator: Vibrator
    private var doubleBackToExitPressedOnce = false
    private val handler = Handler(Looper.getMainLooper())

    enum class BottomTab(val eventName: String) {
        HOME("click_home"),
        SEARCH("click_search"),
        SETTING("click_setting");

        companion object {
            fun from(eventName: String) = values().find { it.eventName == eventName }
        }
    }

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (supportFragmentManager.backStackEntryCount > 0) {
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityServiceBinding.inflate(layoutInflater)
        setContentView(binding.root)
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        supportFragmentManager.beginTransaction()
            .replace(binding.mainContent.id, createMediaFragment)
            .commit()
        currentTab = BottomTab.HOME
        // 하단바 클릭 이벤트 등 추가 구현 (필요시)
    }

    fun bottomVisible(visibility: Boolean) {
        binding.bottomBar.visibility = if (visibility) View.VISIBLE else View.GONE
    }

    fun sessionDialog(context: Context) {
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
}
