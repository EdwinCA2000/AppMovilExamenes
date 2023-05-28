package com.example.examenesseq.fragments.inicio

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
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.examenesseq.R
import com.example.examenesseq.databinding.FragmentInicioBinding
import com.example.examenesseq.datos.ApiServicio
import com.example.examenesseq.fragments.inicio.examenadapter.ExamenUsuarioAdapter
import com.example.examenesseq.fragments.inicio.examenadapter.examenAdapter
import com.example.examenesseq.model.examen.Examen
import com.example.examenesseq.model.examen.ExamenUsuario
import com.example.examenesseq.model.usuario.Identidad
import com.example.examenesseq.util.PreferenceHelper
import com.example.examenesseq.util.PreferenceHelper.getIdentidad
import com.example.examenesseq.util.PreferenceHelper.saveExamenes
import com.example.examenesseq.util.PreferenceHelper.saveExamenesUsuario
import com.example.examenesseq.util.PreferenceHelper.setJSessionId
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class inicio : Fragment() {

    private var _binding: FragmentInicioBinding? = null
    private val binding get() = _binding!!

    private val apiServicio: ApiServicio by lazy {
        ApiServicio.create(requireContext())
    }

    private lateinit var examenAdapter: examenAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentInicioBinding.inflate(inflater, container, false)
        binding.listaExamenes.layoutManager = LinearLayoutManager(requireContext())

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val menuHost: MenuHost = requireActivity()

        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_inicio, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    R.id.ver_perfil -> irAPerfil()
                    android.R.id.home -> requireActivity().onBackPressedDispatcher.onBackPressed()
                }

                return true
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
        obtenerDatosSesion()
        obtenerExamenesDisponibles()
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
                    }else{
                        binding.txtnoExamenesDisponibles.visibility=View.VISIBLE
                        binding.imgNoExamenesDisponibles.visibility=View.VISIBLE
                    }
                } else {
                    // Manejar error de respuesta
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
                            examenAdapter = examenAdapter(requireContext(), examenes, exameneusuario)
                            binding.listaExamenes.adapter = examenAdapter
                            preferences.saveExamenesUsuario(exameneusuario)
                        }else{

                        }
                    } else {
                        // Manejar error de respuesta
                    }
                }

                override fun onFailure(call: Call <List<ExamenUsuario>>, t: Throwable) {
                    Toast.makeText(requireContext(), "Fallo: ${t.message}", Toast.LENGTH_SHORT).show()
                    Log.e("API Failure", "Error: ${t.message}", t)
                }

            })
        }
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
                    // Manejar error de respuesta
                }
            }

            override fun onFailure(call: Call<Identidad>, t: Throwable) {
                // Manejar error de solicitud
            }
        })
    }


    fun irAPerfil(){
        findNavController().navigate(R.id.action_inicio_to_perfil_usuario)
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}