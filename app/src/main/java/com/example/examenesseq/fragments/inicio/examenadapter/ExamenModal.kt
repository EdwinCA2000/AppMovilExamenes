package com.example.examenesseq.fragments.inicio.examenadapter

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.example.examenesseq.R
import com.example.examenesseq.datos.ApiServicio
import com.example.examenesseq.datos.examen.DaoExamen
import com.example.examenesseq.datos.respuesta.LoginRespuesta
import com.example.examenesseq.datos.respuesta.RespuestaExamenUsuario
import com.example.examenesseq.model.examen.Examen
import com.example.examenesseq.util.PreferenceHelper
import com.example.examenesseq.util.PreferenceHelper.TieneExamenesUsuario
import com.example.examenesseq.util.PreferenceHelper.getExamenesUsuario
import com.example.examenesseq.util.PreferenceHelper.saveCantidadPreguntas
import com.example.examenesseq.util.PreferenceHelper.setDatosExamen
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Locale

class ExamenModal(private val examen: Examen) : DialogFragment() {

    val daoExamen = DaoExamen()

    private val apiServicio: ApiServicio by lazy {
        ApiServicio.create(requireContext())
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.modal_examen, null)


        view.findViewById<TextView>(R.id.tituloModal).text = examen.TituloExamen
        val descripcionSinEtiquetas=
            examen.DescripcionExamen.replace("<p>".toRegex(), "").replace("</p>".toRegex(), "")

        if (examen.DescripcionExamen.contains("<p>") && examen.DescripcionExamen.contains("</p>")){
            view.findViewById<TextView>(R.id.descripcionModal).text = descripcionSinEtiquetas
        }else{
            view.findViewById<TextView>(R.id.descripcionModal).text = examen.DescripcionExamen
        }

        //fechas
        val fechaInicioExamen=examen.FechaInicio.toString()
        val fechaFinalExamen=examen.FechaFinal.toString()

        view.findViewById<TextView>(R.id.fechaInicio).text = parsearFecha(fechaInicioExamen)
        view.findViewById<TextView>(R.id.fechaFinal).text = parsearFecha(fechaFinalExamen)

        //Tiempo transcurrido
        val duracionExamen= examen.TiempoExamen
        val duracionExamenReal=conversorHoras(duracionExamen)
        view.findViewById<TextView>(R.id.duracionExamen).text=duracionExamenReal
        val idExamen= examen.IdExamen
        val cantidadSecciones= daoExamen.obtenerCantidadSecciones(requireContext(),idExamen)


        if(cantidadSecciones==1){
            view.findViewById<TextView>(R.id.cantidadPreguntas).text="$cantidadSecciones sección"
        }else{
            view.findViewById<TextView>(R.id.cantidadPreguntas).text="$cantidadSecciones secciones"
        }

        val contenedorSecciones = view.findViewById<LinearLayout>(R.id.contenedorTitulos)
        val titulosSecciones = daoExamen.obtenerTitulosSecciones(requireContext(), idExamen)

        val idSecciones=daoExamen.obtenerIdsSecciones(requireContext(),idExamen)

        val btnIniciarExamen=view.findViewById<Button>(R.id.btnIniciarExamen)
        val preferences = PreferenceHelper.defaultPrefs(requireContext())
        if (preferences.TieneExamenesUsuario()) {
            val examenUsuario = preferences.getExamenesUsuario()
            val examenIniciado = examenUsuario?.find { it.IdExamen == idExamen && it.Estado == 1 }

            if (examenIniciado != null) {
                btnIniciarExamen.text = "Continuar Examen"
            } else {
                btnIniciarExamen.text = "Iniciar Examen"
            }
        } else {
            btnIniciarExamen.text = "Iniciar Examen"
        }



        for (i in 0 until titulosSecciones.size) {
            val titulo = titulosSecciones[i]
            val idSeccion = idSecciones[i]

            val textView = TextView(requireContext())
            textView.text = "- $titulo"  // Agregar un guion antes del título
            obtenerTotalPreguntas(idSeccion) { totalPreguntas ->
                val texto = "- $titulo ($totalPreguntas preguntas)"
                textView.text = texto
            }

            contenedorSecciones.addView(textView)
        }

