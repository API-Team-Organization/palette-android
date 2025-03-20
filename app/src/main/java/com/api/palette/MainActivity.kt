package com.api.palette

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.api.palette.application.PaletteApplication
import com.api.palette.application.PreferenceManager
import com.api.palette.common.Constant
import com.api.palette.databinding.ActivityMainBinding
import com.api.palette.presentation.main.ServiceActivity
import com.api.palette.ui.util.log
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        PaletteApplication.prefs = PreferenceManager(application)
        handleAuth()
        initView()
    }

    private fun initView() {
        val prefs = PaletteApplication.prefs
        val isFirst = prefs.isFirst
        if (isFirst) {
            log("최초 실행입니다.")
        } else {
            log("최초실행이 아닙니다.")
        }
    }

    private fun handleAuth() {
        if (PaletteApplication.prefs.token.isNotEmpty()) {
            val intent = Intent(this, ServiceActivity::class.java)
            startActivity(intent)
            finish()
        }
        else {
            Log.d(Constant.TAG,"token is Empty")
        }
    }
}
