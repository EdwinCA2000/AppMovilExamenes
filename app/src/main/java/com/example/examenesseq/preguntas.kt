package com.example.examenesseq


import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.examenesseq.databinding.PreguntasBinding
import com.example.examenesseq.datos.ApiServicio
import com.example.examenesseq.datos.respuesta.LoginRespuesta
import com.example.examenesseq.datos.respuesta.RespuestaExamen
import com.example.examenesseq.datos.respuesta.Respuestas
import com.example.examenesseq.fragments.inicio.examenadapter.ExamenAdapter
import com.example.examenesseq.model.examen.Examen
import com.example.examenesseq.model.examen.ExamenUsuario
import com.example.examenesseq.model.examen.PreguntasExamen
import com.example.examenesseq.model.examen.SeccionesExamen
import com.example.examenesseq.util.PreferenceHelper
import com.example.examenesseq.util.PreferenceHelper.TieneExamenesUsuario
import com.example.examenesseq.util.PreferenceHelper.getDuracionExamen
import com.example.examenesseq.util.PreferenceHelper.getExamenesUsuario
import com.example.examenesseq.util.PreferenceHelper.getIdExamen
import com.example.examenesseq.util.PreferenceHelper.getIdentidad
import com.example.examenesseq.util.PreferenceHelper.saveExamenesUsuario
import org.jsoup.Jsoup
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.TimeUnit


class preguntas : Fragment() {

    private var _binding: PreguntasBinding? = null
    private val binding get() = _binding!!

    private val apiServicio: ApiServicio by lazy {
        ApiServicio.create(requireContext())
    }
    private var countDownTimer: CountDownTimer? = null
    private var indicePreguntaActual = 0
    private var botonSeleccionado: Button? = null
    private var indiceSeccionActual = 0
    private var respuestaActual: Respuestas? = null
    private val respuestasList = mutableListOf<Respuestas>()
    private var idOpcionSeleccionada: Int = -1
    private var tiempoRestanteMilisegundos: Long = 0
    private var tiempoTranscurridoMilisegundos: Long = 0
    private var idExamenUsuario: Int = 0
    private var cantidadTotalPreguntas = 0
    private var preguntaActualEnExamen = 0




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = PreguntasBinding.inflate(inflater, container, false)
        (requireActivity() as AppCompatActivity).supportActionBar?.show()

