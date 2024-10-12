package com.example.palette.ui.main.work

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.palette.application.PaletteApplication
import com.example.palette.data.chat.ChatRequestManager
import com.example.palette.data.error.CustomException
import com.example.palette.databinding.FragmentWorkPosterBinding
import com.example.palette.ui.util.log
import com.example.palette.ui.util.logE
import com.example.palette.ui.util.shortToast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WorkPosterFragment : Fragment() {

    private lateinit var binding: FragmentWorkPosterBinding
    private lateinit var imageAdapter: ImageAdapter
    private lateinit var rvImageList: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWorkPosterBinding.inflate(inflater, container, false)

        rvImageList = binding.rvImageList
        rvImageList.layoutManager = GridLayoutManager(requireContext(), 2)

        imageAdapter = ImageAdapter(listOf())
        rvImageList.adapter = imageAdapter

        loadImageList()

        return binding.root
    }

    private fun loadImageList() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val token = PaletteApplication.prefs.token
                val page = 0
                val size = 20 // TODO: Constants 로 분리
                log("aaaa")
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
                    imageAdapter.updateImages(imageList.images)
                }
            } catch (e: Exception) {
                logE("Error: ${e.message}")
            }
        }
    }
}
