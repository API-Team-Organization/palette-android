package com.api.palette.ui.main.work

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.api.palette.application.PaletteApplication
import com.api.palette.data.chat.ChatRequestManager
import com.api.palette.data.error.CustomException
import com.api.palette.databinding.FragmentWorkPosterBinding
import com.api.palette.ui.util.log
import com.api.palette.ui.util.logE
import com.api.palette.ui.util.shortToast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WorkPosterFragment : Fragment() {

    private lateinit var binding: FragmentWorkPosterBinding
    private lateinit var imageAdapter: ImageAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWorkPosterBinding.inflate(inflater, container, false)

        binding.rvImageList.layoutManager = GridLayoutManager(requireContext(), 2)
        imageAdapter = ImageAdapter(listOf())
        binding.rvImageList.adapter = imageAdapter

        binding.swipeRefreshLayout.setOnRefreshListener {
            loadImageList()
        }

        loadImageList()

        return binding.root
    }

    private fun loadImageList() {
        binding.swipeRefreshLayout.isRefreshing = true

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val token = PaletteApplication.prefs.token
                val page = 0
                val size = 20 // TODO: Constants로 분리
                val response = try {
                    val m = ChatRequestManager.getImageList(token, page, size)
                    log("$m")
                    m
                } catch (e: CustomException) {
                    shortToast(e.errorResponse.message)
                    null
                } ?: return@launch

                val imageList = response.data

                withContext(Dispatchers.Main) {
                    if (imageList.images.isEmpty()) {
                        binding.tvNoImages.visibility = View.VISIBLE
                        binding.rvImageList.visibility = View.GONE
                    } else {
                        binding.tvNoImages.visibility = View.GONE
                        binding.rvImageList.visibility = View.VISIBLE
                        imageAdapter.updateImages(imageList.images)
                    }
                    binding.swipeRefreshLayout.isRefreshing = false
                }
            } catch (e: Exception) {
                logE("Error: ${e.message}")
                binding.swipeRefreshLayout.isRefreshing = false
            }
        }
    }
}
