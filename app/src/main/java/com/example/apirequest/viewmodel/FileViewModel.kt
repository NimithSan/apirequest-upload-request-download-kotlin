package com.example.apirequest.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.apirequest.api.RetrofitClient
import com.example.apirequest.event.Event
import com.example.apirequest.filemodels.Data
import com.example.apirequest.filemodels.RetrieveFileResponse
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import java.io.FileOutputStream
import java.io.InputStream


class FileViewModel: ViewModel() {

    //for File Fragment
    private val statusMessage = MutableLiveData<Event<String>>()
    val message : LiveData<Event<String>>
        get() = statusMessage

    val token = MutableLiveData<String>()

    private val _response = MutableLiveData<List<Data>>()
    val response: LiveData<List<Data>>
        get() = _response

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean>
        get() = _loading
    var position = MutableLiveData<Int>()
    val folderId = MutableLiveData<String>()

    fun getFile() {
        RetrofitClient.instance.getFile(token.value.toString(), folderId = folderId.value.toString())
            .enqueue(object : Callback<RetrieveFileResponse> {
                override fun onResponse(
                    call: Call<RetrieveFileResponse>,
                    response: Response<RetrieveFileResponse>,
                ) {
                    if (response.isSuccessful){
                        _response.value = response.body()!!.data
                        _loading.value = false
                    }else{
                        statusMessage.value = Event("Fail to Retrieve")
                    }
                }
                override fun onFailure(call: Call<RetrieveFileResponse>, t: Throwable) {
                    _loading.value = false
                }
            })
    }
    fun deleteFile(file: Data){
        RetrofitClient.instance.deleteFile(token = token.value.toString(), _id = file._id)
            .enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>,
                ) {
                    if(response.isSuccessful){
                        val mutableList=_response.value?.toMutableList()
                        mutableList?.remove(file)
                        _response.value=mutableList?.toList()
                        statusMessage.value = Event("Deleted")
                    }else{
                        statusMessage.value = Event("Fail to Delete")
                    }
                }
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    statusMessage.value = Event("${t.message}")
                }
            })
    }
}