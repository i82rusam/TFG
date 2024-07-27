package com.example.tfg.ui.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tfg.R
import com.example.tfg.models.Inmueble
import com.example.tfg.ui.InmuebleDetailActivity

class InmuebleAdapter(
    private var inmuebles: List<Inmueble>,
    private val itemClick: (Inmueble) -> Unit,
    private val itemEdit: (Inmueble) -> Unit,
    private val itemDelete: (Inmueble) -> Unit
) : RecyclerView.Adapter<InmuebleAdapter.InmuebleViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InmuebleViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_inmueble, parent, false)
        return InmuebleViewHolder(view)
    }

    override fun onBindViewHolder(holder: InmuebleViewHolder, position: Int) {
        val inmueble = inmuebles[position]
        holder.bind(inmueble, itemClick, itemEdit, itemDelete)
    }

    override fun getItemCount(): Int = inmuebles.size

    fun setInmuebles(inmuebles: List<Inmueble>) {
        this.inmuebles = inmuebles
        notifyDataSetChanged()
    }

    class InmuebleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nombreTextView: TextView = itemView.findViewById(R.id.textViewNombre)
        private val ciudadTextView: TextView = itemView.findViewById(R.id.textViewCiudad)

        fun bind(
            inmueble: Inmueble,
            itemClick: (Inmueble) -> Unit,
            itemEdit: (Inmueble) -> Unit,
            itemDelete: (Inmueble) -> Unit
        ) {
            nombreTextView.text = inmueble.nombre
            ciudadTextView.text = inmueble.ciudad

            itemView.setOnClickListener {
                val context = itemView.context
                val intent = Intent(context, InmuebleDetailActivity::class.java).apply {
                    putExtra(InmuebleDetailActivity.EXTRA_INMUEBLE_ID, inmueble.idInmueble)
                }
                context.startActivity(intent)
            }
        }
    }
}