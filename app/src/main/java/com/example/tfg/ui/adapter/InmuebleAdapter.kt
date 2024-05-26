package com.example.tfg.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tfg.R
import com.example.tfg.models.Inmueble

class InmuebleAdapter(
    private var inmuebles: List<Inmueble>,
    private val onItemClick: (Inmueble) -> Unit
) : RecyclerView.Adapter<InmuebleAdapter.ViewHolder>() {

    fun setInmuebles(newInmuebles: List<Inmueble>) {
        inmuebles = newInmuebles
        notifyDataSetChanged()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textViewNombre: TextView = view.findViewById(R.id.textViewNombre)
        val textViewCiudad: TextView = view.findViewById(R.id.textViewCiudad)
        val textViewUbicacion: TextView = view.findViewById(R.id.textViewUbicacion)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_inmueble, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val inmueble = inmuebles[position]
        holder.textViewNombre.text = inmueble.nombre
        holder.textViewCiudad.text = inmueble.ciudad
        holder.textViewUbicacion.text = inmueble.ubicacion

        holder.itemView.setOnClickListener {
            onItemClick(inmueble)
        }
    }

    override fun getItemCount() = inmuebles.size

}