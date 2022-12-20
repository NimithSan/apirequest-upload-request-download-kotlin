package com.example.apirequest.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.apirequest.R
import com.example.apirequest.databinding.FileListBinding
import com.example.apirequest.filemodels.Data
import com.example.apirequest.fileutils.FileUtils
import com.example.apirequest.fileutils.isFileExists
import okhttp3.ResponseBody
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.*

class FilesAdapter(private val context: Context,private val onClickListener: OnClickListener): ListAdapter<Data,FilesAdapter.FileViewHolder>(DiffCallback()){
    inner class FileViewHolder(val fileListBinding: FileListBinding): RecyclerView.ViewHolder(fileListBinding.root){

        fun bind(data: Data) = with(fileListBinding){
            Log.d("bind","${data.name}")
            val filePath = FileUtils.getFilePath(context = context,data.name)
            val file = File(filePath)
            proDownload.visibility = View.INVISIBLE
            if(file.isFileExists()){
                btnDownload.setImageResource(R.drawable.ic_baseline_check_circle_outline)
//                icDownload.visibility=View.GONE
            }else{
                btnDownload.setImageResource(R.drawable.ic_download)
//                icDownload.visibility=View.VISIBLE
            }
            fileName.text = data.name
            if (data.name.contains(".jpg") || data.name.contains(".png")){
                image.setImageResource(R.drawable.ic_image)
            }
            else if(data.name.contains(".pdf")){
                image.setImageResource(R.drawable.ic_pdf)
            }
            else if(data.name.contains(".mp4") || data.name.contains(".MP4")){
                image.setImageResource(R.drawable.ic_video)
            }else{
                image.setImageResource(R.drawable.ic_file)
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileViewHolder {
//        return FileViewHolder(
//            LayoutInflater.from(parent.context)
//                .inflate(R.layout.file_list,parent,false)
//        )
        return FileViewHolder(FileListBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }
    override fun onBindViewHolder(holder: FileViewHolder, position: Int) {
        val data = getItem(position)
        holder.itemView.setOnClickListener {
            onClickListener.onClick(data)
        }
        holder.bind(data)
    }
    class OnClickListener(val clickListener: (data: Data) -> Unit) {
        fun onClick(data:Data) = clickListener(data)
    }
    private var unFilteredList = listOf<Data>()
    fun modifyList(list: List<Data>){
        unFilteredList = list
        submitList(list)

    }
    fun filter(query : CharSequence?){
        val list = mutableListOf<Data>()
        if(!query.isNullOrEmpty()){
            list.addAll(unFilteredList.filter {
                it.name.lowercase(Locale.getDefault()).contains(query.toString()
                    .lowercase(Locale.getDefault()))
            })
        }else{
            list.addAll(unFilteredList)
        }
        submitList(list)
    }
}
class DiffCallback: DiffUtil.ItemCallback<Data>(){
    override fun areItemsTheSame(oldItem: Data, newItem: Data): Boolean {
        return oldItem._id == newItem._id
    }

    override fun areContentsTheSame(oldItem: Data, newItem: Data): Boolean {
        return oldItem == newItem
    }
}

fun saveFilePath(body: ResponseBody?, pathWhereYouWantToSaveFile: String,onProgress:(percent:Int)->Unit): String {
    if (body == null)
        return ""
    var input: InputStream? = null
    try {
        var done: Long = 0
        val total = body.contentLength()
        input = body.byteStream()
        val fos = FileOutputStream(pathWhereYouWantToSaveFile)
        fos.use { output ->
            val buffer = ByteArray(4 * 1024) // or other buffer size
            var read: Int
            while (input.read(buffer).also { read = it } != -1) {
                done+=read
                val percent = ((done.toFloat()/total.toFloat())*100)
                onProgress(percent.toInt())
                Log.d("download", "$total $done $percent")
                output.write(buffer, 0, read)
            }
            output.flush()
        }
        return pathWhereYouWantToSaveFile
    } catch (e: Exception) {
        Timber.tag("saveFile").e(e.toString())
    } finally {
        input?.close()
    }
    return ""
}

