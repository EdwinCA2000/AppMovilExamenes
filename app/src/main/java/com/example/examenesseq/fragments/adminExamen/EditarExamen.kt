package com.example.examenesseq.fragments.adminExamen

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.examenesseq.R
import com.example.examenesseq.databinding.FragmentEditarExamenBinding
import com.example.examenesseq.datos.ApiServicio
import com.example.examenesseq.fragments.crearExamen.adapter.DuracionExamenAdapter
import com.example.examenesseq.fragments.crearExamen.adapter.EstadoExamenAdapter
import com.example.examenesseq.fragments.crearExamen.data.DuracionExamenData
import com.example.examenesseq.fragments.crearExamen.data.EstadoExamenData
import com.example.examenesseq.model.crearExamen.ExamenResponse
import com.example.examenesseq.secciones.viewmodel.ExamenViewModel
import org.jsoup.Jsoup
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


class EditarExamen : Fragment() {

    private val apiServicio: ApiServicio by lazy {
        ApiServicio.create(requireContext())
    }
    private var _binding: FragmentEditarExamenBinding? = null
    private val binding get() = _binding!!

    private lateinit var examenViewModel: ExamenViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentEditarExamenBinding.inflate(inflater, container, false)
        examenViewModel = ViewModelProvider(requireActivity())[ExamenViewModel::class.java]
        val examenData= examenViewModel.selectedExamenData

        val spinnerDuracionExamen = binding.spinnerDuracionExamenEditar
        val duracionExamenOpciones = DuracionExamenData.duracionExamenOpciones
        val duracionExamenAdapter = DuracionExamenAdapter(requireContext(), duracionExamenOpciones)
        spinnerDuracionExamen.adapter = duracionExamenAdapter

        //Estado Examen
        val spinnerEstadoExamen = binding.spinnerEstadoExamenEditar
        val estadoExamenOpciones = EstadoExamenData.estadoExamenOpciones
        val estadoExamenAdapter = EstadoExamenAdapter(requireContext(), estadoExamenOpciones)
        spinnerEstadoExamen.adapter = estadoExamenAdapter
        if (examenData != null) {
            cargarDatos(examenData)
            crearExamen(examenData.IdExamen)
        }

