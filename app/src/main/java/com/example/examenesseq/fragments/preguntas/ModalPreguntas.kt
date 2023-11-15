package com.example.examenesseq.fragments.preguntas

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.example.examenesseq.R
import com.example.examenesseq.datos.ApiServicio
import com.example.examenesseq.fragments.preguntas.adapter.EstadoPreguntaAdapter
import com.example.examenesseq.fragments.preguntas.adapter.TipoPreguntaAdapter
import com.example.examenesseq.fragments.preguntas.data.EstadoPregunta
import com.example.examenesseq.fragments.preguntas.data.Pregunta
import com.example.examenesseq.fragments.preguntas.data.TipoPregunta
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ModalPreguntas(private val idSeccion: Int, private val preguntas: Pregunta? = null, private val listener: OnPreguntaChangedListener?): DialogFragment() {

    private lateinit var txtNumeroPregunta: EditText
    private lateinit var spinnerEstadoPregunta: Spinner
    private lateinit var spinnerTipoPregunta: Spinner
    private lateinit var cardEstadoPregunta: CardView
    private lateinit var btnGuardarEditar: Button

    private val apiServicio: ApiServicio by lazy {
        ApiServicio.create(requireContext())
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.modal_preguntas, null)

        txtNumeroPregunta= view.findViewById(R.id.etNumeroPregunta)
        spinnerEstadoPregunta= view.findViewById(R.id.spinnerEstadoPregunta)
        spinnerTipoPregunta=view.findViewById(R.id.spinnerTipoPregunta)
        cardEstadoPregunta=view.findViewById(R.id.estadoPreguntaCard)
        btnGuardarEditar=view.findViewById(R.id.btnGuardarEditar)

        //Cargar spinner de estado de pregunta
        val estadoPreguntaOpciones = EstadoPregunta.estadoPreguntaOpciones
        val estadoPreguntaAdapter = EstadoPreguntaAdapter(requireContext(), estadoPreguntaOpciones)
        spinnerEstadoPregunta.adapter = estadoPreguntaAdapter

        //Cargar spinner de tipo de pregunta
        val tipoPreguntaOpciones = TipoPregunta.tipoPreguntaOpciones
        val tipoPreguntaAdapter = TipoPreguntaAdapter(requireContext(), tipoPreguntaOpciones)
        spinnerTipoPregunta.adapter = tipoPreguntaAdapter

        if (preguntas!=null){
            txtNumeroPregunta.setText(preguntas.Numero.toString())
            val estadoPreguntaIndex = estadoPreguntaOpciones.indexOfFirst { it.estadoPregunta == preguntas.Activo }
            if (estadoPreguntaIndex != -1) {
                spinnerEstadoPregunta.setSelection(estadoPreguntaIndex)
            }

            val tipoPreguntaIndex = tipoPreguntaOpciones.indexOfFirst { it.tipoPregunta == preguntas.IdTipoPregunta }
            if (tipoPreguntaIndex != -1) {
                spinnerTipoPregunta.setSelection(tipoPreguntaIndex)
            }
            if (preguntas.Activo==1){
                cardEstadoPregunta.setCardBackgroundColor(ContextCompat.getColor(requireContext(),
                    R.color.green
                ))
            }else{
                cardEstadoPregunta.setCardBackgroundColor(ContextCompat.getColor(requireContext(),
                    R.color.red
                ))
            }
            btnGuardarEditar.text="EDITAR PREGUNTA"

            btnGuardarEditar.setOnClickListener {
                if (validarCampos()){
                    val idPregunta=preguntas.IdPregunta
                    ApiAgregarPregunta(idPregunta,idSeccion)
                }
            }

        }else{
            txtNumeroPregunta.setText("")
            spinnerEstadoPregunta.setSelection(0)
            spinnerTipoPregunta.setSelection(0)
            cardEstadoPregunta.visibility=View.GONE
            btnGuardarEditar.text="GUARDAR PREGUNTA"
            btnGuardarEditar.setOnClickListener {
                if (validarCampos()){
                    ApiAgregarPregunta(-1,idSeccion)
                }
            }
        }

        builder.setView(view)
        return builder.create()
    }

    private fun ApiAgregarPregunta(idPregunta: Int,idSeccion: Int){
        val etNumeroPregunta = txtNumeroPregunta.text.toString()


        val spinnerEstadoPreguntas = spinnerEstadoPregunta

        // Obtener el índice seleccionado en los spinners

        //Estado Pregunta
        val spinnerEstadoPreguntaIndex = spinnerEstadoPreguntas.selectedItemPosition

        val estadoPreguntaSeleccionado = EstadoPregunta.estadoPreguntaOpciones[spinnerEstadoPreguntaIndex]
        val estadoPreguntaValor = estadoPreguntaSeleccionado.estadoPregunta

        val estadoPregunta=estadoPreguntaValor

        //Tipo Pregunta

        val spinnerTipoPreguntaIndex = spinnerTipoPregunta.selectedItemPosition

        val tipoPreguntaSeleccionado = TipoPregunta.tipoPreguntaOpciones[spinnerTipoPreguntaIndex]
        val tipoPreguntaPreguntaValor = tipoPreguntaSeleccionado.tipoPregunta

        val tipoPregunta=tipoPreguntaPreguntaValor

        val call=apiServicio.guardarEditarPreguntas(idPregunta,etNumeroPregunta.toInt(),estadoPregunta,idSeccion,tipoPregunta)
        call.enqueue(object : Callback<Pregunta> {
            override fun onResponse(
                call: Call<Pregunta>,
                response: Response<Pregunta>
            ) {
                if (response.isSuccessful){
                    Toast.makeText(requireContext(), "Has agregado la pregunta ${etNumeroPregunta} exitosamente", Toast.LENGTH_SHORT).show()
                    dismiss()
                    listener?.onPreguntaChanged(idSeccion)
                }
            }

            override fun onFailure(call: Call<Pregunta>, t: Throwable) {
                Toast.makeText(requireContext(), "Fallo: ${t.message}", Toast.LENGTH_SHORT).show()
                Log.e("API Failure", "Error: ${t.message}", t)
            }

        })

    }

    private fun validarCampos(): Boolean {
        val etNumeroPregunta = txtNumeroPregunta.text.toString()


        // Obtener el índice seleccionado en los spinners

        //Estado Pregunta
        val spinnerEstadoPreguntaIndex = spinnerEstadoPregunta.selectedItemPosition

        //Tipo Pregunta

        val spinnerTipoPreguntaIndex = spinnerTipoPregunta.selectedItemPosition


        // Validar que los campos no estén vacíos
        if (etNumeroPregunta.isEmpty() || spinnerEstadoPreguntaIndex == 0 || spinnerTipoPreguntaIndex == 0) {
            Toast.makeText(requireContext(), "Por favor complete todos los campos", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
    }

    interface OnPreguntaChangedListener {
        fun onPreguntaChanged(idSeccion: Int)
    }


}