package com.example.examenesseq.fragments.dashboard.usuarios

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.examenesseq.R
import com.example.examenesseq.datos.respuesta.ModuloUsuarioRespuesta
import com.example.examenesseq.model.usuario.Usuario
import com.example.examenesseq.util.PreferenceHelper
import com.example.examenesseq.util.PreferenceHelper.TieneEstadoUser
import com.example.examenesseq.util.PreferenceHelper.TieneUser
import com.example.examenesseq.util.PreferenceHelper.getEstadoUser
import com.example.examenesseq.util.PreferenceHelper.getUser


class UsuariosAdapter(var con: Context, var list: ModuloUsuarioRespuesta): RecyclerView.Adapter<UsuariosAdapter.ViewHolder>() {

    inner class ViewHolder(v: View): RecyclerView.ViewHolder(v){
        var txtNombreUser=v.findViewById<TextView>(R.id.tituloUserTxt)
        var txtCorreoUser=v.findViewById<TextView>(R.id.txtCorreo)
        var EstadoUser=v.findViewById<CardView>(R.id.estadoUser)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view= LayoutInflater.from(con).inflate(R.layout.elementos_usuarios,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.Objeto.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val usuarios= list.Objeto[position]

        val nombreCompleto= usuarios.Nombres + " " + usuarios.Apellido1 + " " + usuarios.Apellido2
        val correoUser=usuarios.CorreoElectronico
        val estadoUser= usuarios.ActivoUsuario

        holder.txtNombreUser.text=nombreCompleto
        holder.txtCorreoUser.text=correoUser

        if (estadoUser == 1) {
            holder.EstadoUser.setCardBackgroundColor(ContextCompat.getColor(con, R.color.green))
        } else {
            holder.EstadoUser.setCardBackgroundColor(ContextCompat.getColor(con, R.color.red))
        }

        holder.itemView.setOnClickListener {
            val usuariosModal = ModalDatosUsuarios(usuarios)
            usuariosModal.show((con as AppCompatActivity).supportFragmentManager, "DatosUsuarioModal")
        }

    }

}