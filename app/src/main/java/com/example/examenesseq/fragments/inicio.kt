package com.example.examenesseq.fragments

import Examen
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
import com.example.examenesseq.R
import com.example.examenesseq.databinding.FragmentInicioBinding
import com.example.examenesseq.datos.ApiServicio
import com.example.examenesseq.model.usuario.Identidad
import com.example.examenesseq.util.PreferenceHelper
import com.example.examenesseq.util.PreferenceHelper.saveExamen
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentInicioBinding.inflate(inflater, container, false)

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
                    // Hacer lo que necesites con la lista de exámenes
                    if (examenes != null) {
                        for (examen in examenes) {
                            val idExamen = examen.IdExamen
                            val titulo = examen.TituloExamen
                            val descripcion = examen.DescripcionExamen
                            val fechaInicio = examen.FechaInicio
                            val fechaFinal = examen.FechaFinal
                            val tiempoExamen = examen.TiempoExamen
                            val estadoExamen = examen.EstadoExamen
                            preferences.saveExamen(examen)
                        }
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


    private fun obtenerDatosSesion() {

        apiServicio.getDatosSesion().enqueue(object : Callback<Identidad> {
            override fun onResponse(call: Call<Identidad>, response: Response<Identidad>) {
                if (response.isSuccessful) {
                    val identidad = response.body()
                    val preferences = PreferenceHelper.defaultPrefs(requireContext())
                    val jsessionid = response.headers()["Set-Cookie"] ?: ""
                    Log.d("JSESSIONID", jsessionid)
                    preferences.setJSessionId(jsessionid)
                    val prueba = identidad?.IdUsuario
                    binding.textView.text=prueba.toString()
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