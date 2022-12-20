package com.example.apirequest.viewmodel

import android.widget.Toast
import androidx.lifecycle.*
import androidx.navigation.fragment.findNavController
import com.example.apirequest.FolderFormFragmentDirections
import com.example.apirequest.LoginFragmentDirections
import com.example.apirequest.api.RetrofitClient
import com.example.apirequest.database.FolderDao
import com.example.apirequest.event.Event
import com.example.apirequest.models.*
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FolderViewModel(private val folderDao: FolderDao): ViewModel() {

    val allFolder: LiveData<List<FolderData>> = folderDao.getFolders()
    //login file variable
    val email = MutableLiveData<String>()
    val pass = MutableLiveData<String>()
    val token = MutableLiveData<String>()
    //Toast
    private val statusMessage = MutableLiveData<Event<String>>()
    val message : LiveData<Event<String>>
        get() = statusMessage
    val id = MutableLiveData<String>()
    val name = MutableLiveData<String>()
    val des =  MutableLiveData<String>()

    fun insertFolder(folderData: List<FolderData>){
        viewModelScope.launch {
            folderDao.insertAllFolder(folderData)
        }
    }
    fun deleteAll(){
        viewModelScope.launch {
            folderDao.deleteAllFolders()
        }
    }
    fun updateFolder(folderData: FolderData){
        viewModelScope.launch {
            folderDao.updateFolder(folderData)
        }
    }
    fun retrieveFolder(id: String): LiveData<FolderData>{
        return folderDao.getFolder(id)
    }
    fun login(onSuccess: () -> Unit){
        RetrofitClient.instance.login(email.value.toString(), pass.value.toString())
            .enqueue(object : Callback<LoginResponse> {
                override fun onResponse(
                    call: Call<LoginResponse>,
                    response: Response<LoginResponse>,
                ) {
                    if(response.isSuccessful){
                        token.value = response.body()!!.token
                        onSuccess()
                    }else{
                        statusMessage.value = Event("Wrong Password or Gmail")
                    }
                }
                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    statusMessage.value = Event("${t.message}")
                }
            })
    }
    fun retrieveFolders(){
        RetrofitClient.instance.getData(token = token.value.toString())
            .enqueue(object : Callback<RetrieveFolderResponse>{
                override fun onResponse(
                    call: Call<RetrieveFolderResponse>,
                    response: Response<RetrieveFolderResponse>,
                ) {
                    if (response.isSuccessful){
                        insertFolder(response.body()!!.data)
                    }else{
                        statusMessage.value = Event(response.message())
                    }
                }
                override fun onFailure(call: Call<RetrieveFolderResponse>, t: Throwable) {
                    statusMessage.value = Event("${t.message}")
                }
            })
    }
    fun createFolder(onSuccess: ()->Unit){
        RetrofitClient.instance.create_folder(token = token.value.toString(),id.value.toString(),name.value.toString(),des.value.toString())
            .enqueue(object : Callback<FolderResponse>{
                override fun onResponse(
                    call: Call<FolderResponse>,
                    response: Response<FolderResponse>,
                ) {
                    if (response.isSuccessful){
                        statusMessage.value = Event("Success")
                        onSuccess()
                    }else{
                        statusMessage.value = Event(response.message())
                    }
                }
                override fun onFailure(call: Call<FolderResponse>, t: Throwable) {
                    statusMessage.value = Event("${t.message}")
                }
            })
    }
}
class FolderViewModelFactory(private val folderDao: FolderDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FolderViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FolderViewModel(folderDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}