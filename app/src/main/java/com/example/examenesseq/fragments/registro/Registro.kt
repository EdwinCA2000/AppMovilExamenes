package com.example.examenesseq.fragments.registro

import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.examenesseq.R
import com.example.examenesseq.databinding.FragmentRegistroBinding
import com.example.examenesseq.datos.ApiServicio
import com.example.examenesseq.datos.respuesta.LoginRespuesta
import com.example.examenesseq.util.PreferenceHelper
import com.example.examenesseq.util.PreferenceHelper.set
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.regex.Pattern

class registro : Fragment() {

    private val sharedPreferences by lazy { PreferenceHelper.defaultPrefs(requireContext()) }

    private val apiServicio: ApiServicio by lazy {
        ApiServicio.create(requireContext())
    }

    private var _binding: FragmentRegistroBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentRegistroBinding.inflate(inflater, container, false)

        binding.btnRegistrarse.setOnClickListener {
            performRegistro()
        }
        return binding.root
    }

    private fun irAInicio(){
        findNavController().navigate(R.id.action_registro_to_inicio)
    }

    private fun createSessionPreference(Mensaje: String){
        sharedPreferences["Mensaje"]=Mensaje
    }
    private fun validarRegistro(): Boolean {
        val etNombres = binding.etNombresRegistro.text.toString()
        val etApellido1 = binding.etApellidoPaterno.text.toString()
        val etApellido2 = binding.etApellidoMaterno.text.toString()
        val etCorreoElectronico = binding.etCorreoElectronicoRegistro.text.toString()
        val etCURP = binding.etCurp.text.toString()
        val etContrasena = binding.etContrasenaRegistro.text.toString()
        val etConfirmarPassword = binding.etConfirmarContrasena.text.toString()

        // Validar que la contraseña y su confirmación sean iguales
        if (etContrasena != etConfirmarPassword) {
            Toast.makeText(requireContext(), "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
            return false
        }


        // Validar que la CURP tenga una longitud de 18 caracteres
        if (etCURP.length != 18) {
            Toast.makeText(requireContext(), "La CURP no es válida", Toast.LENGTH_SHORT).show()
            return false
        }

        // Validar que los campos no estén vacíos
        if (etNombres.isEmpty() || etApellido1.isEmpty() || etApellido2.isEmpty() || etCorreoElectronico.isEmpty() || etCURP.isEmpty() || etContrasena.isEmpty()) {
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
        val etNombres=binding.etNombresRegistro.text.toString()
        val etApellido1=binding.etApellidoPaterno.text.toString()
        val etApellido2=binding.etApellidoMaterno.text.toString()
        val etCorreoElectronico=binding.etCorreoElectronicoRegistro.text.toString()
        val etCURP= binding.etCurp.text.toString()
        val etContrasena=binding.etContrasenaRegistro.text.toString()

        if(!validarCorreo(etCorreoElectronico)){
            Toast.makeText(requireContext(), "Por favor, ingresa un correo electrónico válido", Toast.LENGTH_SHORT).show()
            return
        }else if (!validarRegistro()) {
            return
        }else{
            val call =apiServicio.postRegistro(idUsuario,etCURP,etContrasena,etCorreoElectronico,etNombres,etApellido1,etApellido2)
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
                            createSessionPreference(loginRespuesta.Mensaje)
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}