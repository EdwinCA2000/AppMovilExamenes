package com.example.examenesseq.fragments.login

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.examenesseq.R
import com.example.examenesseq.databinding.FragmentLoginBinding
import com.example.examenesseq.datos.ApiServicio
import com.example.examenesseq.datos.respuesta.LoginRespuesta
import com.example.examenesseq.model.usuario.Identidad
import com.example.examenesseq.util.PreferenceHelper
import com.example.examenesseq.util.PreferenceHelper.saveIdentidad
import com.example.examenesseq.util.PreferenceHelper.setJSessionId
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class login : Fragment() {

    private val apiServicio: ApiServicio by lazy {
        ApiServicio.create(requireContext())
    }

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)

        binding.btnIniciarSesion.setOnClickListener {
            performLogin()
        }

        return binding.root
    }

    private fun irAInicio() {
        findNavController().navigate(R.id.action_login_to_inicio)
    }

    private fun irADashboard() {
        findNavController().navigate(R.id.action_login_to_dashboard)
    }

    private fun performLogin() {
        val etEmail = binding.etCorreoElectronico.text.toString()
        val etPassword = binding.etContrasena.text.toString()

        val call = apiServicio.postLogin(etEmail, etPassword)
        call.enqueue(object : Callback<LoginRespuesta> {
            override fun onResponse(call: Call<LoginRespuesta>, response: Response<LoginRespuesta>) {
                if (response.isSuccessful) {
                    val loginRespuesta = response.body()
                    if (loginRespuesta == null) {
                        Toast.makeText(requireContext(), "Se produjo un error en el servidor", Toast.LENGTH_SHORT).show()
                        return
                    }
                    if (loginRespuesta.Error == 0) {
                        val identidad = loginRespuesta.Objeto
                        val preferences = PreferenceHelper.defaultPrefs(requireContext())
                        val jsessionid = response.headers()["Set-Cookie"] ?: ""
                        Log.d("JSESSIONID", jsessionid)
                        preferences.setJSessionId(jsessionid)
                        if (etEmail.contains("@")) {
                            val identidad = Identidad(
                                IdUsuario = identidad.IdUsuario,
                                CURP = identidad.CURP,
                                CorreoElectronico = identidad.CorreoElectronico,
                                Nombres = identidad.Nombres,
                                Apellido1 = identidad.Apellido1,
                                Apellido2 = identidad.Apellido2,
                                IdPerfil = identidad.IdPerfil,
                                ActivoUsuario = identidad.ActivoUsuario
                            )
                            preferences.saveIdentidad(identidad)


                            irAInicio()
                        } else {
                            // Almacenar identidad en una variable local o en otra forma de almacenamiento si es necesario
                            irADashboard()
                        }
                    } else {
                        Toast.makeText(requireContext(), "Las credenciales son incorrectas", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(requireContext(), "Ocurrió un error en el servidor", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<LoginRespuesta>, t: Throwable) {
                Toast.makeText(requireContext(), "Hubo un error en el servidor", Toast.LENGTH_SHORT).show()
            }
        })
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
