package com.example.apirequest.models

data class RetrieveFolderResponse(
    val data: List<FolderData>,
    val page: Page
)