package com.api.palette.presentation.main.settings.info

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.api.palette.R
import com.api.palette.databinding.FragmentAppInfoBinding
import com.api.palette.presentation.main.ServiceActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AppInfoFragment : Fragment() {

    private lateinit var binding: FragmentAppInfoBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAppInfoBinding.inflate(inflater, container, false)
        (activity as ServiceActivity).findViewById<View>(R.id.bottomBar).visibility = View.GONE

        binding.appInfo.setOnClickListener { goToDevAppInfoPage() }

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        (activity as ServiceActivity).findViewById<View>(R.id.bottomBar).visibility = View.VISIBLE
    }

    private fun goToDevAppInfoPage() {
        try {
            val url = "https://4-rne5.notion.site/Team-API-2100356bfe554cf58df89b204b3afb8d"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            e.printStackTrace()
        }
    }
}
