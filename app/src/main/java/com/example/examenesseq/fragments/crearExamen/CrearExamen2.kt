package com.example.examenesseq.fragments.crearExamen

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.examenesseq.databinding.FragmentCrearExamen2Binding
import com.example.examenesseq.datos.ApiServicio
import com.example.examenesseq.model.crearExamen.ExamenResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CrearExamen2 : Fragment() {

    private var _binding: FragmentCrearExamen2Binding? = null
    private val binding get() = _binding!!

    private lateinit var crearExamenViewModel: CrearExamenViewModel

    private val apiServicio: ApiServicio by lazy {
        ApiServicio.create(requireContext())
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentCrearExamen2Binding.inflate(inflater, container, false)
        crearExamenViewModel = ViewModelProvider(requireActivity())[CrearExamenViewModel::class.java]
        crearExamen()
        return binding.root
    }

    private fun validarInputs(): Boolean{
        val etDescripcionExamen=binding.etDescripcionExamen.text.toString()
        val etBienvenidaExamen=binding.etBienvenidaExamen.text.toString()

        if (etDescripcionExamen.isEmpty() || etBienvenidaExamen.isEmpty()) {
            Toast.makeText(requireContext(), "Por favor complete todos los campos", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    private fun stringToHex(input: String): String {
        val stringBuilder = StringBuilder()
        for (char in input.toCharArray()) {
            val hex = String.format("%02X", char.code)
            stringBuilder.append(hex)
        }
        return stringBuilder.toString()
    }

    private fun crearExamen(){
        binding.btnCrearExamen.setOnClickListener{
            if (validarInputs()){
                apiCrearExamen()
            }
        }
    }

    fun obtenerFechaActual(): String {
        val formato = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val fechaActual = Date()
        return formato.format(fechaActual)
    }

    private fun apiCrearExamen(){
        val idExamen=-1
        val tituloExamen=crearExamenViewModel.tituloExamen
        val fechaInicio=crearExamenViewModel.fechaInicio
        val fechaFinal= crearExamenViewModel.fechaFinal
        val fechaCreacion= obtenerFechaActual()
        val duracionExamen=crearExamenViewModel.duracionExamen
        val estadoExamen=crearExamenViewModel.estadoExamen

        val etDescripcionExamen=binding.etDescripcionExamen.text.toString()
        val etBienvenidaExamen=binding.etBienvenidaExamen.text.toString()
        // Convertir el texto a hexadecimal
        val descripcionExamenHex = stringToHex(etDescripcionExamen)
        val bienvenidaExamenHex = stringToHex(etBienvenidaExamen)

        val call=apiServicio.crearExamen(idExamen,tituloExamen,estadoExamen,fechaCreacion,fechaInicio,fechaFinal,
            duracionExamen,descripcionExamenHex,bienvenidaExamenHex)
        call.enqueue(object : Callback<ExamenResponse>{
            override fun onResponse(
                call: Call<ExamenResponse>,
                response: Response<ExamenResponse>
            ) {
                if (response.isSuccessful){
                    Toast.makeText(requireContext(), "Has creado el examen exitosamente", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ExamenResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "Fallo: ${t.message}", Toast.LENGTH_SHORT).show()
                Log.e("API Failure", "Error: ${t.message}", t)
            }

        })

    }


}