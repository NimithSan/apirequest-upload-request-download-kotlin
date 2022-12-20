package com.example.apirequest.fileutils

import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import android.webkit.MimeTypeMap
import androidx.core.content.FileProvider
import androidx.lifecycle.LiveData
import okhttp3.ResponseBody
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.*


class FileUtils {
    companion object {
        private const val MEDIAL_PATH: String = "medias"

        fun getFilePath(context: Context,fileName:String):String{
            val mediaFolderPath = context.getExternalFilesDir(MEDIAL_PATH)
            val fileImage = File(
                mediaFolderPath,fileName
            )
            return fileImage.absolutePath
        }
        //copy content file from phone to app media folder
        fun getFileFromUri(context: Context, uri: Uri): String? {
            try {
                val extension: String? = if (uri.scheme.equals(ContentResolver.SCHEME_CONTENT)) {
                    val mime = MimeTypeMap.getSingleton()
                    mime.getExtensionFromMimeType(context.contentResolver.getType(uri))
                } else {
                    MimeTypeMap.getFileExtensionFromUrl(
                        Uri.fromFile(uri.path?.let { File(it) }).toString()
                    )
                }
                val mediaFolderPath = context.getExternalFilesDir(MEDIAL_PATH)
                val fileImage = File(
                    mediaFolderPath, UUID.randomUUID().toString() + ".$extension"
                )
                fileImage.createNewFile()
                val inputStream = context.contentResolver.openInputStream(uri)
                val outputStream = FileOutputStream(fileImage)
                inputStream.use { input ->
                    outputStream.use { output ->
                        input?.copyTo(output)
                    }
                }
                return fileImage.absolutePath
            } catch (ex: Exception) {
                ex.printStackTrace()
                return null
            }
        }
        fun openFile(context: Context, filePath: String) {
            try {
                val mime = MimeTypeMap.getSingleton()
                val file = File(filePath)
                val fileExtension = filePath.substring(filePath.lastIndexOf("."))
                val intent = Intent(Intent.ACTION_VIEW)
                if (Build.VERSION.SDK_INT >= 24) {
                    val fileUri = FileProvider.getUriForFile(
                        context, context.packageName + ".provider", file
                    )
                    intent.setDataAndType(
                        fileUri, mime.getMimeTypeFromExtension(fileExtension)
                    )
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    try {
                        context.startActivity(intent)
                    } catch (ex: java.lang.Exception) {
                        val intentChooser = Intent.createChooser(intent, "Open File")
                        context.startActivity(intentChooser)
                    }
                } else {
                    intent.setDataAndType(
                        Uri.fromFile(file),
                        mime.getMimeTypeFromExtension(fileExtension)
                    )
                    context.startActivity(intent)
                }
            } catch (ex: java.lang.Exception) {
                ex.printStackTrace()
                Timber.d("Cannot open file")
            }
        }
    }
}
fun File.isFileExists():Boolean{
    return this.exists() && !this.isDirectory
}

//fun saveFilePath(body: ResponseBody?, pathWhereYouWantToSaveFile: String): String {
//    if (body == null)
//        return ""
//    var input: InputStream? = null
//    try {
//        var done: Long = 0
//        val total = body.contentLength()
//        input = body.byteStream()
//        val fos = FileOutputStream(pathWhereYouWantToSaveFile)
//        fos.use { output ->
//            val buffer = ByteArray(4 * 1024) // or other buffer size
//            var read: Int
//            while (input.read(buffer).also { read = it } != -1) {
//                done+=read
//                val percent = (done.toFloat()/total.toFloat())*100
//                Log.d("download", "$total $done $percent")
//                output.write(buffer, 0, read)
//            }
//            output.flush()
//        }
//        return pathWhereYouWantToSaveFile
//    } catch (e: Exception) {
//        Timber.tag("saveFile").e(e.toString())
//    } finally {
//        input?.close()
//    }
//    return ""
//}