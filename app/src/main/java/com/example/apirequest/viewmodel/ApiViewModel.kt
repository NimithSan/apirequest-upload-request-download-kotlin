package com.example.apirequest.viewmodel

import android.app.Application
import android.media.session.MediaSession.Token
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.findNavController
import com.example.apirequest.LoginFragmentDirections
import com.example.apirequest.api.RetrofitClient
import com.example.apirequest.models.LoginResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

//class ApiViewModel:ViewModel() {

//    fun test(onSuccess: ()->Unit,onError: ()->Unit){
//
//        onError()
//        //code login
//        onSuccess()
//    }
//    var name : String? = null
//    fun returnName():String{
//        return name.toString()
//    }
//    fun getName(listener: LoginListener){
//        name = listener.getName()
//    }
//}
//interface LoginListener{
//    fun getName()
//}