        val preferences = PreferenceHelper.defaultPrefs(requireContext())
        val duracionExamen = preferences.getDuracionExamen() * 60000
        obtenerCantidadTotalPreguntas()
        obtenerDatosDeExamenUsuario()
        iniciarTemporizador(duracionExamen, binding.temporizador)
        onStart()
        obtenerPreguntas()
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            confirmacionDeSalida()
        }
    }

    private fun confirmacionDeSalida() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Confirmación")
        builder.setMessage("¿Estás seguro de que deseas abandonar el examen?")
        builder.setPositiveButton("Sí") { _, _ ->
            findNavController().navigate(R.id.action_preguntas_to_inicio)
        }
        builder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }

    private fun iniciarTemporizador(duracionExamenMilisegundos: Int, temporizador: TextView) {
        countDownTimer = object : CountDownTimer(duracionExamenMilisegundos.toLong(), 1000) {
            override fun onTick(millisUntilFinished: Long) {
                tiempoRestanteMilisegundos = millisUntilFinished
                tiempoTranscurridoMilisegundos = duracionExamenMilisegundos - millisUntilFinished
                val segundos = millisUntilFinished / 1000
                val horas = segundos / 3600
                val minutos = (segundos % 3600) / 60
                val segundosRestantes = segundos % 60

                val tiempoFormateado =
                    String.format("%02d:%02d:%02d", horas, minutos, segundosRestantes)
                temporizador.text = tiempoFormateado
            }

            override fun onFinish() {
                temporizador.text = "00:00:00"
            }
        }
        countDownTimer?.start()
    }

    private fun obtenerDatosDeExamenUsuario() {
        val preferences = PreferenceHelper.defaultPrefs(requireContext())
        val idExamen = preferences.getIdExamen()
        val idUsuario = preferences.getIdentidad()?.IdUsuario
        if (idUsuario != null) {
            apiServicio.obtenerExamenUsuario(idUsuario)
                .enqueue(object : Callback<List<ExamenUsuario>> {
                    override fun onResponse(
                        call: Call<List<ExamenUsuario>>,
                        response: Response<List<ExamenUsuario>>
                    ) {
                        if (response.isSuccessful) {
                            val exameneusuario = response.body()
                            if (!exameneusuario.isNullOrEmpty()) {
                                val datosUsuario =
                                    exameneusuario.find { it.IdExamen == idExamen && it.Estado == 1 }
                                if (datosUsuario != null) {
                                    idExamenUsuario = datosUsuario.IdExamenUsuario
                                }
                            }
                        } else {
                            Toast.makeText(
                                requireContext(),
                                "Hubo error en la respuesta del servidor",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onFailure(call: Call<List<ExamenUsuario>>, t: Throwable) {
                        Toast.makeText(requireContext(), "Fallo: ${t.message}", Toast.LENGTH_SHORT)
                            .show()
                        Log.e("API Failure", "Error: ${t.message}", t)
                    }

                })
        }
    }
    private fun obtenerPreguntas() {
        val preferences = PreferenceHelper.defaultPrefs(requireContext())
        val idExamen = preferences.getIdExamen()
        apiServicio.obtenerExamen(idExamen).enqueue(object : Callback<RespuestaExamen> {
            override fun onResponse(
                call: Call<RespuestaExamen>, response: Response<RespuestaExamen>
            ) {
                if (response.isSuccessful) {
                    val respuesta = response.body()
                    val respuestaSeccion = respuesta?.Secciones
                    if (respuestaSeccion != null) {
                        indiceSeccionActual = 0
                        indicePreguntaActual = 0 // Reinicia el índice de preguntas
                        mostrarPregunta(
                            respuestaSeccion[indiceSeccionActual].Preguntas, respuestaSeccion
                        )
                    }
                }
            }

            override fun onFailure(call: Call<RespuestaExamen>, t: Throwable) {
                Toast.makeText(requireContext(), "Fallo: ${t.message}", Toast.LENGTH_SHORT).show()
                Log.e("API Failure", "Error: ${t.message}", t)
            }
        })
    }

    private fun obtenerCantidadTotalPreguntas() {
        val preferences = PreferenceHelper.defaultPrefs(requireContext())
        val idExamen = preferences.getIdExamen()

        apiServicio.obtenerExamen(idExamen).enqueue(object : Callback<RespuestaExamen> {
            override fun onResponse(
                call: Call<RespuestaExamen>, response: Response<RespuestaExamen>
            ) {
                if (response.isSuccessful) {
                    val respuesta = response.body()
                    val respuestaSeccion = respuesta?.Secciones
                    if (respuestaSeccion != null) {
                        cantidadTotalPreguntas = respuestaSeccion.sumOf { it.Preguntas.size }
                    }
                }
            }

            override fun onFailure(call: Call<RespuestaExamen>, t: Throwable) {
            }
        })
    }


    fun mostrarPregunta(preguntasExamen: List<PreguntasExamen>, secciones: List<SeccionesExamen>) {
        val preferences = PreferenceHelper.defaultPrefs(requireContext())
        val idExamen = preferences.getIdExamen()

        if (indicePreguntaActual < preguntasExamen.size) {
            val pregunta = preguntasExamen[indicePreguntaActual]
            val preguntaView =
                LayoutInflater.from(requireContext()).inflate(R.layout.elementopregunta, null)
            val btnResponder = preguntaView.findViewById<Button>(R.id.btnResponder)
            val txtNumPregunta = preguntaView.findViewById<TextView>(R.id.txtNumPregunta)
            val txtPregunta = preguntaView.findViewById<TextView>(R.id.preguntaTxt)
            val btnRespuesta1 = preguntaView.findViewById<Button>(R.id.btnRespuesta1)
            val btnRespuesta2 = preguntaView.findViewById<Button>(R.id.btnRespuesta2)
            val btnRespuesta3 = preguntaView.findViewById<Button>(R.id.btnRespuesta3)
            val btnRespuesta4 = preguntaView.findViewById<Button>(R.id.btnRespuesta4)

            txtNumPregunta.text = "Pregunta ${pregunta.Numero}"

            val resultado = limpiarTextoPregunta(pregunta.contenidoPregunta)

            txtPregunta.text = resultado

            val opciones = pregunta.opciones
            if (opciones != null) {
                opciones.forEachIndexed { i, opcion ->
                    val contenidoOpcion = opcion.contenidoOpcion
                    val text = limpiarTextoPregunta(contenidoOpcion) // Extraer el texto

                    when (i) {
                        0 -> btnRespuesta1.text = text
                        1 -> btnRespuesta2.text = text
                        2 -> btnRespuesta3.text = text
                        3 -> btnRespuesta4.text = text
                    }
                }
            }
            binding.container.removeAllViews()
            binding.container.addView(preguntaView)

            val botones = listOf(btnRespuesta1, btnRespuesta2, btnRespuesta3, btnRespuesta4)
            val coloresOriginales =
                botones.map { it.currentTextColor } // Almacena los colores originales de los botones
            val fondosOriginales = botones.map { it.background }


            var idOpcion = 0
            botones.forEachIndexed { index, boton ->
                boton.setOnClickListener {

                    idOpcionSeleccionada = 1
                    // Restaura el color de texto y el fondo de todos los botones
                    botones.forEachIndexed { i, b ->
                        if (i != index) {
                            b.setTextColor(coloresOriginales[i])
                            b.background = fondosOriginales[i]
                        }
                    }

                    botonSeleccionado = boton

                    // Encuentra la opción seleccionada en la lista de opciones
                    val opcionSeleccionada = opciones[index]
                    if (opcionSeleccionada != null) {
                        idOpcion = opcionSeleccionada.IdOpcion
                    } else {
                        idOpcion = -1
                    }

                    // Cambia el color de texto y el fondo del botón seleccionado
                    boton.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                    boton.setBackgroundResource(R.drawable.btnseleccionado)

                    if (preferences.TieneExamenesUsuario()) {
                        val examenUsuario = preferences.getExamenesUsuario()
                        val examenIniciado =
                            examenUsuario?.find { it.IdExamen == idExamen && it.Estado == 1 }
                        val respuesta = pregunta.respuesta

                        var idRespuestaPregunta = respuesta.idRespuesta
                        if (idRespuestaPregunta == 0) {
                            idRespuestaPregunta = -1
                        } else {
                            idRespuestaPregunta = respuesta.idRespuesta
                        }

                        // Guardar la respuesta actual en la variable respuestaActual
                        respuestaActual = Respuestas(
                            idRespuesta = idRespuestaPregunta,
                            idPregunta = pregunta.idPregunta,
                            idOpcion = idOpcion,
                            idExamen = examenIniciado?.IdExamenUsuario ?: -1
                        )
                    }
                }
            }

            btnResponder.setOnClickListener {

                if (idOpcionSeleccionada == -1) {
                    // Muestra una alerta o notificación al usuario para informar que debe seleccionar una opción.
                    AlertDialog.Builder(requireContext()).setTitle("Seleccione una opción")
                        .setMessage("Por favor, seleccione una opción antes de continuar.")
                        .setPositiveButton("OK") { dialog, _ ->
                            dialog.dismiss()
                        }.show()
                } else {
                    respuestasList.clear()

                    if (respuestaActual != null) {
                        respuestasList.add(respuestaActual!!)
                    }
                    if (respuestasList.isNotEmpty()) {
                        for (respuesta in respuestasList) {
                            apiServicio.guardarRespuestaUsuario(
                                respuesta.idRespuesta,
                                respuesta.idPregunta,
                                respuesta.idOpcion,
                                respuesta.idExamen
                            ).enqueue(object : Callback<Respuestas> {
                                override fun onResponse(
                                    call: Call<Respuestas>, response: Response<Respuestas>
                                ) {
                                    if (response.isSuccessful) {
                                        preguntaActualEnExamen++
                                        var progresoActual: Int

                                        if (preguntaActualEnExamen==cantidadTotalPreguntas){
                                            progresoActual=100
                                        }else{
                                            progresoActual=(preguntaActualEnExamen * 100) / cantidadTotalPreguntas
                                        }
                                        _binding?.barraProgresoExamen?.progress = progresoActual
                                        idOpcionSeleccionada=-1
                                    } else {
                                    }
                                }
                                override fun onFailure(call: Call<Respuestas>, t: Throwable) {
                                    Toast.makeText(
                                        requireContext(), "Fallo: ${t.message}", Toast.LENGTH_SHORT
                                    ).show()
                                    Log.e("API Failure", "Error: ${t.message}", t)
                                }
                            })
                        }
                    } else {
                    }

                    if (indicePreguntaActual < preguntasExamen.size - 1) {
                        avanzarAPreguntaSiguiente()
                        mostrarPregunta(preguntasExamen, secciones)
                    } else {
                        avanzarASeccionSiguiente()
                        if (indiceSeccionActual < secciones.size) {
                            mostrarPregunta(secciones[indiceSeccionActual].Preguntas, secciones)
                        } else {
                            val btnMostrarFinal = preguntaView.findViewById<Button>(R.id.btnFinalizarExamen)
                            btnMostrarFinal.visibility = View.VISIBLE
                            btnResponder.visibility = View.GONE

                            btnMostrarFinal.setOnClickListener {
                                val idExamenUsuario = idExamenUsuario
                                val intentos = 3
                                val intentoFinalizar = intentos - 1
                                val estadoFinalizado = 2
                                val tiempoRestante = convertirMilisAString(tiempoRestanteMilisegundos)
                                val tiempoTranscurrido = TimeUnit.MILLISECONDS.toMinutes(tiempoTranscurridoMilisegundos).toInt()
                                finalizarExamen(idExamenUsuario, intentoFinalizar, estadoFinalizado, tiempoRestante, tiempoTranscurrido)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun finalizarExamen(idExamenU: Int,intentos: Int, estado: Int, tiempoRestante:String, time: Int){
        apiServicio.finalizarExamenUsuario(idExamenU,intentos,estado,tiempoRestante,time).enqueue(object : Callback<LoginRespuesta>{
            override fun onResponse(
                call: Call<LoginRespuesta>,
                response: Response<LoginRespuesta>
            ) {
                if (response.isSuccessful){
                    findNavController().navigate(R.id.action_preguntas_to_inicio)
                }
            }

            override fun onFailure(call: Call<LoginRespuesta>, t: Throwable) {
                Toast.makeText(requireContext(), "Fallo: ${t.message}", Toast.LENGTH_SHORT).show()
                Log.e("API Failure", "Error: ${t.message}", t)
            }
        })
    }

    private fun convertirMilisAString(tiempoEnMilisegundos: Long): String {
        val horas = TimeUnit.MILLISECONDS.toHours(tiempoEnMilisegundos)
        val minutos = TimeUnit.MILLISECONDS.toMinutes(tiempoEnMilisegundos) - TimeUnit.HOURS.toMinutes(horas)
        val segundos = TimeUnit.MILLISECONDS.toSeconds(tiempoEnMilisegundos) - TimeUnit.MINUTES.toSeconds(minutos)
        val segundosRestantes = segundos % 60

        return String.format("%02d:%02d:%02d", horas, minutos, segundosRestantes)
    }


    fun eliminarEtiquetasHTML(textoHTML: String): String {
        val documento = Jsoup.parse(textoHTML)
        return documento.text()
    }

    fun limpiarTextoPregunta(textoHTML: String): String  {
        val textoSinHTML = eliminarEtiquetasHTML(textoHTML)

        val indiceEspacio = textoSinHTML.indexOf(' ')


        val textoLimpio = if (indiceEspacio != -1) {
            textoSinHTML.substring(indiceEspacio + 1)
        } else {
            textoSinHTML
        }

        return textoLimpio
    }

    private fun obtenerIndicePreguntaActual(): Int {
        return indicePreguntaActual
    }

    private fun avanzarAPreguntaSiguiente() {
        indicePreguntaActual++
    }

    private fun avanzarASeccionSiguiente() {
        indicePreguntaActual = 0
        indiceSeccionActual++
    }


    override fun onStart() {
        super.onStart()
        (activity as AppCompatActivity).supportActionBar!!.hide()
    }

    override fun onStop() {
        super.onStop()
        (activity as AppCompatActivity).supportActionBar!!.show()
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity).supportActionBar!!.hide()
    }

}