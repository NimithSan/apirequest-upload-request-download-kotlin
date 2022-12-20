package com.example.apirequest.api

import com.example.apirequest.filemodels.RetrieveFileResponse
import com.example.apirequest.models.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface Api {

    @FormUrlEncoded
    @POST("register")
    fun register(
        @Field("first_name") first_name:String,
        @Field("last_name") last_name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ):retrofit2.Call<DefaultResponse>

    @FormUrlEncoded
    @POST("login")
    fun login(
        @Field("email") email: String,
        @Field("password") password: String,
    ):retrofit2.Call<LoginResponse>

    @FormUrlEncoded
    @POST("create_folder")
    fun create_folder(
        @Header("token") token:String,
        @Field("_id") _id: String,
        @Field("name") name: String,
        @Field("description") description: String,
    ):retrofit2.Call<FolderResponse>

    @GET("get_folder")
    fun getData(@Header("token") token:String): Call<RetrieveFolderResponse>

    @FormUrlEncoded
    @POST("update_folder")
    fun updateFolder(
        @Header("token") token : String,
        @Field("_id") _id: String,
        @Field("name") name: String,
        @Field("description") description: String
    ):retrofit2.Call<FolderData>

    //@Multipart
    @POST("upload_file")
    fun uploadFile(
        @Header("token") token : String,
        @Body body:RequestBody,
    ):retrofit2.Call<FileResponse>

    @GET("get_files")
    fun getFile(
        @Header("token") token: String,
        @Query("folderId") folderId: String,
    ): Call<RetrieveFileResponse>

    @Streaming
    @GET
    fun downloadFile(
        @Header("token") token : String,
        @Url fileUrl: String,
    ): Call<ResponseBody>

    @FormUrlEncoded
    @POST("delete_file")
    fun deleteFile(
        @Header("token") token : String,
        @Field("_id") _id: String,
    ): Call<ResponseBody>
}