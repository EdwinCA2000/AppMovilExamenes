package com.example.examenesseq.fragments.dashboard


import android.app.AlertDialog
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.navigation.fragment.findNavController
import com.example.examenesseq.R
import com.example.examenesseq.databinding.FragmentDashboardBinding
import com.example.examenesseq.datos.ApiServicio
import com.example.examenesseq.datos.respuesta.LoginRespuesta
import com.example.examenesseq.model.examen.ExamenUsuario
import com.example.examenesseq.util.PreferenceHelper
import com.example.examenesseq.util.PreferenceHelper.TieneExamenCompletados
import com.example.examenesseq.util.PreferenceHelper.TieneExamenesUsuario
import com.example.examenesseq.util.PreferenceHelper.TieneUsuarios
import com.example.examenesseq.util.PreferenceHelper.getExamenesUsuario
import com.example.examenesseq.util.PreferenceHelper.getIdentidad
import com.example.examenesseq.util.PreferenceHelper.getTotalExamenComple
import com.example.examenesseq.util.PreferenceHelper.getTotalUser
import com.example.examenesseq.util.PreferenceHelper.saveExamenesUsuario
import com.example.examenesseq.util.PreferenceHelper.setTotalExamenesComple
import com.example.examenesseq.util.PreferenceHelper.setTotalUser
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Dashboard : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!


    private val apiServicio: ApiServicio by lazy {
        ApiServicio.create(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        (requireActivity() as AppCompatActivity).supportActionBar?.hide()
        val preferences = PreferenceHelper.defaultPrefs(requireContext())
        val identidadAdmin=preferences.getIdentidad()
        Log.e("identidad",identidadAdmin.toString())
        val nombreCompletoAdmin= identidadAdmin!!.Nombres + " " + identidadAdmin.Apellido1 + " " + identidadAdmin.Apellido2
        binding.nombreAdmintxt.text=nombreCompletoAdmin
        obtenerTotalUsers()
        obtenerTotalExamenesCompletados()
        obtenerExamenesUsers()


        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            mostrarDialogoCerrarSesion()
        }

        irAUsers()
        irACrearExamen()
        irAdministrarExamenes()
        irAReportes()
        return binding.root
    }
    private fun isNetworkConnected(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val network = connectivityManager.activeNetwork
            val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
            return networkCapabilities != null && (networkCapabilities.hasTransport(
                NetworkCapabilities.TRANSPORT_CELLULAR) || networkCapabilities.hasTransport(
                NetworkCapabilities.TRANSPORT_WIFI))
        } else {
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            return activeNetworkInfo != null && activeNetworkInfo.isConnected
        }
    }

    fun obtenerTotalUsers(){
        if (!isNetworkConnected(requireContext())) {
            // No hay conexión a Internet, muestra un fragmento de error por falta de conexión.
            findNavController().navigate(R.id.action_dashboard_to_noHayInternet)
        }else{
            val preferences = PreferenceHelper.defaultPrefs(requireContext())
            apiServicio.obtenerTotalUsuariosRegistrados().enqueue(object: Callback <LoginRespuesta>{
                override fun onResponse(
                    call: Call<LoginRespuesta>,
                    response: Response<LoginRespuesta>
                ) {
                    if (response.isSuccessful){
                        val totalUsers=response.body()

                        if (totalUsers!=null){
                            val totalUsuarios= totalUsers.Mensaje
                            binding.txtUserRegistrados.text=totalUsuarios
                            preferences.setTotalUser(totalUsuarios)
                        }else if (preferences.TieneUsuarios()){
                            val totalUsuarios=preferences.getTotalUser()
                            binding.txtUserRegistrados.text=totalUsuarios
                        }else{
                        }
                    }
                }

                override fun onFailure(call: Call<LoginRespuesta>, t: Throwable) {
                    findNavController().navigate(R.id.action_dashboard_to_falloServidor)
                }

            })
        }

    }

    fun obtenerTotalExamenesCompletados(){
        val preferences = PreferenceHelper.defaultPrefs(requireContext())
        apiServicio.obtenerTotalExamenCompletados().enqueue(object: Callback <LoginRespuesta>{
            override fun onResponse(
                call: Call<LoginRespuesta>,
                response: Response<LoginRespuesta>
            ) {
                if (response.isSuccessful){
                    val totalExamenes=response.body()
                    if (totalExamenes!=null){
                        val totalExamenesComple= totalExamenes.Mensaje
                        binding.txtExamenesComple.text=totalExamenesComple
                        preferences.setTotalExamenesComple(totalExamenesComple)
                    }else if (preferences.TieneExamenCompletados()){
                        val totalExamenesComple=preferences.getTotalExamenComple()
                        binding.txtExamenesComple.text=totalExamenesComple
                    }else{
                    }
                }
            }

            override fun onFailure(call: Call<LoginRespuesta>, t: Throwable) {
            }

        })
    }

    fun obtenerExamenesUsers(){
        val preferences = PreferenceHelper.defaultPrefs(requireContext())
        apiServicio.obtenerExamenesUsuario().enqueue(object: Callback <List<ExamenUsuario>>{
            override fun onResponse(
                call: Call<List<ExamenUsuario>>,
                response: Response<List<ExamenUsuario>>
            ) {
                if (response.isSuccessful){
                    val totalExamenesUsuario=response.body()
                    if (totalExamenesUsuario!=null){
                        preferences.saveExamenesUsuario(totalExamenesUsuario)
                        binding.txtPromedioCalif.text=obtenerPromedioCalif(totalExamenesUsuario).toString()
                    }else if (preferences.TieneExamenesUsuario()){
                        val totalExamenesUser=preferences.getExamenesUsuario()
                        binding.txtPromedioCalif.text= totalExamenesUser?.let {
                            obtenerPromedioCalif(
                                it
                            ).toString()
                        }
                    }else{
                    }
                }
            }

            override fun onFailure(call: Call<List<ExamenUsuario>>, t: Throwable) {
                Toast.makeText(requireContext(), "Ocurrio un error en el servidor para obtener los examenes completados", Toast.LENGTH_SHORT).show()
            }

        })
    }

    fun obtenerPromedioCalif(examenUsuario: List<ExamenUsuario>): Int {
        val cantidad=examenUsuario.size
        var calificacion=0
        var promedio=0
        for (examenes in examenUsuario){
            calificacion+=examenes.TotalCalificacion
            promedio=calificacion/cantidad
        }
        return promedio
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
                    val preferences = PreferenceHelper.defaultPrefs(requireContext())
                    preferences.edit {
                        remove("JSESSIONID")
                        apply()
                    }
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

    fun irALogin(){
        findNavController().navigate(R.id.action_dashboard_to_login2)
    }

    fun irAUsers(){
        binding.llUsuarios.setOnClickListener {
            findNavController().navigate(R.id.action_dashboard_to_administrar_usuarios)
        }
    }

    fun irACrearExamen(){
        binding.llCrearExamen.setOnClickListener {
            findNavController().navigate(R.id.action_dashboard_to_crearExamen1)
        }
    }

    fun irAdministrarExamenes(){
        binding.llExamenes.setOnClickListener{
            findNavController().navigate(R.id.action_dashboard_to_administrarExamenes)
        }
    }

    fun irAReportes(){
        binding.llReportes.setOnClickListener{
            findNavController().navigate(R.id.action_dashboard_to_reportes)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}