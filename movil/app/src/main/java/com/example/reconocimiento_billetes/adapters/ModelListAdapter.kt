package com.example.reconocimiento_billetes.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.reconocimiento_billetes.R

/**
 * Adaptador para mostrar una lista de modelos en un RecyclerView.
 *
 * @param models Lista de modelos a mostrar.
 * @param onModelSelected Callback que se ejecuta cuando se selecciona un modelo.
 */
class ModelListAdapter(
    private val models: List<String>,
    private val onModelSelected: (String, Int) -> Unit
) : RecyclerView.Adapter<ModelListAdapter.ModelViewHolder>() {

    private var selectedPosition: Int = RecyclerView.NO_POSITION

    /**
     * Crea una nueva instancia de ViewHolder y la inicializa con la vista correspondiente.
     *
     * @param parent El ViewGroup en el que se va a añadir la nueva vista.
     * @param viewType El tipo de vista que se va a crear.
     * @return Un nuevo ModelViewHolder.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ModelViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_model, parent, false)
        return ModelViewHolder(view)
    }

    /**
     * Asigna los datos a las vistas del ViewHolder.
     *
     * @param holder El ViewHolder que contiene las vistas a las que se asignarán los datos.
     * @param position La posición actual en la lista de modelos.
     */
    override fun onBindViewHolder(holder: ModelViewHolder, position: Int) {
        val model = models[position]
        holder.bind(model, position)
    }

    /**
     * Devuelve el número total de modelos en la lista.
     *
     * @return El tamaño de la lista de modelos.
     */
    override fun getItemCount(): Int = models.size

    /**
     * Actualiza la posición del modelo seleccionado y notifica al adaptador.
     *
     * @param position La posición del modelo seleccionado.
     */
    fun setSelectedPosition(position: Int) {
        val previousPosition = selectedPosition
        selectedPosition = position
        notifyItemChanged(previousPosition) // Notificar cambio en la posición anterior
        notifyItemChanged(selectedPosition) // Notificar cambio en la nueva posición
    }

    /**
     * ViewHolder que contiene las referencias a las vistas de cada ítem.
     */
    inner class ModelViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val modelName: TextView = view.findViewById(R.id.model_name)
        private val selectionIndicator: View = view.findViewById(R.id.selection_indicator)

        /**
         * Asigna el modelo y la posición al ViewHolder y establece el listener para clics.
         *
         * @param model El modelo que se va a mostrar.
         * @param position La posición del modelo en la lista.
         */
        fun bind(model: String, position: Int) {
            modelName.text = model
            selectionIndicator.setBackgroundResource(
                if (position == selectedPosition)
                    R.drawable.circle_selected
                else
                    R.drawable.circle_unselected
            )

            itemView.setOnClickListener {
                setSelectedPosition(position)
                onModelSelected(model, position)
            }
        }
    }
}