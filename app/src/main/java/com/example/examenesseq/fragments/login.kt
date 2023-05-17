package com.example.examenesseq.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.examenesseq.R
import com.example.examenesseq.databinding.FragmentLoginBinding
import com.example.examenesseq.datos.ApiServicio
import com.example.examenesseq.datos.respuesta.LoginRespuesta
import com.example.examenesseq.util.PreferenceHelper
import com.example.examenesseq.util.PreferenceHelper.get
import com.example.examenesseq.util.PreferenceHelper.set
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class login : Fragment() {

    private val sharedPreferences by lazy { PreferenceHelper.defaultPrefs(requireContext()) }

    private val apiServicio: ApiServicio by lazy {
        ApiServicio.create()
    }

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentLoginBinding.inflate(inflater, container, false)

        if(sharedPreferences["Mensaje",""].contains(".")){
            irAInicio()
        }

        // Supongamos que "nombreUsuario" es una variable que contiene el nombre del usuario.
        val sharedPreferences = requireActivity().getSharedPreferences("my_prefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("nombre_usuario", binding.etCorreoElectronico.text.toString())
        editor.apply()

        binding.btnIniciarSesion.setOnClickListener(){
            performLogin()
        }

        return binding.root
    }

    private fun irAInicio(){
            findNavController().navigate(R.id.action_login_to_inicio)
    }

    private fun createSessionPreference(Mensaje: String){
        sharedPreferences["Mensaje"]=Mensaje
    }

    private fun performLogin(){
        val etEmail=binding.etCorreoElectronico.text.toString()
        val etPassword= binding.etContrasena.text.toString()

        val call =apiServicio.postLogin(etEmail,etPassword)
        call.enqueue(object: Callback<LoginRespuesta>{
            override fun onResponse(
                call: Call<LoginRespuesta>,
                response: Response<LoginRespuesta>
            ) {
                if(response.isSuccessful){
                    val loginRespuesta=response.body()
                    if(loginRespuesta==null){
                        Toast.makeText(requireContext(), "Se produjo un error en el servidor", Toast.LENGTH_SHORT).show()
                        return
                    }
                    if (loginRespuesta.Error==0){
                        createSessionPreference(loginRespuesta.Mensaje)
                        irAInicio()
                    }else{
                        Toast.makeText(requireContext(), "Las credenciales son incorrectas", Toast.LENGTH_SHORT).show()
                    }
                }else{
                    Toast.makeText(requireContext(), "Ocurrio un error en el servidor", Toast.LENGTH_SHORT).show()
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