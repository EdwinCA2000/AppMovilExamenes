package com.example.examenesseq.fragments.inicio.perfil

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.edit
import androidx.navigation.fragment.findNavController
import com.example.examenesseq.R
import com.example.examenesseq.databinding.FragmentPerfilUsuarioBinding
import com.example.examenesseq.datos.ApiServicio
import com.example.examenesseq.util.PreferenceHelper
import com.example.examenesseq.util.PreferenceHelper.getIdentidad
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class PerfilUsuario : Fragment() {

    private var _binding: FragmentPerfilUsuarioBinding? = null
    private val binding get() = _binding!!

    private val apiServicio: ApiServicio by lazy {
        ApiServicio.create(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentPerfilUsuarioBinding.inflate(inflater, container, false)

        val preferences = PreferenceHelper.defaultPrefs(requireContext())
        //correo
        val correoElectronico=preferences.getIdentidad()?.CorreoElectronico
        //CURP
        val curp=preferences.getIdentidad()?.CURP
        //nombre
        val nombreUser= preferences.getIdentidad()?.Nombres
        val apellido1User= preferences.getIdentidad()?.Apellido1
        val apellido2User= preferences.getIdentidad()?.Apellido2
        val nombreCompleto= nombreUser + " " + apellido1User + " " + apellido2User

        binding.txtnombreAlumno.text=nombreCompleto
        binding.txtCorreoElectronico.text=correoElectronico
        binding.txtCurp.text=curp

        binding.perfilCerrarSesion.setOnClickListener {
            mostrarDialogoCerrarSesion()
        }
        return binding.root
    }

    private fun mostrarDialogoCerrarSesion() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Confirmar Cerrar sesión")
            .setMessage("¿Desea cerrar sesión?")
            .setPositiveButton("Si") { dialog, _ ->
                cerrarSesionUser()
                dialog.dismiss()
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    fun cerrarSesionUser(){
        apiServicio.cerrarSesion().enqueue(object : Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                if (response.isSuccessful) {
                    val preferences = PreferenceHelper.defaultPrefs(requireContext())
                    preferences.edit {
                        remove("JSESSIONID")
                        apply()
                    }
                    irALogin()
                } else {
                    Toast.makeText(requireContext(), "No se pudo cerrar sesión", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Unit>, t: Throwable) {
                Toast.makeText(requireContext(), "Ocurrio un error al cerrar sesión en el servidor", Toast.LENGTH_SHORT).show()
            }
        })
    }
    fun irALogin() {
        val navController = findNavController()
        // Navega al fragmento de bienvenida
        navController.navigate(R.id.action_perfil_usuario_to_login)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}