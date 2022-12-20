package com.example.apirequest

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.apirequest.api.ReqBodyWithProgress
import com.example.apirequest.databinding.FragmentUploadFileBinding
import com.example.apirequest.fileutils.FileUtils
import com.example.apirequest.viewmodel.UploadViewModel
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class UploadFileFragment : Fragment() {
    private val navArgs : UploadFileFragmentArgs by navArgs()
    private var pathFile : String? = null
    private var _binding : FragmentUploadFileBinding ?= null
    private val binding get() = _binding!!
    private lateinit var viewModel: UploadViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View{
        _binding = FragmentUploadFileBinding.inflate(inflater,container,false)
        return binding.root
    }
    private val getContentForDoc =
        registerForActivityResult(ActivityResultContracts.GetContent()){ file ->
            file?.let { fileUri ->
                lifecycleScope.launch {
                    val filePath = FileUtils.getFileFromUri(requireActivity().baseContext, fileUri)
                    pathFile = filePath.toString()
                }
            }
        }
    private val requestReadExternalStoragePermissionForDoc =
        registerForActivityResult(ActivityResultContracts.RequestPermission()){
            if(it){
                getContentForDoc.launch("*/*")
            }
        }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[UploadViewModel::class.java]
        binding.btnUploadFile.setOnClickListener {
            requestReadExternalStoragePermissionForDoc.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        binding.upload.setOnClickListener {
            try {
                val file = File(pathFile.toString())
                val bodyBuilder = MultipartBody.Builder().setType(MultipartBody.FORM)
                bodyBuilder.addFormDataPart("folderId",navArgs.folderId)
                bodyBuilder.addFormDataPart("file",file.name,file.asRequestBody())
                val progressBody= ReqBodyWithProgress(bodyBuilder.build(), onUploadProgress = {
                    binding.progressBar.progress = it
                })
                viewModel.token.value = navArgs.token
                viewModel.progressBody.value = progressBody
                viewModel.message.observe(viewLifecycleOwner) { it1 ->
                    it1.getContentIfNotHandled()?.let {
                        Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                    }
                }
                viewModel.upload(onSuccess = {
                findNavController().navigateUp()
                })
            }catch (ex:Exception){
                ex.printStackTrace()
            }
        }
    }
}
