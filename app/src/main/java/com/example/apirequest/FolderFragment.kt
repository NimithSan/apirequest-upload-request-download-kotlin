package com.example.apirequest

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.example.apirequest.adapter.FolderAdapter
import com.example.apirequest.application.MyApplication
import com.example.apirequest.databinding.FragmentFolderBinding
import com.example.apirequest.sharedPref.Pref
import com.example.apirequest.viewmodel.FolderViewModel
import com.example.apirequest.viewmodel.FolderViewModelFactory
import com.google.android.material.dialog.MaterialAlertDialogBuilder


class FolderFragment : Fragment() {
    private val viewModel: FolderViewModel by activityViewModels {
        FolderViewModelFactory(
            (activity?.application as MyApplication).database.folderDao()
        )
    }
    private val navArgs: FolderFragmentArgs by navArgs()
    private var _binding: FragmentFolderBinding? = null
    private val binding get() = _binding!!
    lateinit var pref: Pref
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentFolderBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pref = Pref(requireContext())
        //menu
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.logout_menu, menu)
                val mSearchMenu = menu.findItem(R.id.actionSearch)
                val searchView = mSearchMenu.actionView as SearchView
                search(searchView)
            }
            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                if (menuItem.itemId == R.id.logout_menu) {
                    showConfirmationDialog()
                }
                return false
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
        //Adapter
        val adapter = FolderAdapter{
            val action = FolderFragmentDirections.actionFolderFragmentToFileFragment(it.name,
                it._id,
                navArgs.token)
            findNavController().navigate(action)
        }
        viewModel.allFolder.observe(this.viewLifecycleOwner) { folder ->
            folder.let {
                adapter.modifyList(it)
            }
        }
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = GridLayoutManager(this.context, 3)
        binding.addAction.setOnClickListener {
            val action =
                FolderFragmentDirections.actionFolderFragmentToFolderFormFragment(navArgs.token,
                    "",
                    navArgs.token)
            findNavController().navigate(action)
        }
        viewModel.retrieveFolders()
    }

    private fun showConfirmationDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("")
            .setMessage("តើអ្នកចង់ចាកចេញមែន ? ")
            .setCancelable(false)
            .setNegativeButton("អត់ទេ") { _, _ -> }
            .setPositiveButton("បាទ/ចាស") { _, _ ->
                pref.editor.putString("token", "").apply()
                val action = FolderFragmentDirections.actionFolderFragmentToLoginFragment("")
                findNavController().navigate(action)
            }
            .show()
    }

    private fun search(searchView: SearchView) {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }
            override fun onQueryTextChange(newText: String): Boolean {
                (binding.recyclerView.adapter as FolderAdapter).filter(newText)
                return true
            }
        })
    }
}