package com.example.examenesseq.fragments.registro

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.examenesseq.databinding.FragmentInfoPersonalBinding


class InfoPersonal : Fragment() {
    private var _binding: FragmentInfoPersonalBinding? = null
    private val binding get() = _binding!!
    private lateinit var registroViewModel: RegistroViewModel



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInfoPersonalBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        registroViewModel = ViewModelProvider(requireActivity())[RegistroViewModel::class.java]
        binding.btnSiguiente.setOnClickListener{
            if (validarCampos()) {

                registroViewModel.nombres=binding.etNombresRegistro.text.toString()
                registroViewModel.apellido1=binding.etApellidoPaterno.text.toString()
                registroViewModel.apellido2=binding.etApellidoMaterno.text.toString()
                registroViewModel.curp=binding.etCurp.text.toString()
                // Cambiar a la segunda pestaña
                Log.e("nombres",registroViewModel.nombres)
                val registroInicioFragment = parentFragment as RegistroInicio
                registroInicioFragment.changeToSecondTab()
            }
        }

    }

    private fun validarCampos(): Boolean {
        val etNombres = binding.etNombresRegistro.text.toString()
        val etApellido1 = binding.etApellidoPaterno.text.toString()
        val etApellido2 = binding.etApellidoMaterno.text.toString()
        val etCURP = binding.etCurp.text.toString()

        if (etCURP.length != 18) {
            Toast.makeText(requireContext(), "La CURP no es válida", Toast.LENGTH_SHORT).show()
            return false
        }

        // Validar que los campos no estén vacíos
        if (etNombres.isEmpty() || etApellido1.isEmpty() || etApellido2.isEmpty()  || etCURP.isEmpty()) {
            Toast.makeText(requireContext(), "Por favor complete todos los campos", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
