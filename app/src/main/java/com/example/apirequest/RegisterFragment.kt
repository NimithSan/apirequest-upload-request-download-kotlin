package com.example.apirequest

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.apirequest.databinding.FragmentRegisterBinding
import com.example.apirequest.viewmodel.RegisterViewModel


class RegisterFragment : Fragment() {
    private var _binding : FragmentRegisterBinding ?= null
    private val binding get() = _binding!!
    private lateinit var viewModel: RegisterViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[RegisterViewModel::class.java]
        binding.registerBtn.setOnClickListener {
            val firstname = binding.firstnameEt.text.toString().trim()
            val lastname = binding.lastnameEt.text.toString().trim()
            val email = binding.gmailEt.text.toString().trim()
            val password = binding.passwordEt.text.toString().trim()

            if(firstname.isEmpty()){
                binding.firstnameEt.error = "FirstName Require"
                binding.firstnameEt.requestFocus()
            }
            if(lastname.isEmpty()){
                binding.lastnameEt.error = "LastName Require"
                binding.lastnameEt.requestFocus()
            }
            if(email.isEmpty()){
                binding.gmailEt.error = "Gmail Require"
                binding.gmailEt.requestFocus()
            }
            if(password.isEmpty()){
                binding.passwordEt.error = "Password Require"
                binding.passwordEt.requestFocus()
            }
            viewModel.message.observe(viewLifecycleOwner) { it1 ->
                it1.getContentIfNotHandled()?.let {
                    Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                }
            }
            if(firstname.isNotBlank() && lastname.isNotBlank() && email.isNotBlank() && password.isNotBlank()){
                viewModel.firstname.value = firstname
                viewModel.lastname.value = lastname
                viewModel.email.value = email
                viewModel.password.value = password
                viewModel.register(onSuccess = {
                    findNavController().navigateUp()
                })
            }
        }
    }
}