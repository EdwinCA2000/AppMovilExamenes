package com.example.examenesseq.fragments.principal

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import com.example.examenesseq.R
import com.example.examenesseq.databinding.FragmentBienvenidaBinding
import com.example.examenesseq.util.PreferenceHelper
import com.example.examenesseq.util.PreferenceHelper.TieneSesion

class Bienvenida : Fragment() {

    private var _binding: FragmentBienvenidaBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentBienvenidaBinding.inflate(inflater, container, false)
        (activity as AppCompatActivity).supportActionBar?.hide()
        val preferences = PreferenceHelper.defaultPrefs(requireContext())
        if(preferences.TieneSesion()){
            irAInicio()
        }
        actions()
        return binding.root
    }

    private fun actions() {
        binding.btnLogin.setOnClickListener {
            findNavController().navigate(R.id.action_bienvenida_to_login)
        }

        binding.btnRegistro.setOnClickListener {
            findNavController().navigate(R.id.action_bienvenida_to_registro)
        }
    }

    private fun irAInicio(){
        findNavController().navigate(R.id.inicio)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}