package com.example.palette.ui.main.settings

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.palette.R
import com.example.palette.databinding.FragmentAppInfoBinding
import com.example.palette.ui.main.ServiceActivity

class AppInfoFragment : Fragment() {

    private lateinit var binding: FragmentAppInfoBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAppInfoBinding.inflate(inflater, container, false)
        (activity as ServiceActivity).findViewById<View>(R.id.bottomBar).visibility = View.GONE

        binding.appInfo.setOnClickListener {
            goToDevAppInfoPage()
        }

        return binding.root
    }

    override fun onPause() {
        super.onPause()
        (activity as ServiceActivity).findViewById<View>(R.id.bottomBar).visibility = View.VISIBLE
    }

    private fun goToDevAppInfoPage() {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://4-rne5.notion.site/Team-API-2100356bfe554cf58df89b204b3afb8d"))
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            e.printStackTrace()
        }
    }
}