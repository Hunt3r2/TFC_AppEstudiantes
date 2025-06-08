package com.example.proyectoappfinanzas

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectoappfinanzas.modelos.Nota

class NotaAdapter(
    private val onClick: (Nota) -> Unit
) : ListAdapter<Nota, NotaAdapter.NotaViewHolder>(DiffCallback) {

    object DiffCallback : DiffUtil.ItemCallback<Nota>() {
        override fun areItemsTheSame(oldItem: Nota, newItem: Nota) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Nota, newItem: Nota) = oldItem == newItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotaViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_nota, parent, false)
        return NotaViewHolder(view)
    }

    override fun onBindViewHolder(holder: NotaViewHolder, position: Int) {
        val nota = getItem(position)
        holder.bind(nota)
        holder.itemView.setOnClickListener { onClick(nota) }
    }

    class NotaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titulo: TextView = itemView.findViewById(R.id.tvTituloNota)
        private val fecha: TextView = itemView.findViewById(R.id.tvFechaNota)

        fun bind(nota: Nota) {
            titulo.text = nota.titulo
            fecha.text = nota.fecha_creacion.toString()
        }
    }
}
