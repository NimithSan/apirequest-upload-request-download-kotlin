package com.example.apirequest

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.apirequest.adapter.FilesAdapter
import com.example.apirequest.adapter.saveFilePath
import com.example.apirequest.api.RetrofitClient
import com.example.apirequest.databinding.FragmentFileBinding
import com.example.apirequest.filemodels.Data
import com.example.apirequest.fileutils.FileUtils
import com.example.apirequest.fileutils.FileUtils.Companion.openFile
import com.example.apirequest.fileutils.isFileExists
import com.example.apirequest.viewmodel.FileViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream


class FileFragment : Fragment(){
    private val navArgs: FileFragmentArgs by navArgs()
    private var _binding : FragmentFileBinding ?= null
    private val binding get() = _binding!!
    private lateinit var myAdapter : FilesAdapter
    lateinit var data: List<Data>
    lateinit var filePath : String
    private lateinit var viewModel : FileViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentFileBinding.inflate(inflater,container,false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[FileViewModel::class.java]
        viewModel.token.value = navArgs.token
        viewModel.folderId.value = navArgs.folderId

        myAdapter = FilesAdapter(context = requireContext(),FilesAdapter.OnClickListener{
            val fileUrl: String = "file"+"/${it._id}/${it.name}"
            filePath = FileUtils.getFilePath(requireContext(),it.name)
            val file = File(filePath)
            if(file.isFileExists()){
                openFile(requireContext(),filePath)
            }else{
                RetrofitClient.instance.downloadFile(navArgs.token, fileUrl)
                    .enqueue(object : Callback<ResponseBody>{
                        override fun onResponse(
                            call: Call<ResponseBody>,
                            response: Response<ResponseBody>,
                        ) {
                            if (response.isSuccessful) {
                                lifecycleScope.launch(Dispatchers.IO){
                                    saveFilePath(response.body(),filePath, onProgress = {percent ->
                                        lifecycleScope.launch(Dispatchers.Main){
                                            try {
                                                Log.d("Progress-pp","$percent")
                                                val index = myAdapter.currentList.indexOf(it)
                                                val getChild = binding.recyclerView.getChildAt(index)
                                                val holder = binding.recyclerView.getChildViewHolder(getChild) as FilesAdapter.FileViewHolder
                                                holder.fileListBinding.proDownload.visibility=View.VISIBLE
                                                holder.fileListBinding.proDownload.progress = percent

                                            }catch (ex:Exception){
                                                ex.printStackTrace()
                                            }
                                        }


                                    })
                                    lifecycleScope.launch(Dispatchers.Main){
                                        Toast.makeText(context, "Download Success", Toast.LENGTH_SHORT).show()
                                        myAdapter.notifyDataSetChanged()
                                    }
                                }
                            } else {
                                Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show()
                            }
                        }
                        override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                            Toast.makeText(context, t.message, Toast.LENGTH_SHORT).show()
                        }
                    })
            }
        })
        binding.addFile.setOnClickListener {
            val action = FileFragmentDirections.actionFileFragmentToUploadFileFragment(folderId = navArgs.folderId, token = navArgs.token)
            findNavController().navigate(action)
        }
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider{
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.edit_folder,menu)
                val mSearchMenu = menu.findItem(R.id.actionSearch)
                val searchView = mSearchMenu.actionView as SearchView
                search(searchView)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                if(menuItem.itemId == R.id.edit_folder){
                    val action = FileFragmentDirections.actionFileFragmentToFolderFormFragment(navArgs.token, folderId = navArgs.folderId,navArgs.token)
                    findNavController().navigate(action)
                }
                return false
            }
        },viewLifecycleOwner,Lifecycle.State.RESUMED)
        viewModel.response.observe(viewLifecycleOwner) {
            myAdapter.modifyList(it)
        }
        viewModel.message.observe(viewLifecycleOwner) { it1 ->
            it1.getContentIfNotHandled()?.let {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            }
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.adapter = myAdapter
        viewModel.getFile()
        val itemTouchHelper = ItemTouchHelper(simpleCallback)
        itemTouchHelper.attachToRecyclerView(binding.recyclerView)
    }
    private val simpleCallback = object : ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.RIGHT){
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder,
        ): Boolean {
            return false
        }
        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            viewModel.position.value = viewHolder.adapterPosition
            val file= myAdapter.currentList[viewHolder.adapterPosition]
            viewModel.deleteFile(file = file)
        }
    }
    private fun search(searchView: SearchView) {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }
            override fun onQueryTextChange(newText: String): Boolean {
                (binding.recyclerView.adapter as FilesAdapter).filter(newText)
                return true
            }
        })
    }
}