        btnIniciarExamen.setOnClickListener {

            val builder = AlertDialog.Builder(requireContext())
            val dialogView = layoutInflater.inflate(R.layout.dialogconfirmacion, null)
            builder.setView(dialogView)
            val dialogconfirmacion= builder.create()

            val btnSi = dialogView.findViewById<Button>(R.id.btnSi)
            val btnNo = dialogView.findViewById<Button>(R.id.btnNo)
            val txtConfirmacion=dialogView.findViewById<TextView>(R.id.txtPreguntaConfirmacion)

            if (preferences.TieneExamenesUsuario()) {
                val examenUsuario = preferences.getExamenesUsuario()
                val examenIniciado = examenUsuario?.find { it.IdExamen == idExamen && it.Estado == 1 }

                if (examenIniciado != null) {
                    txtConfirmacion.text="Estás seguro de continuar el ${examen.TituloExamen}?"
                } else {
                    txtConfirmacion.text="Estás seguro de iniciar el ${examen.TituloExamen}?"
                }
            } else {
                txtConfirmacion.text="Estás seguro de iniciar el ${examen.TituloExamen}?"
            }

            btnSi.setOnClickListener {
                // Navegar a la pantalla de preguntas
                preferences.setDatosExamen(idExamen,duracionExamen)
                findNavController().navigate(R.id.action_inicio_to_preguntas)
                    val examenUsuario = preferences.getExamenesUsuario()
                    val examenIniciado = examenUsuario?.find { it.IdExamen == idExamen && it.Estado == 1 }

                    if (examenIniciado != null) {
                        val idExamenUsuarioIniciado = examenIniciado.IdExamenUsuario
                        apiServicio.obtenerDatosExamenUsuario(idExamen,idExamenUsuarioIniciado).enqueue(object : Callback<RespuestaExamenUsuario>{
                            override fun onResponse(
                                call: Call<RespuestaExamenUsuario>,
                                response: Response<RespuestaExamenUsuario>
                            ) {
                                if (response.isSuccessful){
                                    val respuesta=response.body()
                                    if (respuesta != null) {
                                    }
                                }
                            }

                            override fun onFailure(
                                call: Call<RespuestaExamenUsuario>,
                                t: Throwable
                            ) {
                                Toast.makeText(requireContext(), "Fallo: ${t.message}", Toast.LENGTH_SHORT).show()
                                Log.e("API Failure", "Error: ${t.message}", t)
                            }

                        })
                    }else{
                        val time=0
                        val calif=0
                        apiServicio.guardarExamenUsuario(time,calif,examen.IdExamen).enqueue(object : Callback<LoginRespuesta>{
                            override fun onResponse(
                                call: Call<LoginRespuesta>,
                                response: Response<LoginRespuesta>
                            ) {
                                if (response.isSuccessful){
                                    val respuesta=response.body()
                                    if (respuesta != null) {
                                        Log.e("Respuesta",respuesta.Mensaje)
                                    }
                                }
                            }

                            override fun onFailure(call: Call<LoginRespuesta>, t: Throwable) {
                                Toast.makeText(requireContext(), "Fallo: ${t.message}", Toast.LENGTH_SHORT).show()
                                Log.e("API Failure", "Error: ${t.message}", t)
                            }

                        })
                    }

                dialogconfirmacion.dismiss()
                dialog?.dismiss()
            }

            btnNo.setOnClickListener {
                dialogconfirmacion.dismiss()
            }

            dialogconfirmacion.show()

        }


        builder.setView(view)
            .setTitle("Detalles del examen")
            .setPositiveButton("Cerrar") { dialog, _ ->
                dialog.dismiss()
            }

        return builder.create()
    }


    private fun obtenerTotalPreguntas(idSeccion: Int, callback: (Int) -> Unit) {
        apiServicio.obtenerTotalPreguntas(idSeccion).enqueue(object : Callback<LoginRespuesta> {
            override fun onResponse(call: Call<LoginRespuesta>, response: Response<LoginRespuesta>) {
                if (response.isSuccessful) {
                    val loginRespuesta = response.body()
                    val totalPreguntas = loginRespuesta?.Mensaje
                    if (totalPreguntas != null) {
                        val preferences = PreferenceHelper.defaultPrefs(requireContext())
                        preferences.saveCantidadPreguntas(totalPreguntas)
                        callback(totalPreguntas.toInt())
                    }
                } else {
                    Toast.makeText(requireContext(), "Hubo un error en la respuesta del servidor", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<LoginRespuesta>, t: Throwable) {
                Toast.makeText(requireContext(), "Fallo: ${t.message}", Toast.LENGTH_SHORT).show()
                Log.e("API Failure", "Error: ${t.message}", t)
            }
        })
    }
    fun conversorHoras(duracionExam: Int): String{
        var duracionExamen=""
        if(duracionExam>=60){
            val calculoHoras=duracionExam/60
            duracionExamen= "$calculoHoras horas"
        }else{
            duracionExamen= "$duracionExam minutos"
        }
        return duracionExamen
    }
    fun parsearFecha(fechas: String): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
        val parsedDate = dateFormat.parse(fechas)
        val formattedDate = parsedDate?.let {
            SimpleDateFormat("yyyy-MM-dd hh:mm:ss a", Locale.US).format(
                it
            )
        }

        return formattedDate.toString()
    }
}