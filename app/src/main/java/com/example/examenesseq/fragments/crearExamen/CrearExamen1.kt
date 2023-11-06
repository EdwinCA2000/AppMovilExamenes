package com.example.examenesseq.fragments.crearExamen

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.examenesseq.R
import com.example.examenesseq.databinding.FragmentCrearExamen1Binding
import com.example.examenesseq.fragments.crearExamen.adapter.DuracionExamenAdapter
import com.example.examenesseq.fragments.crearExamen.data.DuracionExamenData
import com.example.examenesseq.fragments.crearExamen.data.DuracionExamenData.Companion.duracionExamenOpciones
import com.example.examenesseq.fragments.crearExamen.adapter.EstadoExamenAdapter
import com.example.examenesseq.fragments.crearExamen.data.EstadoExamenData
import com.example.examenesseq.fragments.crearExamen.data.EstadoExamenData.Companion.estadoExamenOpciones
import java.util.Calendar
import java.util.Locale

class CrearExamen1 : Fragment() {

    private var _binding: FragmentCrearExamen1Binding? = null
    private val binding get() = _binding!!
    private lateinit var crearExamenViewModel: CrearExamenViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentCrearExamen1Binding.inflate(inflater, container, false)

        //Duración examen
        val spinnerDuracionExamen = binding.spinnerDuracionExamen
        val duracionExamenOpciones = DuracionExamenData.duracionExamenOpciones
        val duracionExamenAdapter = DuracionExamenAdapter(requireContext(), duracionExamenOpciones)
        spinnerDuracionExamen.adapter = duracionExamenAdapter

        //Estado Examen
        val spinnerEstadoExamen = binding.spinnerEstadoExamen
        val estadoExamenOpciones = EstadoExamenData.estadoExamenOpciones
        val estadoExamenAdapter = EstadoExamenAdapter(requireContext(), estadoExamenOpciones)
        spinnerEstadoExamen.adapter = estadoExamenAdapter

        //Actions
        datePickerFechaInicio()
        datePickerFechaFinal()
        irACrearExamen2()

        return binding.root
    }

    private fun datePickerFechaInicio (){
        val dpFechaInicio=binding.etFechaInicio
        dpFechaInicio.setOnClickListener{
            showDateTimePickerDialog(dpFechaInicio)
        }
    }

    private fun datePickerFechaFinal (){
        val dpFechaFinal=binding.etFechaFinal
        dpFechaFinal.setOnClickListener{
            showDateTimePickerDialog(dpFechaFinal)
        }
    }

    private fun irACrearExamen2(){
        val btnSiguiente=binding.btnSiguiente
        btnSiguiente.setOnClickListener{
            if(validarInputs()){
                actionIrCrearExamen2()
            }
        }
    }

    private fun actionIrCrearExamen2(){
        findNavController().navigate(R.id.action_crearExamen1_to_crearExamen2)
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

    private fun validarInputs(): Boolean {
        crearExamenViewModel = ViewModelProvider(requireActivity())[CrearExamenViewModel::class.java]
        val etTituloExamen = binding.etTituloExamen.text.toString()
        val etFechaInicio = binding.etFechaInicio.text.toString()
        val etFechaFin = binding.etFechaFinal.text.toString()

        val spinnerDuracionExamen = binding.spinnerDuracionExamen
        val spinnerEstadoExamen = binding.spinnerEstadoExamen

        // Obtener el índice seleccionado en los spinners
        val duracionExamenIndex = spinnerDuracionExamen.selectedItemPosition
        val estadoExamenIndex = spinnerEstadoExamen.selectedItemPosition

        if (etTituloExamen.isEmpty() || etFechaInicio.isEmpty() || etFechaFin.isEmpty() ||
            duracionExamenIndex == 0 || estadoExamenIndex == 0) {
            Toast.makeText(requireContext(), "Por favor complete todos los campos", Toast.LENGTH_SHORT).show()
            return false
        }

        val duracionExamenSeleccionado = duracionExamenOpciones[duracionExamenIndex]
        val duracionExamenValor = duracionExamenSeleccionado.duracionExamen

        val estadoExamenSeleccionado = estadoExamenOpciones[duracionExamenIndex]
        val estadoExamenValor = estadoExamenSeleccionado.estadoExamen

        crearExamenViewModel.tituloExamen=etTituloExamen
        crearExamenViewModel.fechaInicio=etFechaInicio
        crearExamenViewModel.fechaFinal=etFechaFin
        crearExamenViewModel.duracionExamen=duracionExamenValor
        crearExamenViewModel.estadoExamen=estadoExamenValor

        return true
    }

}