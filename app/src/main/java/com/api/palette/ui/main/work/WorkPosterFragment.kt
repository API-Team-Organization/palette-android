package com.api.palette.ui.main.work

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.api.palette.application.PaletteApplication
import com.api.palette.data.chat.ChatRequestManager
import com.api.palette.data.error.CustomException
import com.api.palette.databinding.FragmentWorkPosterBinding
import com.api.palette.ui.util.logE
import com.api.palette.ui.util.shortToast
import kotlinx.coroutines.*

class WorkPosterFragment : Fragment() {

    private lateinit var binding: FragmentWorkPosterBinding
    private lateinit var imageAdapter: ImageAdapter
    private var isLoading = false
    private var currentPage = 0
    private val pageSize = 10
    private val coroutineScope = CoroutineScope(Dispatchers.Main + Job())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWorkPosterBinding.inflate(inflater, container, false)

        ContextRetainer.init(requireActivity())

        setupRecyclerView()
        setupSwipeRefresh()
        loadImageList(isRefresh = true)

        return binding.root
    }

    private fun setupRecyclerView() {
        binding.rvImageList.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL).apply {
            gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_NONE
        }
        imageAdapter = ImageAdapter(mutableListOf())
        binding.rvImageList.adapter = imageAdapter

        binding.rvImageList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (!isLoading && !binding.swipeRefreshLayout.isRefreshing) {
                    val layoutManager = recyclerView.layoutManager as StaggeredGridLayoutManager
                    val totalItemCount = layoutManager.itemCount
                    val lastVisibleItemPositions = layoutManager.findLastVisibleItemPositions(null)
                    val lastVisibleItem = lastVisibleItemPositions.maxOrNull() ?: 0
                    if (totalItemCount <= lastVisibleItem + 1) {
                        loadImageList()
                    }
                }
            }
        })
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            if (!isLoading) {
                loadImageList(isRefresh = true)
            }
        }
    }

    private fun loadImageList(isRefresh: Boolean = false) {
        if (isLoading) return

        if (isRefresh) {
            currentPage = 0
            imageAdapter.clearImages()
        }

        isLoading = true
        binding.swipeRefreshLayout.isRefreshing = true

        coroutineScope.launch {
            try {
                val token = PaletteApplication.prefs.token
                val response = withContext(Dispatchers.IO) {
                    try {
                        ChatRequestManager.getImageList(token, currentPage, pageSize)
                    } catch (e: CustomException) {
                        withContext(Dispatchers.Main) {
                            shortToast(e.errorResponse.message)
                        }
                        null
                    }
                } ?: return@launch

                val imageList = response.data

                updateUI(imageList.images, isRefresh)
                currentPage++
            } catch (e: Exception) {
                logE("Error: ${e.message}")
            } finally {
                isLoading = false
                binding.swipeRefreshLayout.isRefreshing = false
            }
        }
    }

    private fun updateUI(images: List<String>, isRefresh: Boolean) {
        if (images.isEmpty() && currentPage == 0) {
            binding.tvNoImages.visibility = View.VISIBLE
            binding.rvImageList.visibility = View.GONE
        } else {
            binding.tvNoImages.visibility = View.GONE
            binding.rvImageList.visibility = View.VISIBLE
            if (isRefresh) {
                imageAdapter.updateImages(images)
            } else {
                imageAdapter.addImages(images)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel()
    }
}