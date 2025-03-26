package com.api.palette

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.api.palette.application.PreferenceManager
import com.api.palette.common.Constant
import com.api.palette.databinding.ActivityMainBinding
import com.api.palette.presentation.main.ServiceActivity
import com.api.palette.presentation.util.log
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    @Inject lateinit var prefs: PreferenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (isLoggedIn()) {
            navigateToService()
            return
        }

        setContentView(binding.root)
        logFirstLaunchInfo()
    }

    private fun isLoggedIn(): Boolean = prefs.token.isNotEmpty()

    private fun navigateToService() {
        log("${Constant.TAG}: token is not empty, navigating to ServiceActivity")
        startActivity(Intent(this, ServiceActivity::class.java))
        finish()
    }

    private fun logFirstLaunchInfo() {
        val isFirstLaunch = prefs.isFirst
        log(if (isFirstLaunch) "최초 실행입니다." else "최초 실행이 아닙니다.")
    }
}
