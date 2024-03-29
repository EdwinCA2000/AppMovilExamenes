package com.example.examenesseq.fragments.dashboard.usuarios

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.example.examenesseq.R
import com.example.examenesseq.datos.ApiServicio
import com.example.examenesseq.datos.respuesta.ActualizarUsuarioRespuesta
import com.example.examenesseq.datos.respuesta.RespuestaActivarUser
import com.example.examenesseq.model.usuario.ModuloUsuario
import com.example.examenesseq.util.PreferenceHelper
import com.example.examenesseq.util.PreferenceHelper.TieneEstadoUser
import com.example.examenesseq.util.PreferenceHelper.TieneUser
import com.example.examenesseq.util.PreferenceHelper.getUser
import com.example.examenesseq.util.PreferenceHelper.saveEstadoUser
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.regex.Pattern


class ModalDatosUsuarios(private val usuario: ModuloUsuario) : DialogFragment() {

    private lateinit var txtNombre: EditText
    private lateinit var txtApellido1: EditText
    private lateinit var txtApellido2: EditText
    private lateinit var txtCURP: EditText
    private lateinit var txtCorreo: EditText
    private lateinit var cardEstadoUser: CardView
    private lateinit var btnGuardar: Button
    private lateinit var btnEstado: Button
    private lateinit var llHistorialUser: LinearLayout
    private var estado=-1
    private var modalListener: ModalListener? = null
    private var usuarioEditado: ModuloUsuario? = null

    private val apiServicio: ApiServicio by lazy {
        ApiServicio.create(requireContext())
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.modal_datos_usuario, null)

        txtNombre = view.findViewById(R.id.etNombreAlumno)
        txtApellido1 = view.findViewById(R.id.etApellido1)
        txtApellido2 = view.findViewById(R.id.etApellido2)
        txtCURP = view.findViewById(R.id.etCURP)
        txtCorreo = view.findViewById(R.id.etCorreoElectronico)
        btnGuardar = view.findViewById(R.id.btnGuardar)
        btnEstado = view.findViewById(R.id.btnEstado)
        cardEstadoUser = view.findViewById(R.id.estadoUsuarioCard)
        llHistorialUser= view.findViewById(R.id.llHistorialExamen)

