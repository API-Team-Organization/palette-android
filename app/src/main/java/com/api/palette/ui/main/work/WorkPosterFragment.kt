package com.api.palette.ui.main.work

import android.os.Bundle
import android.os.Parcelable
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

    private var layoutManagerState: Parcelable? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWorkPosterBinding.inflate(inflater, container, false)

        ContextRetainer.init(requireActivity())

        setupRecyclerView()
        setupSwipeRefresh()

        if (layoutManagerState != null) {
            binding.rvImageList.layoutManager?.onRestoreInstanceState(layoutManagerState)
        }

        loadImageList(isRefresh = true)

        return binding.root
    }

    private fun setupRecyclerView() {
        val staggeredGridLayoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL).apply {
            gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_NONE
        }
        binding.rvImageList.layoutManager = staggeredGridLayoutManager
        imageAdapter = ImageAdapter(mutableListOf())
        binding.rvImageList.adapter = imageAdapter

        // Scroll Listener for pagination
        binding.rvImageList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (!isLoading && !binding.swipeRefreshLayout.isRefreshing) {
                    val layoutManager = recyclerView.layoutManager as StaggeredGridLayoutManager
                    val totalItemCount = layoutManager.itemCount
                    val lastVisibleItemPositions = layoutManager.findLastVisibleItemPositions(null)

                    // Check if we are at the end of the list
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

        binding.rvImageList.layoutManager?.apply {
            if (this is StaggeredGridLayoutManager) {
                invalidateSpanAssignments() // 가로 세로 재배치 무시
            }
        }

        binding.rvImageList.requestLayout() // 재배치 요청
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel()
    }

    override fun onPause() {
        super.onPause()
        layoutManagerState = binding.rvImageList.layoutManager?.onSaveInstanceState()
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        layoutManagerState?.let {
            binding.rvImageList.layoutManager?.onRestoreInstanceState(it)
        }
    }

    override fun onResume() {
        super.onResume()
        layoutManagerState?.let {
            binding.rvImageList.layoutManager?.onRestoreInstanceState(it)
        }
    }
}
