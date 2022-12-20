package com.example.apirequest.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.apirequest.api.RetrofitClient
import com.example.apirequest.event.Event
import com.example.apirequest.models.DefaultResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterViewModel:ViewModel(){
    //Toast
    private val statusMessage = MutableLiveData<Event<String>>()
    val message : LiveData<Event<String>>
        get() = statusMessage
    //variable
    val firstname = MutableLiveData<String>()
    val lastname = MutableLiveData<String>()
    val email = MutableLiveData<String>()
    val password = MutableLiveData<String>()

    fun register(onSuccess: () -> Unit){
        RetrofitClient.instance.register(
            first_name = firstname.value.toString(),
            last_name = lastname.value.toString(),
            email = email.value.toString(),
            password = password.value.toString()
        ).enqueue(object : Callback<DefaultResponse> {
            override fun onResponse(
                call: Call<DefaultResponse>,
                response: Response<DefaultResponse>,
            ) {
                if (response.isSuccessful){
                    statusMessage.value = Event("Success Account")
                    onSuccess()
                }else{
                    statusMessage.value = Event("account already exists")
                }
            }
            override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                statusMessage.value = Event("${t.message}")
            }
        })
    }
}