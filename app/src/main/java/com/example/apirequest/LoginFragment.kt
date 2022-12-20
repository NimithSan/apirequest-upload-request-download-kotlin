package com.example.apirequest

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.apirequest.application.MyApplication
import com.example.apirequest.databinding.FragmentLoginBinding
import com.example.apirequest.sharedPref.Pref
import com.example.apirequest.viewmodel.FolderViewModel
import com.example.apirequest.viewmodel.FolderViewModelFactory


class LoginFragment : Fragment() {
    private val viewModel: FolderViewModel by activityViewModels {
        FolderViewModelFactory(
            (activity?.application as MyApplication).database.folderDao()
        )
    }
    private var _binding: FragmentLoginBinding ?= null
    private val binding get() = _binding!!
    private var email: String ?= null
    private var password : String ?= null
    private var token : String ?= null
    lateinit var pref: Pref
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater,container,false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pref = Pref(requireContext())
        binding.registerBtn.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }
        binding.loginBtn.setOnClickListener {
            viewModel.deleteAll()
            email    = binding.gmailEt.text.toString()
            password = binding.passwordEt.text.toString()

            if(email?.isEmpty()!!){
                binding.gmailEt.error = "Gmail Require"
                binding.gmailEt.requestFocus()
            }
            if(password?.isEmpty()!!){
                binding.passwordEt.error = "Password Require"
                binding.passwordEt.requestFocus()
            }
            if(binding.gmailEt.text.toString().isNotBlank() && binding.passwordEt.text.toString().isNotBlank()){
                viewModel.email.value = binding.gmailEt.text.toString()
                viewModel.pass.value = binding.passwordEt.text.toString()
                viewModel.message.observe(viewLifecycleOwner) { it1 ->
                    it1.getContentIfNotHandled()?.let {
                        Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                    }
                }
                viewModel.login(onSuccess = {
                    token = viewModel.token.value.toString()
                    val action = LoginFragmentDirections.actionLoginFragmentToFolderFragment(viewModel.token.value.toString())
                    findNavController().navigate(action)
                    pref.editor.apply {
                        putString("token",viewModel.token.value.toString())
                        apply()
                    }
                })
            }
        }
        val token = pref.sharedPref.getString("token","")
        if (token!!.length > 5){
            val action = LoginFragmentDirections.actionLoginFragmentToFolderFragment(token)
            findNavController().navigate(action)
        }
    }
}
//            viewModel.test(onSuccess = {
//                findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
//            }, onError = {
//
//            })
//            viewModel.getToViewModel(listener = object:LoginListener{
//                override fun token(): String {
//                    return
//                }
//            })
