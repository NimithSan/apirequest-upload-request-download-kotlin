package com.example.apirequest.filemodels

data class RetrieveFileResponse(
    val data: List<Data>,
    val page: Page
)