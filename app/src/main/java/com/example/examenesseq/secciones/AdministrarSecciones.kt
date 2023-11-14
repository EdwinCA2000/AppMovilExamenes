package com.example.examenesseq.secciones

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.examenesseq.secciones.adapter.AdminSeccionesAdapter
import com.example.examenesseq.secciones.viewmodel.IdExamenViewModel
import com.example.examenesseq.R
import com.example.examenesseq.secciones.viewmodel.SeccionesViewModel
import com.example.examenesseq.databinding.FragmentAdministrarSeccionesBinding
import com.example.examenesseq.datos.ApiServicio
import com.example.examenesseq.datos.respuesta.RespuestaActivarSeccion
import com.example.examenesseq.datos.respuesta.RespuestaEliminarSeccion
import com.example.examenesseq.model.examen.Secciones
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdministrarSecciones : Fragment(), AdminSeccionesAdapter.OnItemClickListener {

    private var selectedPosition: Int? = null

    private var _binding: FragmentAdministrarSeccionesBinding? = null
    private val binding get() = _binding!!

    private val apiServicio: ApiServicio by lazy {
        ApiServicio.create(requireContext())
    }
    private lateinit var idExamenViewModel: IdExamenViewModel
    private lateinit var seccionesViewModel: SeccionesViewModel
    private lateinit var adminSeccionesAdapter: AdminSeccionesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentAdministrarSeccionesBinding.inflate(inflater, container, false)
        seccionesViewModel = ViewModelProvider(requireActivity())[SeccionesViewModel::class.java]
        idExamenViewModel = ViewModelProvider(requireActivity())[IdExamenViewModel::class.java]
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().navigate(R.id.action_administrarSecciones_to_administrarExamenes)
        }
        binding.listaSeccionesAdmin.layoutManager = LinearLayoutManager(requireContext())

        val idExamen=idExamenViewModel.idExamen
        obtenerSecciones(idExamen)
        binding.fabAgregarSeccion.setOnClickListener{
            findNavController().navigate(R.id.action_administrarSecciones_to_agregarSeccion)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            0,  ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false // No permitir el movimiento de elementos
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                // Eliminar el elemento cuando se realiza un deslizamiento
                val position = viewHolder.absoluteAdapterPosition
                eliminarSeccion(position)
            }
        })

        // Vincular el ItemTouchHelper con el RecyclerView
        itemTouchHelper.attachToRecyclerView(binding.listaSeccionesAdmin)
    }

    private fun eliminarSeccion(idSeccion: Int){
        val seccionAeliminar = adminSeccionesAdapter.list[idSeccion]
        apiServicio.deleteSeccion(seccionAeliminar.IdSeccion).enqueue(object : Callback<RespuestaEliminarSeccion>{
            override fun onResponse(
                call: Call<RespuestaEliminarSeccion>,
                response: Response<RespuestaEliminarSeccion>
            ) {
                if (response.isSuccessful){
                    val respuesta=response.body()
                    if (respuesta != null) {
                        val nuevaLista = adminSeccionesAdapter.list.toMutableList()
                        nuevaLista.removeAt(idSeccion)

                        // Actualizar la lista en el adaptador
                        adminSeccionesAdapter.list = nuevaLista

                        // Notificar al adaptador sobre el cambio
                        adminSeccionesAdapter.notifyItemRemoved(idSeccion)

                        Toast.makeText(requireContext(),"Se eliminó la sección ${seccionAeliminar.TituloSeccion}",Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<RespuestaEliminarSeccion>, t: Throwable) {
                Toast.makeText(requireContext(),"No se logró conectar al servidor para eliminar el usuario",Toast.LENGTH_SHORT).show()
            }

        })

    }

    private fun obtenerSecciones(idExamen: Int){
        apiServicio.getSeccionesDeExamen(idExamen).enqueue(object : Callback<List<Secciones>> {
            override fun onResponse(
                call: Call<List<Secciones>>,
                response: Response<List<Secciones>>
            ) {
                if (response.isSuccessful){
                    val respuesta=response.body()
                    adminSeccionesAdapter = AdminSeccionesAdapter(requireContext(), respuesta!!)
                    binding.listaSeccionesAdmin.adapter = adminSeccionesAdapter
                    adminSeccionesAdapter.setOnItemClickListener(this@AdministrarSecciones)
                }
            }

            override fun onFailure(call: Call<List<Secciones>>, t: Throwable) {
                Toast.makeText(requireContext(), "Fallo: ${t.message}", Toast.LENGTH_SHORT).show()
                Log.e("API Failure", "Error: ${t.message}", t)
            }

        })
    }

    private fun activarDesactivarSeccion(idSeccion: Int,activo: Int){
        apiServicio.activarDesactivarSeccion(idSeccion,activo).enqueue(object : Callback<RespuestaActivarSeccion>{
            override fun onResponse(
                call: Call<RespuestaActivarSeccion>,
                response: Response<RespuestaActivarSeccion>
            ) {
                if (response.isSuccessful){
                    if(activo==0){
                        Toast.makeText(requireContext(), "Haz desactivado la sección", Toast.LENGTH_SHORT).show()
                    }else{
                        Toast.makeText(requireContext(), "Haz activado la sección", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<RespuestaActivarSeccion>, t: Throwable) {
                Toast.makeText(requireContext(), "Fallo: ${t.message}", Toast.LENGTH_SHORT).show()
                Log.e("API Failure", "Error: ${t.message}", t)
            }

        })
    }

    override fun onItemClick(position: Int, seccionesData: Secciones) {
        var activo = -1

        if (selectedPosition == position) {
            // Ya está seleccionado, deseleccionar
            selectedPosition = null
            binding.btnEditarSeccion.visibility = View.GONE
            binding.btnPreguntas.visibility = View.GONE
            binding.btnActivarDesactivar.visibility = View.GONE
        } else {
            // Seleccionar el nuevo elemento
            selectedPosition = position
            binding.btnEditarSeccion.visibility = View.VISIBLE
            binding.btnPreguntas.visibility = View.VISIBLE
            binding.btnActivarDesactivar.visibility = View.VISIBLE

            binding.btnEditarSeccion.setOnClickListener {
                seccionesViewModel.selectedSeccionData = seccionesData
                findNavController().navigate(R.id.action_administrarSecciones_to_editarSeccion)
            }

            if (seccionesData.Activo == 1) {
                activo = 0
                binding.btnActivarDesactivar.setImageResource(R.drawable.off_button)
            } else {
                activo = 1
                binding.btnActivarDesactivar.setImageResource(R.drawable.on_button)
            }

            binding.btnActivarDesactivar.setOnClickListener {
                if (seccionesData.Activo == 1) {
                    activo = 0
                } else {
                    activo = 1
                }
                // Aquí llamamos a activarDesactivarSeccion después de cambiar la imagen del botón
                if (activo == 0) {
                    binding.btnActivarDesactivar.setImageResource(R.drawable.on_button)
                } else {
                    binding.btnActivarDesactivar.setImageResource(R.drawable.off_button)
                }

                adminSeccionesAdapter.list[position].Activo = activo

                // Notificar al adaptador sobre el cambio en el ítem específico
                adminSeccionesAdapter.notifyItemChanged(position)

                activarDesactivarSeccion(seccionesData.IdSeccion, activo)
            }

            binding.btnPreguntas.setOnClickListener {
                //idExamenViewModel.idExamen=examenData.IdExamen
                //findNavController().navigate(R.id.action_administrarExamenes_to_administrarSecciones)
            }
        }

        // Notificar al adaptador sobre el cambio en la selección
        adminSeccionesAdapter.setSelectedPosition(selectedPosition)
    }

}