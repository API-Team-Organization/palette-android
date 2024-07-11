package com.example.palette

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.palette.application.PaletteApplication
import com.example.palette.application.PreferenceManager
import com.example.palette.common.Constant
import com.example.palette.databinding.ActivityMainBinding
import com.example.palette.ui.main.ServiceActivity
import com.example.palette.ui.util.log


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
        val pref = getSharedPreferences("isFirst", MODE_PRIVATE)
        val first = pref.getBoolean("isFirst", true)
        val pref2 = getSharedPreferences("notFirst", MODE_PRIVATE)
        pref.getBoolean("notFirst", false)

        val editor2 = pref2.edit()

        if (first) {
            Log.d("Is first Time?", "first")
            val editor = pref.edit()
            editor.putBoolean("isFirst", false)
            editor2.putBoolean("notFirst", false)
            editor.apply()

            log("최초 실행입니다.")

        } else {
            editor2.putBoolean("notFirst", true)
            log("최초실행이 아닙니다.")
        }

        editor2.apply()
    }

    private fun handleAuth() {
        if (PaletteApplication.prefs.token.isNotEmpty()) {
            Log.d(Constant.TAG,"token is not Empty")

            val intent = Intent(this, ServiceActivity::class.java)
            startActivity(intent)
            finish()
        }
        else {
            Log.d(Constant.TAG,"token is Empty")
        }
    }
}