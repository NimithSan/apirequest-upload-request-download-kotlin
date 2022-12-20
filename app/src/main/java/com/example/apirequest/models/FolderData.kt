package com.example.apirequest.models

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "Folder")
data class FolderData (
    @PrimaryKey
    val _id: String,
    val name: String,
    val description: String,
    val createdAt: String,
    val updatedAt: String,
)