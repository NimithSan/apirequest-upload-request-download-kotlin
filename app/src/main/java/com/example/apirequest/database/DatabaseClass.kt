package com.example.apirequest.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.apirequest.models.FolderData


@Database(entities = [FolderData::class], version = 1, exportSchema = false)
abstract class DatabaseClass : RoomDatabase(){
    abstract fun folderDao() : FolderDao
    companion object {
        @Volatile
        private var INSTANCE: DatabaseClass? = null
        fun getDatabase(context: Context): DatabaseClass {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DatabaseClass::class.java,
                    "folder_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }
}