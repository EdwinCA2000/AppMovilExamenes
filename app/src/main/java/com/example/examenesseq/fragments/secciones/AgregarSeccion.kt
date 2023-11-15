package com.example.examenesseq.fragments.secciones

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.examenesseq.R
import com.example.examenesseq.databinding.FragmentAgregarSeccionBinding
import com.example.examenesseq.datos.ApiServicio
import com.example.examenesseq.model.SeccionResponse
import com.example.examenesseq.fragments.secciones.adapter.EstadoSeccionAdapter
import com.example.examenesseq.fragments.secciones.data.EstadoSeccionData
import com.example.examenesseq.fragments.secciones.viewmodel.IdExamenViewModel
import org.jsoup.Jsoup
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class AgregarSeccion : Fragment() {

    private var _binding: FragmentAgregarSeccionBinding? = null
    private val binding get() = _binding!!

    private val apiServicio: ApiServicio by lazy {
        ApiServicio.create(requireContext())
    }
    private lateinit var idExamenViewModel: IdExamenViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentAgregarSeccionBinding.inflate(inflater, container, false)
        idExamenViewModel = ViewModelProvider(requireActivity())[IdExamenViewModel::class.java]
        Log.e("idExamen",idExamenViewModel.idExamen.toString())

        val spinnerEstadoSeccion = binding.spinnerEstadoSeccionAgregar
        val estadoSeccionOpciones = EstadoSeccionData.estadoSeccionOpciones
        val estadoSeccionAdapter = EstadoSeccionAdapter(requireContext(), estadoSeccionOpciones)
        spinnerEstadoSeccion.adapter = estadoSeccionAdapter
        agregarSeccion()
        return binding.root
    }

    private fun agregarSeccion(){
        binding.btnAgregarSeccion.setOnClickListener{
            if (validarInputs()){
                ApiAgregarSeccion()
            }
        }
    }

    private fun ApiAgregarSeccion(){
        val idSeccion=-1
        val idExamen=idExamenViewModel.idExamen
        val etTituloSeccion = binding.etTituloSeccionAgregar.text.toString()
        val etOrden = binding.etOrdenSeccionAgregar.text.toString()


        val spinnerEstadoSeccion = binding.spinnerEstadoSeccionAgregar

        // Obtener el índice seleccionado en los spinners
        val seccionIndex = spinnerEstadoSeccion.selectedItemPosition

        val estadoSeccionSeleccionado = EstadoSeccionData.estadoSeccionOpciones[seccionIndex]
        val estadoSeccionValor = estadoSeccionSeleccionado.estadoSeccion

        val estadoSeccion=estadoSeccionValor
        val etDescripcionSeccion=stringToHex(binding.etDescripcionSeccionAgregar.text.toString())

        val call=apiServicio.guardarEditarSecciones(idSeccion,etTituloSeccion,estadoSeccion,idExamen,etOrden.toInt(),etDescripcionSeccion)
        call.enqueue(object : Callback<SeccionResponse> {
            override fun onResponse(
                call: Call<SeccionResponse>,
                response: Response<SeccionResponse>
            ) {
                if (response.isSuccessful){
                    Toast.makeText(requireContext(), "Has agregado la sección ${etTituloSeccion} exitosamente", Toast.LENGTH_SHORT).show()
                    irAdministrarSecciones()
                }
            }

            override fun onFailure(call: Call<SeccionResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "Fallo: ${t.message}", Toast.LENGTH_SHORT).show()
                Log.e("API Failure", "Error: ${t.message}", t)
            }

        })

    }

    private fun validarInputs(): Boolean {
        val etTituloSeccion = binding.etTituloSeccionAgregar.text.toString()
        val etOrden = binding.etOrdenSeccionAgregar.text.toString()


        val spinnerEstadoSeccion = binding.spinnerEstadoSeccionAgregar

        // Obtener el índice seleccionado en los spinners
        val seccionIndex = spinnerEstadoSeccion.selectedItemPosition

        val etDescripcionSeccion=binding.etDescripcionSeccionAgregar.text.toString()

        if (etTituloSeccion.isEmpty() || etOrden.isEmpty() || seccionIndex == 0 || etDescripcionSeccion.isEmpty()) {
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

    fun eliminarEtiquetasHTML(textoHTML: String): String {
        val documento = Jsoup.parse(textoHTML)
        return documento.text()
    }

    private fun irAdministrarSecciones(){
        findNavController().navigate(R.id.action_agregarSeccion_to_administrarSecciones)
    }


}