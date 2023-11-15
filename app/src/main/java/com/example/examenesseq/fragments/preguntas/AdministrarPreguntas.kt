package com.example.examenesseq.fragments.preguntas

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.examenesseq.fragments.preguntas.adapter.AdminPreguntasAdapter
import com.example.examenesseq.fragments.preguntas.viewmodel.IdSeccionViewModel
import com.example.examenesseq.fragments.preguntas.data.Pregunta
import com.example.examenesseq.fragments.preguntas.viewmodel.PreguntasViewModel
import com.example.examenesseq.R
import com.example.examenesseq.databinding.FragmentAdministrarPreguntasBinding
import com.example.examenesseq.datos.ApiServicio
import com.example.examenesseq.datos.respuesta.RespuestaActivarPregunta
import com.example.examenesseq.datos.respuesta.RespuestaEliminarPregunta
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class AdministrarPreguntas : Fragment(), AdminPreguntasAdapter.OnItemClickListener,
    ModalPreguntas.OnPreguntaChangedListener {

    private var selectedPosition: Int? = null

    private var _binding: FragmentAdministrarPreguntasBinding? = null
    private val binding get() = _binding!!

    private val apiServicio: ApiServicio by lazy {
        ApiServicio.create(requireContext())
    }
    private lateinit var idSeccionViewModel: IdSeccionViewModel
    private lateinit var preguntasViewModel: PreguntasViewModel
    private lateinit var adminPreguntasAdapter: AdminPreguntasAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentAdministrarPreguntasBinding.inflate(inflater, container, false)
        idSeccionViewModel = ViewModelProvider(requireActivity())[IdSeccionViewModel::class.java]
        preguntasViewModel=ViewModelProvider(requireActivity())[PreguntasViewModel::class.java]
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().navigate(R.id.action_administrarPreguntas_to_administrarSecciones)
        }

        binding.listaPreguntasAdmin.layoutManager = LinearLayoutManager(requireContext())

        val idSeccion=idSeccionViewModel.idSeccion
        obtenerPreguntas(idSeccion)
        binding.fabAgregarPregunta.setOnClickListener{
            val usuariosModal = ModalPreguntas(idSeccion,null,this)
            usuariosModal.show((requireContext() as AppCompatActivity).supportFragmentManager, "DatosUsuarioModal")
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
                eliminarPregunta(position)
            }
        })

        // Vincular el ItemTouchHelper con el RecyclerView
        itemTouchHelper.attachToRecyclerView(binding.listaPreguntasAdmin)
    }

    private fun obtenerPreguntas(idSeccion: Int){
        apiServicio.getPreguntasDeSeccion(idSeccion).enqueue(object : Callback<List<Pregunta>> {
            override fun onResponse(
                call: Call<List<Pregunta>>,
                response: Response<List<Pregunta>>
            ) {
                if (response.isSuccessful){
                    val respuesta=response.body()
                    adminPreguntasAdapter = AdminPreguntasAdapter(requireContext(), respuesta!!)
                    binding.listaPreguntasAdmin.adapter = adminPreguntasAdapter
                    adminPreguntasAdapter.setOnItemClickListener(this@AdministrarPreguntas)
                }
            }

            override fun onFailure(call: Call<List<Pregunta>>, t: Throwable) {
                Toast.makeText(requireContext(), "Fallo: ${t.message}", Toast.LENGTH_SHORT).show()
                Log.e("API Failure", "Error: ${t.message}", t)
            }

        })
    }

    private fun eliminarPregunta(idPregunta: Int){
        val preguntaAeliminar = adminPreguntasAdapter.list[idPregunta]
        apiServicio.deletePregunta(preguntaAeliminar.IdPregunta).enqueue(object : Callback<RespuestaEliminarPregunta>{
            override fun onResponse(
                call: Call<RespuestaEliminarPregunta>,
                response: Response<RespuestaEliminarPregunta>
            ) {
                if (response.isSuccessful){
                    val respuesta=response.body()
                    if (respuesta != null) {
                        val nuevaLista = adminPreguntasAdapter.list.toMutableList()
                        nuevaLista.removeAt(idPregunta)

                        // Actualizar la lista en el adaptador
                        adminPreguntasAdapter.list = nuevaLista

                        // Notificar al adaptador sobre el cambio
                        adminPreguntasAdapter.notifyItemRemoved(idPregunta)

                        Toast.makeText(requireContext(),"Se eliminó la pregunta ${preguntaAeliminar.Numero}",Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<RespuestaEliminarPregunta>, t: Throwable) {
                Toast.makeText(requireContext(),"No se logró conectar al servidor para eliminar la pregunta",Toast.LENGTH_SHORT).show()
            }

        })

    }

    private fun activarDesactivarPregunta(idPregunta: Int,activo: Int){
        apiServicio.activarDesactivarPregunta(idPregunta,activo).enqueue(object : Callback<RespuestaActivarPregunta>{
            override fun onResponse(
                call: Call<RespuestaActivarPregunta>,
                response: Response<RespuestaActivarPregunta>
            ) {
                if (response.isSuccessful){
                    if(activo==0){
                        Toast.makeText(requireContext(), "Haz desactivado la pregunta", Toast.LENGTH_SHORT).show()
                    }else{
                        Toast.makeText(requireContext(), "Haz activado la pregunta", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<RespuestaActivarPregunta>, t: Throwable) {
                Toast.makeText(requireContext(), "Fallo: ${t.message}", Toast.LENGTH_SHORT).show()
                Log.e("API Failure", "Error: ${t.message}", t)
            }

        })
    }



    override fun onItemClick(position: Int, preguntasData: Pregunta) {
        var activo = -1

        if (selectedPosition == position) {
            // Ya está seleccionado, deseleccionar
            selectedPosition = null
            binding.btnEditarPregunta.visibility = View.GONE
            binding.btnContenidos.visibility = View.GONE
            binding.btnActivarDesactivar.visibility = View.GONE
        } else {
            // Seleccionar el nuevo elemento
            selectedPosition = position
            binding.btnEditarPregunta.visibility = View.VISIBLE
            binding.btnContenidos.visibility = View.VISIBLE
            binding.btnActivarDesactivar.visibility = View.VISIBLE

            binding.btnEditarPregunta.setOnClickListener {
                preguntasViewModel.selectedPreguntaData = preguntasData
                val pregunta=preguntasViewModel.selectedPreguntaData
                if (pregunta!=null){
                    val idSeccion=pregunta.IdSeccion
                    val usuariosModal = ModalPreguntas(idSeccion,pregunta,this)
                    usuariosModal.show((requireContext() as AppCompatActivity).supportFragmentManager, "DatosUsuarioModal")
                }
            }

            if (preguntasData.Activo == 1) {
                activo = 0
                binding.btnActivarDesactivar.setImageResource(R.drawable.off_button)
            } else {
                activo = 1
                binding.btnActivarDesactivar.setImageResource(R.drawable.on_button)
            }

            binding.btnActivarDesactivar.setOnClickListener {
                if (preguntasData.Activo == 1) {
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

                adminPreguntasAdapter.list[position].Activo = activo

                // Notificar al adaptador sobre el cambio en el ítem específico
                adminPreguntasAdapter.notifyItemChanged(position)

                activarDesactivarPregunta(preguntasData.IdPregunta, activo)
            }

            binding.btnContenidos.setOnClickListener {
                //idSeccionViewModel.idSeccion=seccionesData.IdSeccion
                //findNavController().navigate(R.id.action_administrarSecciones_to_administrarPreguntas)
            }
        }

        // Notificar al adaptador sobre el cambio en la selección
        adminPreguntasAdapter.setSelectedPosition(selectedPosition)
    }



    override fun onPreguntaChanged(idSeccion: Int) {
        obtenerPreguntas(idSeccion)
    }


}