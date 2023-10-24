package com.example.examenesseq

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.examenesseq.model.examen.PreguntasExamen

class PreguntasAdapter(private val preguntas: List<PreguntasExamen>) : RecyclerView.Adapter<PreguntasAdapter.PreguntaViewHolder>() {
    private var preguntaActual = 0 // Rastrea la pregunta actual

    class PreguntaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val preguntaText: TextView = itemView.findViewById(R.id.preguntaTxt)
        val respuesta1Button: Button = itemView.findViewById(R.id.btnRespuesta1)
        // Agregar otras vistas de respuesta según sea necesario
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PreguntaViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.elementopregunta, parent, false)
        return PreguntaViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PreguntaViewHolder, position: Int) {
        val pregunta = preguntas[position]

        // Configurar las vistas de pregunta individual con los datos de 'pregunta'
        holder.preguntaText.text = pregunta.contenidoPregunta

        // Agregar la lógica para manejar el clic en el botón de respuesta
        holder.respuesta1Button.setOnClickListener {
            // Realiza la lógica para procesar la respuesta seleccionada

            // Incrementa el índice de la pregunta para mostrar la siguiente pregunta
            preguntaActual++

            // Asegúrate de que no te excedas del número de preguntas disponibles
            if (preguntaActual < preguntas.size) {
                // Notifica al RecyclerView para que actualice la vista con la siguiente pregunta
                notifyItemChanged(preguntaActual)
            } else {
                // Has llegado al final de las preguntas, puedes realizar una acción adicional o mostrar un mensaje
            }
        }
    }

    override fun getItemCount(): Int {
        return preguntas.size
    }
}


