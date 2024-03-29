package com.example.examenesseq.fragments.principal

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import com.example.examenesseq.R
import com.example.examenesseq.databinding.FragmentBienvenidaBinding
import com.example.examenesseq.util.PreferenceHelper
import com.example.examenesseq.util.PreferenceHelper.TieneIdentidad
import com.example.examenesseq.util.PreferenceHelper.TieneSesion
import com.example.examenesseq.util.PreferenceHelper.getIdentidad

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
        if (preferences.TieneSesion()) {
                val identidad = preferences.getIdentidad()
                Log.e("identidad",identidad.toString())
                if (identidad != null) {
                    if (identidad.IdRolUsuario == 2) {
                        irAInicio()
                    } else {
                        irADashboard()
                    }
                }
        }
        actions()
        return binding.root
    }

    private fun actions() {
        binding.btnLogin.setOnClickListener {
            findNavController().navigate(R.id.action_bienvenida_to_login)
        }

        binding.btnRegistro.setOnClickListener {
            findNavController().navigate(R.id.action_bienvenida_to_registroInicio)
        }
    }

    private fun irAInicio(){
        findNavController().navigate(R.id.inicio)
    }

    private fun irADashboard(){
        findNavController().navigate(R.id.dashboard)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}