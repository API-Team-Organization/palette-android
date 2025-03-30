package com.api.palette.presentation.main.work.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.api.palette.domain.chat.usecase.GetImageListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class WorkViewModel @Inject constructor(
    private val getImageListUseCase: GetImageListUseCase
) : ViewModel() {

    private val _imageList = MutableLiveData<List<String>>()
    val imageList: LiveData<List<String>> get() = _imageList

    suspend fun fetchImages(token: String, page: Int, size: Int): Result<List<String>> {
        return runCatching {
            val response = getImageListUseCase(token, page, size)
            if (response.isSuccessful) {
                val images = response.body()?.data?.images.orEmpty()
                _imageList.postValue(images)
                images
            } else {
                throw HttpException(response)
            }
        }
    }
}
