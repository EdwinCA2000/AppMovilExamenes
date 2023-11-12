package com.example.examenesseq.fragments.dashboard.usuarios

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.examenesseq.R
import com.example.examenesseq.datos.respuesta.ModuloUsuarioRespuesta

class UsuariosAdapter(var con: Context, var list: ModuloUsuarioRespuesta): RecyclerView.Adapter<UsuariosAdapter.ViewHolder>() {

    inner class ViewHolder(v: View): RecyclerView.ViewHolder(v){
        var txtNombreUser=v.findViewById<TextView>(R.id.tituloUserTxt)
        var txtCorreoUser=v.findViewById<TextView>(R.id.txtCorreo)
        var llEstadoUser=v.findViewById<LinearLayout>(R.id.llAdminUsuarios)
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
            holder.llEstadoUser.setBackgroundResource(R.drawable.border_stroke_activo)
        } else if (estadoUser == 0) {
            holder.llEstadoUser.setBackgroundResource(R.drawable.border_stroke_inactivo)
        }
        holder.itemView.setOnClickListener {
            val usuariosModal = ModalDatosUsuarios(usuarios)
            usuariosModal.show((con as AppCompatActivity).supportFragmentManager, "DatosUsuarioModal")
        }

    }

}