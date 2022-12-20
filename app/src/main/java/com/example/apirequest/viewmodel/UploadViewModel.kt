package com.example.apirequest.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.apirequest.api.RetrofitClient
import com.example.apirequest.event.Event
import com.example.apirequest.models.FileResponse
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UploadViewModel : ViewModel() {
    private val statusMessage = MutableLiveData<Event<String>>()
    val message : LiveData<Event<String>>
        get() = statusMessage
    val token = MutableLiveData<String>()
    val progressBody = MutableLiveData<RequestBody>()

    fun upload(onSuccess: ()->Unit){

        RetrofitClient.instance.uploadFile(token = token.value.toString(), body = progressBody.value!!)
            .enqueue(object : Callback<FileResponse> {
                override fun onResponse(
                    call: Call<FileResponse>,
                    response: Response<FileResponse>,
                ) {
                    if (response.isSuccessful){
                        onSuccess()
                        statusMessage.value = Event("Uploaded")
                    }
                    else{
                        statusMessage.value = Event("Can't Uploaded")
                    }
                }
                override fun onFailure(call: Call<FileResponse>, t: Throwable) {
                    statusMessage.value = Event("${t.message}")
                }
            })
    }
}