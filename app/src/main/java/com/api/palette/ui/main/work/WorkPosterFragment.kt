package com.api.palette.ui.main.work

import android.os.Bundle
import android.os.Parcelable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.api.palette.application.PaletteApplication
import com.api.palette.data.chat.ChatRequestManager
import com.api.palette.data.error.CustomException
import com.api.palette.databinding.FragmentWorkPosterBinding
import com.api.palette.ui.util.ContextRetainer
import com.api.palette.ui.util.logE
import com.api.palette.ui.util.shortToast
import com.bumptech.glide.Glide
import kotlinx.coroutines.*

class WorkPosterFragment : Fragment() {

    private lateinit var binding: FragmentWorkPosterBinding
    private lateinit var imageAdapter: ImageAdapter
    private lateinit var staggeredGridLayoutManager: StaggeredGridLayoutManager

    private var isLoading = false
    private var isLayoutSorting = false
    private var currentPage = 0
    private val pageSize = 10

    private val totalImageList = mutableListOf<String>()
    private var hasMoreImages = true

    private var layoutManagerState: Parcelable? = null
    private var lastVisibleItemPosition = 0

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
        val restoredList = savedInstanceState?.getStringArrayList("totalImageList")

        if (restoredList != null) {
            totalImageList.clear()
            totalImageList.addAll(restoredList)
            imageAdapter.setImages(totalImageList)
        }

        if (savedInstanceState != null) {
            layoutManagerState = savedInstanceState.getParcelable("layoutManagerState")
            currentPage = savedInstanceState.getInt("currentPage", 0)
            totalImageList.addAll(savedInstanceState.getStringArrayList("totalImageList") ?: listOf())
            hasMoreImages = savedInstanceState.getBoolean("hasMoreImages", true)

            layoutManagerState?.let {
                binding.rvImageList.layoutManager?.onRestoreInstanceState(it)
            }
        }

        if (totalImageList.isEmpty()) {
            loadImageList(isRefresh = true)
        } else {
            restoreImages()
            layoutManagerState?.let {
                binding.rvImageList.layoutManager?.onRestoreInstanceState(it)
            }
        }
    }

    private fun setupRecyclerView() {
        staggeredGridLayoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL).apply {
            gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS
        }

        binding.rvImageList.layoutManager = staggeredGridLayoutManager

        imageAdapter = ImageAdapter(mutableListOf())
        binding.rvImageList.adapter = imageAdapter

        binding.rvImageList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (hasMoreImages && !isLayoutSorting && !isLoading && !recyclerView.canScrollVertically(1)) {
                        loadImageList()
                    }
                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val lastPositions = staggeredGridLayoutManager.findLastVisibleItemPositions(null)
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

        viewLifecycleOwner.lifecycleScope.launch {
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

                if (imageList.images.isEmpty()) {
                    hasMoreImages = false
                    binding.swipeRefreshLayout.isRefreshing = false
                    isLoading = false
                    isLayoutSorting = false
                    return@launch
                }

                totalImageList.addAll(imageList.images)

                updateUI(imageList.images, isRefresh)
                currentPage++

                binding.rvImageList.viewTreeObserver.addOnGlobalLayoutListener {
                    if (layoutManagerState == null) {
                        staggeredGridLayoutManager.invalidateSpanAssignments()
                    }
                }
            } catch (e: Exception) {
                logE("Error: ${e.message}")
            } finally {
                isLoading = false
                isLayoutSorting = false
                binding.swipeRefreshLayout.isRefreshing = false
            }
        }
    }

    private fun restoreImages() {
        updateUI(totalImageList, true)

        layoutManagerState?.let { state ->
            binding.rvImageList.layoutManager?.onRestoreInstanceState(state)
        } ?: run {
            binding.rvImageList.post {
                staggeredGridLayoutManager.scrollToPositionWithOffset(lastVisibleItemPosition, 0)
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

        binding.rvImageList.post {
            staggeredGridLayoutManager.invalidateSpanAssignments()
            binding.rvImageList.requestLayout()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        val layoutManagerState = binding.rvImageList.layoutManager?.onSaveInstanceState()
        outState.putParcelable("layoutManagerState", layoutManagerState)
        outState.putInt("currentPage", currentPage)
        outState.putStringArrayList("totalImageList", ArrayList(totalImageList))
        outState.putBoolean("hasMoreImages", hasMoreImages)

        val lastPositions = staggeredGridLayoutManager.findLastVisibleItemPositions(null)
        outState.putIntArray("lastVisiblePositions", lastPositions)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)

        if (savedInstanceState != null) {
            layoutManagerState = savedInstanceState.getParcelable("layoutManagerState")
            currentPage = savedInstanceState.getInt("currentPage", 0)
            totalImageList.addAll(savedInstanceState.getStringArrayList("totalImageList") ?: listOf())
            hasMoreImages = savedInstanceState.getBoolean("hasMoreImages", true)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Glide.get(requireContext()).clearMemory()
    }

    override fun onStop() {
        super.onStop()
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            Glide.get(requireContext()).clearDiskCache()
        }
    }
}
