package com.example.apirequest

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.apirequest.api.RetrofitClient
import com.example.apirequest.application.MyApplication
import com.example.apirequest.databinding.FragmentFolderFormBinding
import com.example.apirequest.models.FolderData
import com.example.apirequest.models.FolderResponse
import com.example.apirequest.viewmodel.FolderViewModel
import com.example.apirequest.viewmodel.FolderViewModelFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.UUID

class FolderFormFragment : Fragment() {
    private val viewModel: FolderViewModel by activityViewModels {
        FolderViewModelFactory(
            (activity?.application as MyApplication).database.folderDao()
        )
    }
    private lateinit var folderData: FolderData
    private val navArgs : FolderFormFragmentArgs by navArgs()
    private var _binding : FragmentFolderFormBinding ?= null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentFolderFormBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val _id = navArgs.folderId
        if(_id.isNotBlank()){
            viewModel.retrieveFolder(_id).observe(this.viewLifecycleOwner){
                    selectedFolder -> folderData = selectedFolder
                bind(folderData)
            }
        }
        binding.saveAction.setOnClickListener {
            val name = binding.folderNameEt.text.toString().trim()
            val des = binding.folderDesEt.text.toString().trim()
            val id = UUID.randomUUID().toString()
            if(name.isNotBlank() && des.isNotBlank() && _id.isBlank()){
                viewModel.message.observe(viewLifecycleOwner) { it1 ->
                    it1.getContentIfNotHandled()?.let {
                        Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                    }
                }
                    viewModel.token.value = navArgs.token
                    viewModel.id.value = id
                    viewModel.name.value = name
                    viewModel.des.value = des
                    viewModel.createFolder(onSuccess ={
                        findNavController().navigateUp()
                    })
            }else{
                RetrofitClient.instance.updateFolder(navArgs.token1,_id,name,des)
                    .enqueue(object : Callback<FolderData>{
                        override fun onResponse(
                            call: Call<FolderData>,
                            response: Response<FolderData>,
                        ) {
                            if (response.isSuccessful){
                                viewModel.updateFolder(response.body()!!)
                                Toast.makeText(context,"Successful", Toast.LENGTH_SHORT).show()
                                val action = FolderFormFragmentDirections.actionFolderFormFragmentToFileFragment(folderName = name,_id,navArgs.token1)
                                findNavController().navigate(action)
                            }else{
                                Toast.makeText(context,"Failure", Toast.LENGTH_SHORT).show()
                            }
                        }
                        override fun onFailure(call: Call<FolderData>, t: Throwable) {
                            Toast.makeText(context,t.message,Toast.LENGTH_SHORT).show()
                        }
                    })
            }
        }
    }
    private fun bind(folderData: FolderData){
        binding.apply {
            folderNameEt.setText(folderData.name,TextView.BufferType.SPANNABLE)
            folderDesEt.setText(folderData.description,TextView.BufferType.SPANNABLE)
        }
    }

}