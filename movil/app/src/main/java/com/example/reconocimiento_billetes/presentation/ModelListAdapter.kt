package com.example.reconocimiento_billetes.presentation

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.reconocimiento_billetes.R

class ModelListAdapter(
    private val models: List<String>,
    private val onModelSelected: (String, Int) -> Unit
) : RecyclerView.Adapter<ModelListAdapter.ModelViewHolder>() {

    private var selectedPosition: Int = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ModelViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_model, parent, false)
        return ModelViewHolder(view)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(
        holder: ModelViewHolder,
        @SuppressLint("RecyclerView") position: Int
    ) {
        val model = models[position]
        holder.modelName.text = model

        if (position == selectedPosition)
            holder.selectionIndicator.setBackgroundResource(R.drawable.circle_selected)
        else
            holder.selectionIndicator.setBackgroundResource(R.drawable.circle_unselected)

        holder.itemView.setOnClickListener {
            selectedPosition = position
            onModelSelected(model, position)
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int {
        return models.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setSelectedPosition(position: Int) {
        selectedPosition = position
        notifyDataSetChanged()
    }

    inner class ModelViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val modelName: TextView = view.findViewById(R.id.model_name)
        val selectionIndicator: View = view.findViewById(R.id.selection_indicator)
    }
}
