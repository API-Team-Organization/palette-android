package com.api.palette.presentation.main.work

import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.api.palette.application.PaletteApplication
import com.api.palette.data.chat.ChatRequestManager
import com.api.palette.data.chat.data.ImageListResponse
import com.api.palette.data.error.CustomException
import com.api.palette.databinding.FragmentWorkPosterBinding
import com.api.palette.presentation.main.work.adapter.ImageAdapter
import com.api.palette.presentation.util.ContextRetainer
import com.api.palette.presentation.util.logE
import com.api.palette.presentation.util.shortToast
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class WorkPosterFragment : Fragment() {

    private lateinit var binding: FragmentWorkPosterBinding
    private lateinit var imageAdapter: ImageAdapter
    private lateinit var layoutManager: StaggeredGridLayoutManager

    private var layoutManagerState: Parcelable? = null
    private var lastVisibleItemPosition = 0
    private var isLoading = false
    private var isLayoutSorting = false
    private var hasMoreImages = true
    private var currentPage = 0
    private val pageSize = 10
    private val totalImageList = mutableListOf<String>()

    @Inject lateinit var chatRequestManager: ChatRequestManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWorkPosterBinding.inflate(inflater, container, false)
        ContextRetainer.init(requireActivity())
        setupRecyclerView()
        setupSwipeRefresh()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        savedInstanceState?.let {
            layoutManagerState = it.getParcelable("layoutManagerState")
            currentPage = it.getInt("currentPage", 0)
            totalImageList.addAll(it.getStringArrayList("totalImageList") ?: emptyList())
            hasMoreImages = it.getBoolean("hasMoreImages", true)
        }

        if (totalImageList.isEmpty()) {
            loadImageList(isRefresh = true)
        } else {
            restoreImages()
        }
    }

    private fun setupRecyclerView() {
        layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL).apply {
            gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS
        }
        binding.rvImageList.layoutManager = layoutManager
        imageAdapter = ImageAdapter(mutableListOf()) { loadImageList(isRefresh = true) }
        binding.rvImageList.adapter = imageAdapter

        binding.rvImageList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(rv: RecyclerView, newState: Int) {
                super.onScrollStateChanged(rv, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE &&
                    hasMoreImages && !isLayoutSorting && !isLoading &&
                    !rv.canScrollVertically(1)
                ) {
                    loadImageList()
                }
            }

            override fun onScrolled(rv: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(rv, dx, dy)
                val lastPositions = layoutManager.findLastVisibleItemPositions(null)
                lastVisibleItemPosition = lastPositions.maxOrNull() ?: 0
            }
        })
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            if (!isLoading && !isLayoutSorting) {
                loadImageList(isRefresh = true)
            }
        }
    }

    private fun loadImageList(isRefresh: Boolean = false) {
        if (isLoading || isLayoutSorting) return
        if (!isRefresh && !hasMoreImages) {
            binding.swipeRefreshLayout.isRefreshing = false
            return
        }

        lifecycleScope.launch {
            if (isRefresh) {
                currentPage = 0
                totalImageList.clear()
                imageAdapter.clearImages()
                hasMoreImages = true
            }

            isLoading = true
            isLayoutSorting = true
            binding.swipeRefreshLayout.isRefreshing = true

            try {
                val token = PaletteApplication.prefs.token
                val response = withContext(Dispatchers.IO) {
                    runCatching {
                        chatRequestManager.getImageList(token, currentPage, pageSize)
                    }.onFailure {
                        if (it is CustomException) {
                            withContext(Dispatchers.Main) { shortToast(it.errorResponse.message) }
                        }
                    }.getOrNull()
                } ?: return@launch

                val imageData = response.body()?.data ?: ImageListResponse(emptyList())

                if (imageData.images.isEmpty()) {
                    hasMoreImages = false
                } else {
                    totalImageList.addAll(imageData.images)
                    updateUI(imageData.images, isRefresh)
                    currentPage++
                }

                binding.rvImageList.post {
                    layoutManager.invalidateSpanAssignments()
                    binding.rvImageList.requestLayout()
                }

            } catch (e: Exception) {
                logE("Image Load Error: ${e.message}")
            } finally {
                isLoading = false
                isLayoutSorting = false
                binding.swipeRefreshLayout.isRefreshing = false
            }
        }
    }

    private fun restoreImages() {
        updateUI(totalImageList, true)
        layoutManagerState?.let {
            binding.rvImageList.layoutManager?.onRestoreInstanceState(it)
        } ?: layoutManager.scrollToPositionWithOffset(lastVisibleItemPosition, 0)
    }

    private fun updateUI(images: List<String>, isRefresh: Boolean) {
        if (images.isEmpty() && currentPage == 0) {
            binding.tvNoImages.visibility = View.VISIBLE
            binding.rvImageList.visibility = View.GONE
        } else {
            binding.tvNoImages.visibility = View.GONE
            binding.rvImageList.visibility = View.VISIBLE
            if (isRefresh) imageAdapter.setImages(images)
            else imageAdapter.addImages(images)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable("layoutManagerState", binding.rvImageList.layoutManager?.onSaveInstanceState())
        outState.putInt("currentPage", currentPage)
        outState.putStringArrayList("totalImageList", ArrayList(totalImageList))
        outState.putBoolean("hasMoreImages", hasMoreImages)
    }

    override fun onDestroy() {
        super.onDestroy()
        Glide.get(requireContext()).clearMemory()
    }

    override fun onStop() {
        super.onStop()
        lifecycleScope.launch(Dispatchers.IO) {
            Glide.get(requireContext()).clearDiskCache()
        }
    }
}
