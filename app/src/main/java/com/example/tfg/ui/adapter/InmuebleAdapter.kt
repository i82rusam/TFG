package com.example.tfg.ui.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tfg.R
import com.example.tfg.models.Inmueble
import com.example.tfg.ui.InmuebleDetailActivity

class InmuebleAdapter(
    private var inmuebles: List<Inmueble>,
    private val itemClick: (Inmueble) -> Unit,
    private val itemDelete: (Inmueble) -> Unit
) : RecyclerView.Adapter<InmuebleAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textViewNombre: TextView = view.findViewById(R.id.textViewNombre)
        val textViewCiudad: TextView = view.findViewById(R.id.textViewCiudad)
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
        holder.itemView.setOnClickListener {
            itemClick(inmueble)
            val intent = Intent(holder.itemView.context, InmuebleDetailActivity::class.java)
            intent.putExtra(InmuebleDetailActivity.EXTRA_INMUEBLE, inmueble)
            holder.itemView.context.startActivity(intent)
        }
        holder.itemView.setOnLongClickListener { view ->
            val popup = PopupMenu(holder.itemView.context, view)
            popup.menuInflater.inflate(R.menu.menu_inmueble_detail, popup.menu)
            popup.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.btnEliminar -> {
                        itemDelete(inmueble)
                        true
                    }
                    else -> false
                }
            }
            popup.show()
            true
        }
    }

    override fun getItemCount() = inmuebles.size

    fun setInmuebles(newInmuebles: List<Inmueble>) {
        val oldSize = inmuebles.size
        val newSize = newInmuebles.size

        inmuebles = newInmuebles

        if (oldSize == newSize) {
            notifyItemRangeChanged(0, oldSize)
        } else if (oldSize < newSize) {
            notifyItemRangeChanged(0, oldSize)
            notifyItemRangeInserted(oldSize, newSize - oldSize)
        } else {
            notifyItemRangeChanged(0, newSize)
            notifyItemRangeRemoved(newSize, oldSize - newSize)
        }
    }
    fun updateData(newInmuebles: List<Inmueble>) {
        this.inmuebles = newInmuebles
        notifyDataSetChanged()
    }
}