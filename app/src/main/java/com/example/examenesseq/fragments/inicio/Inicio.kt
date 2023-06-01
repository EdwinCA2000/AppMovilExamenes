package com.example.examenesseq.fragments.inicio

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.examenesseq.R
import com.example.examenesseq.databinding.FragmentInicioBinding
import com.example.examenesseq.datos.ApiServicio
import com.example.examenesseq.fragments.inicio.examenadapter.ExamenAdapter
import com.example.examenesseq.model.examen.Examen
import com.example.examenesseq.model.examen.ExamenUsuario
import com.example.examenesseq.model.examen.Secciones
import com.example.examenesseq.model.usuario.Identidad
import com.example.examenesseq.util.PreferenceHelper
import com.example.examenesseq.util.PreferenceHelper.TieneExamenes
import com.example.examenesseq.util.PreferenceHelper.getExamenes
import com.example.examenesseq.util.PreferenceHelper.getIdentidad
import com.example.examenesseq.util.PreferenceHelper.getJSessionId
import com.example.examenesseq.util.PreferenceHelper.saveExamenes
import com.example.examenesseq.util.PreferenceHelper.saveExamenesUsuario
import com.example.examenesseq.util.PreferenceHelper.saveSecciones
import com.example.examenesseq.util.PreferenceHelper.setJSessionId
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class Inicio : Fragment() {

    private var _binding: FragmentInicioBinding? = null
    private val binding get() = _binding!!

    private val apiServicio: ApiServicio by lazy {
        ApiServicio.create(requireContext())
    }

    private lateinit var examenAdapter: ExamenAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentInicioBinding.inflate(inflater, container, false)
        (requireActivity() as AppCompatActivity).supportActionBar?.show()
        binding.listaExamenes.layoutManager = LinearLayoutManager(requireContext())

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            mostrarDialogoCerrarSesion()
        }
        super.onViewCreated(view, savedInstanceState)
        val menuHost: MenuHost = requireActivity()

        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_inicio, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    R.id.ver_perfil -> irAPerfil()
                    R.id.historial_examenes -> irAHistorial()
                    R.id.cerrar_sesion -> mostrarDialogoCerrarSesion()
                    android.R.id.home -> requireActivity().onBackPressedDispatcher.onBackPressed()
                }

                return true
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
        obtenerDatosSesion()
        obtenerExamenesDisponibles()
        obtenerSeccionesExamen()
    }


    private fun obtenerExamenesDisponibles() {
        apiServicio.getExamenesDisponibles().enqueue(object : Callback<List<Examen>> {
            override fun onResponse(call: Call<List<Examen>>, response: Response<List<Examen>>) {
                if (response.isSuccessful) {
                    val examenes = response.body()
                    val preferences = PreferenceHelper.defaultPrefs(requireContext())
                    val jsessionid = response.headers()["Set-Cookie"] ?: ""
                    Log.d("JSESSIONID", jsessionid)
                    preferences.setJSessionId(jsessionid)
                    if (!examenes.isNullOrEmpty()) {
                        preferences.saveExamenes(examenes)
                        obtenerDatosDeExamenUsuario(examenes)
                    }else if (preferences.TieneExamenes()) {
                        val examenes = preferences.getExamenes()
                        preferences.getJSessionId()
                        if (examenes != null) {
                            obtenerDatosDeExamenUsuario(examenes)
                        }
                    } else {
                        // No existen examenes disponibles
                        binding.txtnoExamenesDisponibles.visibility = View.VISIBLE
                        binding.imgNoExamenesDisponibles.visibility = View.VISIBLE
                    }
                } else {
                    Toast.makeText(requireContext(), "Hubo un error en la respuesta del servidor para obtener los examenes", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call <List<Examen>>, t: Throwable) {
                Toast.makeText(requireContext(), "Fallo: ${t.message}", Toast.LENGTH_SHORT).show()
                Log.e("API Failure", "Error: ${t.message}", t)
            }

        })
    }


    private fun obtenerDatosDeExamenUsuario(examenes: List<Examen>) {
        val preferences = PreferenceHelper.defaultPrefs(requireContext())
        val idUsuario= preferences.getIdentidad()?.IdUsuario
        if (idUsuario != null) {
            apiServicio.obtenerExamenUsuario(idUsuario).enqueue(object : Callback<List<ExamenUsuario>> {
                override fun onResponse(call: Call<List<ExamenUsuario>>, response: Response<List<ExamenUsuario>>) {
                    if (response.isSuccessful) {
                        val exameneusuario = response.body()
                        val jsessionid = response.headers()["Set-Cookie"] ?: ""
                        Log.d("JSESSIONID", jsessionid)
                        preferences.setJSessionId(jsessionid)
                        if (!exameneusuario.isNullOrEmpty()) {
                            examenAdapter = ExamenAdapter(requireContext(), examenes, exameneusuario)
                            binding.listaExamenes.adapter = examenAdapter
                            preferences.saveExamenesUsuario(exameneusuario)
                        }else{
                            examenAdapter = ExamenAdapter(requireContext(), examenes, emptyList())
                            binding.listaExamenes.adapter = examenAdapter
                        }
                    } else {
                        Toast.makeText(requireContext(), "Hubo error en la respuesta del servidor", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call <List<ExamenUsuario>>, t: Throwable) {
                    Toast.makeText(requireContext(), "Fallo: ${t.message}", Toast.LENGTH_SHORT).show()
                    Log.e("API Failure", "Error: ${t.message}", t)
                }

            })
        }
    }


    private fun obtenerSeccionesExamen(){
        apiServicio.getSeccionesExamen().enqueue(object : Callback<List<Secciones>> {
            override fun onResponse(call: Call<List<Secciones>>, response: Response<List<Secciones>>) {
                if (response.isSuccessful) {
                    val secciones = response.body()
                    val preferences = PreferenceHelper.defaultPrefs(requireContext())
                    val jsessionid = response.headers()["Set-Cookie"] ?: ""
                    Log.d("JSESSIONID", jsessionid)
                    preferences.setJSessionId(jsessionid)
                    if (!secciones.isNullOrEmpty()) {
                        preferences.saveSecciones(secciones)

                    } else {
                        // No existen examenes disponibles
                        binding.txtnoExamenesDisponibles.visibility = View.VISIBLE
                        binding.imgNoExamenesDisponibles.visibility = View.VISIBLE
                    }
                } else {
                    Toast.makeText(requireContext(), "Hubo un error en la respuesta del servidor para obtener las secciones", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call <List<Secciones>>, t: Throwable) {
                Toast.makeText(requireContext(), "Fallo: ${t.message}", Toast.LENGTH_SHORT).show()
                Log.e("API Failure", "Error: ${t.message}", t)
            }

        })
    }



    private fun obtenerDatosSesion() {

        apiServicio.getDatosSesion().enqueue(object : Callback<Identidad> {
            override fun onResponse(call: Call<Identidad>, response: Response<Identidad>) {
                if (response.isSuccessful) {
                    val identidad = response.body()
                    val preferences = PreferenceHelper.defaultPrefs(requireContext())
                    val jsessionid = response.headers()["Set-Cookie"] ?: ""
                    Log.d("JSESSIONID", jsessionid)
                    preferences.setJSessionId(jsessionid)
                } else {
                    Toast.makeText(requireContext(), "Hubo un error en la respuesta del servidor para obtener la cantidad de preguntas", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Identidad>, t: Throwable) {
                Toast.makeText(requireContext(), "Hubo una falla en el servidor", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun mostrarDialogoCerrarSesion() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Confirmar Cerrar sesión")
            .setMessage("¿Desea cerrar sesión?")
            .setPositiveButton("Si") { dialog, _ ->
                cerrarSesionUser()
                dialog.dismiss()
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    fun cerrarSesionUser(){
        apiServicio.cerrarSesion().enqueue(object : Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                if (response.isSuccessful) {
                    irALogin()
                    (activity as AppCompatActivity).supportActionBar?.hide()
                } else {
                    Toast.makeText(requireContext(), "No se logro cerrar sesión por el servidor", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Unit>, t: Throwable) {
                Toast.makeText(requireContext(), "Ocurrio un fallo en el servidor al cerrar sesión", Toast.LENGTH_SHORT).show()
            }
        })
    }
    fun irAHistorial(){
        findNavController().navigate(R.id.action_inicio_to_historial_examenes2)
    }
    fun irALogin(){
        findNavController().navigate(R.id.action_inicio_to_login)
    }
    fun irAPerfil(){
        findNavController().navigate(R.id.action_inicio_to_perfil_usuario)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}