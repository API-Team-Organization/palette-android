package com.api.palette.ui.main.work

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
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

        binding.rvImageList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as GridLayoutManager
                val totalItemCount = layoutManager.itemCount
                val lastVisibleItem = layoutManager.findLastVisibleItemPosition()

                if (!binding.swipeRefreshLayout.isRefreshing && totalItemCount <= lastVisibleItem + 2) {
                    loadImageList()
                }
            }
        })


        binding.rvImageList.layoutManager = GridLayoutManager(requireContext(), 2)
        imageAdapter = ImageAdapter(mutableListOf())
        binding.rvImageList.adapter = imageAdapter

        binding.swipeRefreshLayout.setOnRefreshListener {
            loadImageList()
        }

        loadImageList()

        return binding.root
    }

    private var currentPage = 0
    private val pageSize = 10

    private fun loadImageList(isRefresh: Boolean = false) {
        if (isRefresh) {
            currentPage = 0
        }

        binding.swipeRefreshLayout.isRefreshing = true

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val token = PaletteApplication.prefs.token
                val response = try {
                    val m = ChatRequestManager.getImageList(token, currentPage, pageSize)
                    log("$m")
                    m
                } catch (e: CustomException) {
                    shortToast(e.errorResponse.message)
                    null
                } ?: return@launch

                val imageList = response.data

                withContext(Dispatchers.Main) {
                    if (imageList.images.isEmpty()) {
                        if (currentPage == 0) {
                            binding.tvNoImages.visibility = View.VISIBLE
                            binding.rvImageList.visibility = View.GONE
                        }
                    } else {
                        binding.tvNoImages.visibility = View.GONE
                        binding.rvImageList.visibility = View.VISIBLE
                        if (isRefresh) {
                            imageAdapter.updateImages(imageList.images)
                        } else {
                            imageAdapter.addImages(imageList.images)
                        }
                        currentPage++
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
