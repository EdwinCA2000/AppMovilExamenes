package com.example.examenesseq.fragments.dashboard.usuarios

import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.examenesseq.R
import com.example.examenesseq.databinding.FragmentAgregarUsuariosBinding
import com.example.examenesseq.datos.ApiServicio
import com.example.examenesseq.model.usuario.ModuloUsuario
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.regex.Pattern

class AgregarUsuarios : Fragment() {

    private var _binding: FragmentAgregarUsuariosBinding? = null
    private val binding get() = _binding!!
    private val apiServicio: ApiServicio by lazy {
        ApiServicio.create(requireContext())
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentAgregarUsuariosBinding.inflate(inflater, container, false)
        binding.btnCrearUsuario.setOnClickListener{
            performRegistro()
        }
        return binding.root
    }

    private fun validarCampos(): Boolean {
        val etNombres = binding.etNombreAlumno.text.toString()
        val etApellido1 = binding.etApellido1.text.toString()
        val etApellido2 = binding.etApellido2.text.toString()
        val etCURP = binding.etCURP.text.toString()
        val etCorreoElectronico = binding.etCorreoElectronico.text.toString()
        val etContrasena = binding.etPassword.text.toString()
        val etConfirmarPassword = binding.etPasswordConfirmar.text.toString()

        if (etContrasena != etConfirmarPassword) {
            Toast.makeText(requireContext(), "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
            return false
        }

        if (etCorreoElectronico.isEmpty() || etContrasena.isEmpty()) {
            Toast.makeText(requireContext(), "Por favor complete todos los campos", Toast.LENGTH_SHORT).show()
            return false
        }

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

    private fun validarCorreo(email: String): Boolean {
        val pattern: Pattern = Patterns.EMAIL_ADDRESS
        return pattern.matcher(email).matches()
    }


    private fun performRegistro(){
        val nombres = binding.etNombreAlumno.text.toString()
        val apellido1 = binding.etApellido1.text.toString()
        val apellido2= binding.etApellido2.text.toString()
        val curp= binding.etCURP.text.toString()
        val etCorreoElectronico=binding.etCorreoElectronico.text.toString()
        val etContrasena=binding.etPassword.text.toString()

        if(!validarCorreo(etCorreoElectronico)){
            Toast.makeText(requireContext(), "Por favor, ingresa un correo electrónico válido", Toast.LENGTH_SHORT).show()
            return
        }else if (!validarCampos()) {
            return
        }else{
            val call =apiServicio.agregarUsuario(curp,etContrasena,etCorreoElectronico,nombres,apellido1,apellido2)
            call.enqueue(object: Callback<ModuloUsuario> {
                override fun onResponse(
                    call: Call<ModuloUsuario>,
                    response: Response<ModuloUsuario>
                ) {
                    if(response.isSuccessful){
                        findNavController().navigate(R.id.action_agregarUsuarios_to_administrar_usuarios)
                        Toast.makeText(requireContext(), "Se ha creado el usuario exitosamente", Toast.LENGTH_SHORT).show()
                    }else{
                        Toast.makeText(requireContext(), "Ocurrio un error en el servidor", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ModuloUsuario>, t: Throwable) {
                    Toast.makeText(requireContext(), "Hubo un error en el servidor", Toast.LENGTH_SHORT).show()
                }

            })
        }
    }
}