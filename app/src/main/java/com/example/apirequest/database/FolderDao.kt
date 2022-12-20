package com.example.apirequest.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.apirequest.models.FolderData

@Dao
interface FolderDao{
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAllFolder(folderData: List<FolderData>)

    @Query("SELECT * FROM Folder ORDER BY name ASC")
    fun getFolders(): LiveData<List<FolderData>>

    @Query("Delete from Folder")
    suspend fun deleteAllFolders()

    @Update
    suspend fun updateFolder(folderData: FolderData)

    @Query("SELECT * FROM Folder WHERE _id = :id")
    fun getFolder(id:String):LiveData<FolderData>

}