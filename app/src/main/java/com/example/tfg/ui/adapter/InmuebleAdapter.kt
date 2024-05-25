package com.example.tfg.ui

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tfg.R
import com.example.tfg.models.Inmueble

class InmuebleAdapter(private var inmuebles: List<Inmueble>) :
    RecyclerView.Adapter<InmuebleAdapter.InmuebleViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InmuebleViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_inmueble, parent, false)
        return InmuebleViewHolder(view)
    }

    override fun onBindViewHolder(holder: InmuebleViewHolder, position: Int) {
        val inmueble = inmuebles[position]
        holder.bind(inmueble)
    }

    override fun getItemCount(): Int {
        return inmuebles.size
    }

    fun setInmuebles(inmuebles: List<Inmueble>) {
        this.inmuebles = inmuebles
        notifyDataSetChanged()
    }

    inner class InmuebleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textViewNombre: TextView = itemView.findViewById(R.id.textViewNombre)
        private val textViewCiudad: TextView = itemView.findViewById(R.id.textViewCiudad)

        fun bind(inmueble: Inmueble) {
            textViewNombre.text = inmueble.nombre
            textViewCiudad.text = inmueble.ciudad

            itemView.setOnClickListener {
                val intent = Intent(itemView.context, InmuebleDetailActivity::class.java).apply {
                    putExtra("nombre", inmueble.nombre)
                    putExtra("ciudad", inmueble.ciudad)
                    putExtra("alquilado", inmueble.alquilado)
                    putExtra("ubicacion", inmueble.ubicacion)
                    putExtra("documento", inmueble.escritura)
                    putExtra("imagen", inmueble.imagen)
                    putExtra("usuario", inmueble.usuario)
                }
                itemView.context.startActivity(intent)
            }
        }
    }
}