        usuarioEditado = usuario.copy()
        val users = usuarioEditado
        val nombres = users?.Nombres
        val apellido1 = users?.Apellido1
        val apellido2 = users?.Apellido2
        val curp = users?.CURP
        val correo = users?.CorreoElectronico
        if (users != null) {
            estado = users.ActivoUsuario
        }
        //Establecer el estado del usuario con el cardview
        if (estado == 1) {
            btnEstado.text = "DESACTIVAR"
            cardEstadoUser.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.green))
        } else {
            btnEstado.text = "ACTIVAR"
            cardEstadoUser.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.red))
        }
        //Guardar datos del modal al cerrar
        val preferences = PreferenceHelper.defaultPrefs(requireContext())
        if(preferences.TieneUser()){
            val usuarios=preferences.getUser()
            val nombreUser=usuarios?.Nombres
            val ape1=usuarios?.Apellido1
            val ape2=usuarios?.Apellido2
            val curpUser=usuarios?.CURP
            val correoElect=usuarios?.CorreoElectronico

            txtNombre.setText(nombreUser)
            txtApellido1.setText(ape1)
            txtApellido2.setText(ape2)
            txtCURP.setText(curpUser)
            txtCorreo.setText(correoElect)
        }else if (preferences.TieneEstadoUser()){

            if (estado == 1) {
                //setear datos normalmente
                txtNombre.setText(nombres)
                txtApellido1.setText(apellido1)
                txtApellido2.setText(apellido2)
                txtCURP.setText(curp)
                txtCorreo.setText(correo)
                btnEstado.text = "DESACTIVAR"
                cardEstadoUser.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.green))
            } else {
                //setear datos normalmente
                txtNombre.setText(nombres)
                txtApellido1.setText(apellido1)
                txtApellido2.setText(apellido2)
                txtCURP.setText(curp)
                txtCorreo.setText(correo)
                btnEstado.text = "ACTIVAR"
                cardEstadoUser.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.red))
            }
        }else{
            //setear datos normalmente
            txtNombre.setText(nombres)
            txtApellido1.setText(apellido1)
            txtApellido2.setText(apellido2)
            txtCURP.setText(curp)
            txtCorreo.setText(correo)
        }

        //actions
        btnGuardar.setOnClickListener {
                actualizarUsuario()
        }

        btnEstado.setOnClickListener {
            actualizarEstadoUsuario()
        }

        llHistorialUser.setOnClickListener{
            findNavController().navigate(R.id.action_administrar_usuarios_to_historialExamenUsers)
            dialog?.dismiss()
        }


        builder.setView(view)
        return builder.create()
    }

    private fun validarCorreo(email: String): Boolean {
        val pattern: Pattern = Patterns.EMAIL_ADDRESS
        return pattern.matcher(email).matches()
    }

    private fun validarRegistro(): Boolean {
        val curp = txtCURP.text.toString()

        // Validar que la CURP tenga una longitud de 18 caracteres
        if (curp.length != 18) {
            Toast.makeText(requireContext(), "La CURP no es válida", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }


    private fun actualizarUsuario() {
        val nombres = txtNombre.text.toString()
        val apellido1 = txtApellido1.text.toString()
        val apellido2 = txtApellido2.text.toString()
        val curp = txtCURP.text.toString()
        val correo = txtCorreo.text.toString()
        val idUsuario= usuarioEditado?.IdUsuario ?: -1


        if (!validarCorreo(correo)) {
            Toast.makeText(requireContext(),"Por favor, ingresa un correo electrónico válido", Toast.LENGTH_SHORT).show()
            return
        } else if (!validarRegistro()) {
            return
        } else {
            val call = apiServicio.actualizarUsuario(
                idUsuario,
                nombres,
                apellido1,
                apellido2,
                curp,
                correo
            )
            call.enqueue(object : Callback<ActualizarUsuarioRespuesta> {
                override fun onResponse(
                    call: Call<ActualizarUsuarioRespuesta>,
                    response: Response<ActualizarUsuarioRespuesta>
                ) {
                    Log.e("response",response.toString())
                    if (response.isSuccessful) {
                        val usuarioActualizado = response.body()
                        Log.e("usuarioActualizado",usuarioActualizado.toString())
                        if (usuarioActualizado != null) {
                            val userObjeto=usuarioActualizado.Objeto
                                val nombUser = userObjeto.Nombres
                                val ape1 = userObjeto.Apellido1
                                val ape2 = userObjeto.Apellido2
                                val curpUser = userObjeto.CURP
                                val correoEle = userObjeto.CorreoElectronico

                                txtNombre.setText(nombUser)
                                txtApellido1.setText(ape1)
                                txtApellido2.setText(ape2)
                                txtCURP.setText(curpUser)
                                txtCorreo.setText(correoEle)

                                if (estado == 1) {
                                    btnEstado.text = "DESACTIVAR"
                                    cardEstadoUser.setCardBackgroundColor(
                                        ContextCompat.getColor(
                                            requireContext(),
                                            R.color.green
                                        )
                                    )
                                } else {
                                    btnEstado.text = "ACTIVAR"
                                    cardEstadoUser.setCardBackgroundColor(
                                        ContextCompat.getColor(
                                            requireContext(),
                                            R.color.red
                                        )
                                    )
                                }

                            Toast.makeText(requireContext(),"Haz actualizado los datos correctamente", Toast.LENGTH_SHORT).show()
                            dismiss()
                        }
                    } else {
                        Toast.makeText(requireContext(),"Ocurrio un fallo al actualizar los datos", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ActualizarUsuarioRespuesta>, t: Throwable) {
                    Toast.makeText(requireContext(), "Fallo: ${t.message}", Toast.LENGTH_SHORT).show()
                    Log.e("API Failure", "Error: ${t.message}", t)
                }
            })
        }

    }

    private fun actualizarEstadoUsuario() {
        val activoUsuario = if (btnEstado.text == "DESACTIVAR") 0 else 1
        val idUsuario=usuario.IdUsuario

        val call = apiServicio.actualizarEstadoUser(idUsuario, activoUsuario)
        call.enqueue(object : Callback<RespuestaActivarUser> {
            override fun onResponse(
                call: Call<RespuestaActivarUser>,
                response: Response<RespuestaActivarUser>
            ) {
                if (response.isSuccessful) {
                    val usuarioActualizado = response.body()
                    if (usuarioActualizado != null) {

                        val activoUser = usuarioActualizado.Objeto
                        val preferences = PreferenceHelper.defaultPrefs(requireContext())
                        preferences.saveEstadoUser(activoUser)
                        if (activoUser == 0) {
                            btnEstado.text = "DESACTIVAR"
                            cardEstadoUser.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.green))
                            Toast.makeText(requireContext(), "Haz desactivado el usuario", Toast.LENGTH_SHORT).show()
                        } else {
                            btnEstado.text = "ACTIVAR"
                            cardEstadoUser.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.red))
                            Toast.makeText(requireContext(), "Haz activado el usuario", Toast.LENGTH_SHORT).show()
                        }

                        dismiss()
                    }
                } else {
                    Toast.makeText(requireContext(),"Ocurrió un fallo al actualizar el estado del usuario", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<RespuestaActivarUser>, t: Throwable) {
                Toast.makeText(requireContext(), "No se logró actualizar el estado del usuario debido al servidor", Toast.LENGTH_SHORT).show()
            }
        })
    }
    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        modalListener?.onModalClosed(usuario.IdUsuario)
    }


    interface ModalListener {
        fun onModalClosed(userId: Int)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            modalListener = context as ModalListener
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement ModalListener")
        }
    }


}
