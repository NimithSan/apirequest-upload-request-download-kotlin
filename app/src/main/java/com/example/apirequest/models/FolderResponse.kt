package com.example.apirequest.models


data class FolderResponse(
    val _id: String,
    val userId: String,
    val name: String,
    val description: String,
    val createdAt : String,
    val updatedAt : String,
    val __v : String,
)
