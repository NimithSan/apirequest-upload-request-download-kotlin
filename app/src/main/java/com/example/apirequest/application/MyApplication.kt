package com.example.apirequest.application

import android.app.Application
import com.example.apirequest.database.DatabaseClass

class MyApplication: Application() {
    val database: DatabaseClass by lazy { DatabaseClass.getDatabase(this) }
}