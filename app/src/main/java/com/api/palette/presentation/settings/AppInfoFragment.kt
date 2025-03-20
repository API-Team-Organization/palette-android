package com.api.palette.presentation.settings

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.api.palette.databinding.FragmentAppInfoBinding
import com.api.palette.presentation.main.ServiceActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AppInfoFragment : Fragment() {
    private var _binding: FragmentAppInfoBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAppInfoBinding.inflate(inflater, container, false)

        (activity as? ServiceActivity)?.findViewById<View>(com.api.palette.R.id.bottomBar)?.visibility = View.GONE

        binding.appInfo.setOnClickListener {
            goToDevAppInfoPage()
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (activity as? ServiceActivity)?.findViewById<View>(com.api.palette.R.id.bottomBar)?.visibility = View.VISIBLE
        _binding = null
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
