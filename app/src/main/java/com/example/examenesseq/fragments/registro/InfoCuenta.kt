package com.example.examenesseq.fragments.registro

import android.os.Bundle
import android.util.Log
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.examenesseq.R
import com.example.examenesseq.databinding.FragmentInfoCuentaBinding
import com.example.examenesseq.datos.ApiServicio
import com.example.examenesseq.datos.respuesta.LoginRespuesta
import com.example.examenesseq.util.PreferenceHelper
import com.example.examenesseq.util.PreferenceHelper.saveIdentidad
import com.example.examenesseq.util.PreferenceHelper.setJSessionId
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.regex.Pattern


class InfoCuenta : Fragment() {

    private val apiServicio: ApiServicio by lazy {
        ApiServicio.create(requireContext())
    }
    private var _binding: FragmentInfoCuentaBinding? = null
    private val binding get() = _binding!!

    private lateinit var registroViewModel: RegistroViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInfoCuentaBinding.inflate(inflater, container, false)
        val registroInicioFragment = parentFragment as RegistroInicio
        registroInicioFragment.isSecondTabEnabled=true
        registroInicioFragment.updateTabState()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        registroViewModel = ViewModelProvider(requireActivity())[RegistroViewModel::class.java]

        binding.btnRegistrarse.setOnClickListener{
            performRegistro()
        }
    }

    private fun validarRegistro(): Boolean {
        val etCorreoElectronico = binding.etCorreoElectronicoRegistro.text.toString()
        val etContrasena = binding.etContrasenaRegistro.text.toString()
        val etConfirmarPassword = binding.etConfirmarContrasena.text.toString()

        if (etContrasena != etConfirmarPassword) {
            Toast.makeText(requireContext(), "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
            return false
        }

        if (etCorreoElectronico.isEmpty() || etContrasena.isEmpty()) {
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
        val idUsuario=0
        val nombres = registroViewModel.nombres
        val apellido1 = registroViewModel.apellido1
        val apellido2= registroViewModel.apellido2
        val curp= registroViewModel.curp
        val etCorreoElectronico=binding.etCorreoElectronicoRegistro.text.toString()
        val etContrasena=binding.etContrasenaRegistro.text.toString()

        if(!validarCorreo(etCorreoElectronico)){
            Toast.makeText(requireContext(), "Por favor, ingresa un correo electrónico válido", Toast.LENGTH_SHORT).show()
            return
        }else if (!validarRegistro()) {
            return
        }else{
            val call =apiServicio.postRegistro(idUsuario,curp,etContrasena,etCorreoElectronico,nombres,apellido1,apellido2)
            call.enqueue(object: Callback<LoginRespuesta> {
                override fun onResponse(
                    call: Call<LoginRespuesta>,
                    response: Response<LoginRespuesta>
                ) {
                    if(response.isSuccessful){
                        val loginRespuesta=response.body()
                        if(loginRespuesta==null){
                            Toast.makeText(requireContext(), "Se produjo un error en el servidor", Toast.LENGTH_SHORT).show()
                            return
                        }else{
                            val preferences = PreferenceHelper.defaultPrefs(requireContext())
                            val identidad = loginRespuesta.Objeto
                            val jsessionid = response.headers()["Set-Cookie"] ?: ""
                            Log.d("JSESSIONID", jsessionid)
                            preferences.setJSessionId(jsessionid)
                            preferences.saveIdentidad(identidad)
                            irAInicio()
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
    }

    private fun irAInicio(){
        findNavController().navigate(R.id.inicio)
    }
}