        datePickerFechaInicio()
        datePickerFechaFinal()
        return binding.root
    }

    private fun crearExamen(idExamenData: Int){
        binding.btnEditarExamen.setOnClickListener{
            if (validarInputs()){
                apiCrearExamen(idExamenData)
            }
        }
    }

    private fun apiCrearExamen(idExamenData: Int){
        val idExamen= idExamenData
        val tituloExamen=binding.etTituloExamenEditar.text.toString()
        val fechaInicio=binding.etFechaInicioEditar.text.toString()
        val fechaFinal= binding.etFechaFinalEditar.text.toString()
        val fechaCreacion= obtenerFechaActual()

        val spinnerDuracionExamen = binding.spinnerDuracionExamenEditar
        val spinnerEstadoExamen = binding.spinnerEstadoExamenEditar

        // Obtener el índice seleccionado en los spinners
        val duracionExamenIndex = spinnerDuracionExamen.selectedItemPosition
        val estadoExamenIndex = spinnerEstadoExamen.selectedItemPosition

        val duracionExamenSeleccionado = DuracionExamenData.duracionExamenOpciones[duracionExamenIndex]
        val duracionExamenValor = duracionExamenSeleccionado.duracionExamen

        val estadoExamenSeleccionado = EstadoExamenData.estadoExamenOpciones[estadoExamenIndex]
        val estadoExamenValor = estadoExamenSeleccionado.estadoExamen

        val duracionExamen=duracionExamenValor
        val estadoExamen=estadoExamenValor

        val etDescripcionExamen=binding.etDescripcionExamenEditar.text.toString()
        val etBienvenidaExamen=binding.etBienvenidaExamenEditar.text.toString()
        // Convertir el texto a hexadecimal
        val descripcionExamenHex = stringToHex(etDescripcionExamen)
        val bienvenidaExamenHex = stringToHex(etBienvenidaExamen)

        val call=apiServicio.crearExamen(idExamen,tituloExamen,estadoExamen,fechaCreacion,fechaInicio,fechaFinal,
            duracionExamen,descripcionExamenHex,bienvenidaExamenHex)
        call.enqueue(object : Callback<ExamenResponse> {
            override fun onResponse(
                call: Call<ExamenResponse>,
                response: Response<ExamenResponse>
            ) {
                if (response.isSuccessful){
                    Toast.makeText(requireContext(), "Has editado el examen exitosamente", Toast.LENGTH_SHORT).show()
                    irAdministrarExamenes()
                }
            }

            override fun onFailure(call: Call<ExamenResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "Fallo: ${t.message}", Toast.LENGTH_SHORT).show()
                Log.e("API Failure", "Error: ${t.message}", t)
            }

        })
    }

    private fun validarInputs(): Boolean {
        val etTituloExamen = binding.etTituloExamenEditar.text.toString()
        val etFechaInicio = binding.etFechaInicioEditar.text.toString()
        val etFechaFin = binding.etFechaFinalEditar.text.toString()

        val spinnerDuracionExamen = binding.spinnerDuracionExamenEditar
        val spinnerEstadoExamen = binding.spinnerEstadoExamenEditar

        // Obtener el índice seleccionado en los spinners
        val duracionExamenIndex = spinnerDuracionExamen.selectedItemPosition
        val estadoExamenIndex = spinnerEstadoExamen.selectedItemPosition

        val etDescripcionExamen=binding.etDescripcionExamenEditar.text.toString()
        val etBienvenidaExamen=binding.etBienvenidaExamenEditar.text.toString()

        if (etTituloExamen.isEmpty() || etFechaInicio.isEmpty() || etFechaFin.isEmpty() ||
            duracionExamenIndex == 0 || estadoExamenIndex == 0 || etDescripcionExamen.isEmpty() || etBienvenidaExamen.isEmpty()) {
            Toast.makeText(requireContext(), "Por favor complete todos los campos", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    private fun cargarDatos(examenesData: AdminExamenesData){
        binding.txtExamen.text=examenesData.TituloExamen
        binding.etTituloExamenEditar.setText(examenesData.TituloExamen)
        val formatoFecha = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val fechaInicio = formatoFecha.parse(examenesData.FechaInicio)
        val fechaFinal = formatoFecha.parse(examenesData.FechaFinal)

        // Formatear las fechas como cadenas sin el ".0"
        val fechaInicioFormateada = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(fechaInicio)
        val fechaFinalFormateada = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(fechaFinal)

        binding.etFechaInicioEditar.setText(fechaInicioFormateada)
        binding.etFechaFinalEditar.setText(fechaFinalFormateada)

        // Cargar duración del examen
        val duracionExamenOpciones = DuracionExamenData.duracionExamenOpciones
        val duracionExamenIndex = duracionExamenOpciones.indexOfFirst { it.duracionExamen == examenesData.TiempoExamen }
        if (duracionExamenIndex != -1) {
            binding.spinnerDuracionExamenEditar.setSelection(duracionExamenIndex)
        }

        // Cargar estado del examen
        val estadoExamenOpciones = EstadoExamenData.estadoExamenOpciones
        val estadoExamenIndex = estadoExamenOpciones.indexOfFirst { it.estadoExamen == examenesData.Activo }
        if (estadoExamenIndex != -1) {
            binding.spinnerEstadoExamenEditar.setSelection(estadoExamenIndex)
        }

        binding.etDescripcionExamenEditar.setText(eliminarEtiquetasHTML(examenesData.DescripcionExamen))
        binding.etBienvenidaExamenEditar.setText(eliminarEtiquetasHTML(examenesData.TextoBienvenida))
    }

    fun eliminarEtiquetasHTML(textoHTML: String): String {
        val documento = Jsoup.parse(textoHTML)
        return documento.text()
    }

    private fun datePickerFechaInicio (){
        val dpFechaInicio=binding.etFechaInicioEditar
        dpFechaInicio.setOnClickListener{
            showDateTimePickerDialog(dpFechaInicio)
        }
    }

    private fun datePickerFechaFinal (){
        val dpFechaFinal=binding.etFechaFinalEditar
        dpFechaFinal.setOnClickListener{
            showDateTimePickerDialog(dpFechaFinal)
        }
    }

    private fun showDateTimePickerDialog(editText: EditText) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        // Crea un DatePickerDialog para seleccionar la fecha
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, selectedYear, selectedMonth, selectedDay ->
                // Cuando se selecciona la fecha, muestra el TimePickerDialog
                showTimePickerDialog(editText, selectedYear, selectedMonth, selectedDay)
            },
            year, month, day
        )

        datePickerDialog.show()
    }

    private fun showTimePickerDialog(editText: EditText, year: Int, month: Int, day: Int) {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        // TimePickerDialog para seleccionar la hora
        val timePickerDialog = TimePickerDialog(
            requireContext(),
            { _, selectedHour, selectedMinute ->
                // obtener la fecha y hora seleccionadas y mostrarlas en el EditText
                val selectedDateTime = String.format(
                    Locale.getDefault(),
                    "%04d-%02d-%02d %02d:%02d:00",
                    year,
                    month + 1,
                    day,
                    selectedHour,
                    selectedMinute
                )

                // Actualiza el EditText con la fecha y la hora seleccionadas
                editText.setText(selectedDateTime)
            },
            hour, minute, true
        )

        timePickerDialog.show()
    }

    private fun stringToHex(input: String): String {
        val stringBuilder = StringBuilder()
        for (char in input.toCharArray()) {
            val hex = String.format("%02X", char.code)
            stringBuilder.append(hex)
        }
        return stringBuilder.toString()
    }

    fun obtenerFechaActual(): String {
        val formato = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val fechaActual = Date()
        return formato.format(fechaActual)
    }

    private fun irAdministrarExamenes(){
        findNavController().navigate(R.id.action_editarExamen_to_administrarExamenes)
    }
}