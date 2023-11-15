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
import com.example.examenesseq.databinding.FragmentEditarSeccionBinding
import com.example.examenesseq.datos.ApiServicio
import com.example.examenesseq.model.SeccionResponse
import com.example.examenesseq.model.examen.Secciones
import com.example.examenesseq.fragments.secciones.adapter.EstadoSeccionAdapter
import com.example.examenesseq.fragments.secciones.data.EstadoSeccionData
import com.example.examenesseq.fragments.secciones.viewmodel.SeccionesViewModel
import org.jsoup.Jsoup
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditarSeccion : Fragment() {

    private val apiServicio: ApiServicio by lazy {
        ApiServicio.create(requireContext())
    }
    private var _binding: FragmentEditarSeccionBinding? = null
    private val binding get() = _binding!!
    private lateinit var seccionesViewModel: SeccionesViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentEditarSeccionBinding.inflate(inflater, container, false)
        seccionesViewModel = ViewModelProvider(requireActivity())[SeccionesViewModel::class.java]
        val secciones=seccionesViewModel.selectedSeccionData

        val spinnerEstadoSeccion = binding.spinnerEstadoSeccionEditar
        val estadoSeccionOpciones = EstadoSeccionData.estadoSeccionOpciones
        val estadoSeccionAdapter = EstadoSeccionAdapter(requireContext(), estadoSeccionOpciones)
        spinnerEstadoSeccion.adapter = estadoSeccionAdapter

        if (secciones != null) {
            cargarDatos(secciones)
            editarSeccion(secciones.IdSeccion)
        }
        return binding.root
    }
    private fun ApiEditarSeccion(idSeccion: Int){
        val secciones=seccionesViewModel.selectedSeccionData
        var idExamen=0
        if (secciones != null) {
            idExamen= secciones.IdExamen
        }
        val etTituloSeccion = binding.etTituloSeccionEditar.text.toString()
        val etOrden = binding.etOrdenSeccionEditar.text.toString()


        val spinnerEstadoSeccion = binding.spinnerEstadoSeccionEditar

        // Obtener el índice seleccionado en los spinners
        val seccionIndex = spinnerEstadoSeccion.selectedItemPosition

        val estadoSeccionSeleccionado = EstadoSeccionData.estadoSeccionOpciones[seccionIndex]
        val estadoSeccionValor = estadoSeccionSeleccionado.estadoSeccion

        val estadoSeccion=estadoSeccionValor
        val etDescripcionSeccion=stringToHex(binding.etDescripcionSeccionEditar.text.toString())

        val call=apiServicio.guardarEditarSecciones(idSeccion,etTituloSeccion,estadoSeccion,idExamen,etOrden.toInt(),etDescripcionSeccion)
        call.enqueue(object : Callback<SeccionResponse> {
            override fun onResponse(
                call: Call<SeccionResponse>,
                response: Response<SeccionResponse>
            ) {
                if (response.isSuccessful){
                    Toast.makeText(requireContext(), "Has editado la sección exitosamente", Toast.LENGTH_SHORT).show()
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
        val etTituloSeccion = binding.etTituloSeccionEditar.text.toString()
        val etOrden = binding.etOrdenSeccionEditar.text.toString()


        val spinnerEstadoSeccion = binding.spinnerEstadoSeccionEditar

        // Obtener el índice seleccionado en los spinners
        val seccionIndex = spinnerEstadoSeccion.selectedItemPosition

        val etDescripcionSeccion=binding.etDescripcionSeccionEditar.text.toString()

        if (etTituloSeccion.isEmpty() || etOrden.isEmpty() || seccionIndex == 0 || etDescripcionSeccion.isEmpty()) {
            Toast.makeText(requireContext(), "Por favor complete todos los campos", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    private fun cargarDatos(secciones: Secciones){
        binding.txtSeccion.text=secciones.TituloSeccion
        binding.etTituloSeccionEditar.setText(secciones.TituloSeccion)
        binding.etOrdenSeccionEditar.setText(secciones.Orden.toString())
        // Cargar estado del examen
        val estadoSeccionOpciones = EstadoSeccionData.estadoSeccionOpciones
        val estadoSeccionIndex = estadoSeccionOpciones.indexOfFirst { it.estadoSeccion == secciones.Activo }
        if (estadoSeccionIndex != -1) {
            binding.spinnerEstadoSeccionEditar.setSelection(estadoSeccionIndex)
        }
        binding.etDescripcionSeccionEditar.setText(eliminarEtiquetasHTML(secciones.DescripcionSeccion))
    }

    private fun editarSeccion(idSeccionData: Int){
        binding.btnEditarSeccion.setOnClickListener{
            if (validarInputs()){
                ApiEditarSeccion(idSeccionData)
            }
        }
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
        findNavController().navigate(R.id.action_editarSeccion_to_administrarSecciones)
    }
}