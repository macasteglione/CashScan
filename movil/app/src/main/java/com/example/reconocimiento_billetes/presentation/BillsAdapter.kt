package com.example.reconocimiento_billetes.presentation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.reconocimiento_billetes.R
import com.example.reconocimiento_billetes.domain.BillData
import java.text.NumberFormat
import java.util.Locale

/**
 * Adaptador para mostrar una lista de billetes en un RecyclerView.
 *
 * Este adaptador toma una lista de objetos [BillData] y los muestra en un RecyclerView.
 * Utiliza [DiffUtil] para actualizar la lista de manera eficiente cuando los datos cambian.
 *
 * @param bills Lista inicial de billetes.
 */
class BillsAdapter(private var bills: List<BillData>) :
    RecyclerView.Adapter<BillsAdapter.BillViewHolder>() {

    /**
     * ViewHolder que contiene las referencias a los elementos de la vista.
     *
     * Este ViewHolder es responsable de referenciar las vistas individuales dentro de
     * cada item del RecyclerView para el valor y la fecha del billete.
     */
    class BillViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val valueTextView: TextView = itemView.findViewById(R.id.valueTextView)
        val dateTextView: TextView = itemView.findViewById(R.id.dateTextView)
    }

    /**
     * Infla la vista del ítem en el RecyclerView.
     *
     * Este método se invoca para crear un nuevo ViewHolder cuando se necesita. Infla el diseño de cada ítem
     * utilizando el archivo XML `layout_bill` y crea un objeto `BillViewHolder` para esa vista.
     *
     * @param parent El contenedor donde se colocará el nuevo ítem.
     * @param viewType El tipo de vista del ítem (no utilizado en este caso).
     * @return Un nuevo ViewHolder con la vista inflada.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BillViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_bill, parent, false)
        return BillViewHolder(view)
    }

    /**
     * Vincula los datos de un billete a su vista correspondiente.
     *
     * Este método es responsable de llenar las vistas de un ítem del RecyclerView con los datos del objeto
     * [BillData] correspondiente a la posición actual.
     *
     * @param holder El ViewHolder que contiene las vistas a llenar.
     * @param position La posición del ítem dentro de la lista de billetes.
     */
    override fun onBindViewHolder(holder: BillViewHolder, position: Int) {
        val bill = bills[position]
        holder.valueTextView.text = formatCurrency(bill.value)
        holder.dateTextView.text = bill.date
    }

    /**
     * Devuelve el número total de ítems en la lista de billetes.
     *
     * @return El tamaño de la lista de billetes.
     */
    override fun getItemCount(): Int = bills.size

    /**
     * Actualiza la lista de billetes utilizando DiffUtil para optimizar las actualizaciones.
     *
     * Este método compara la lista actual con la nueva lista de billetes y calcula las diferencias
     * para aplicar solo los cambios necesarios, mejorando el rendimiento al actualizar el RecyclerView.
     *
     * @param newBills Lista actualizada de billetes.
     */
    fun updateBills(newBills: List<BillData>) {
        val diffResult = DiffUtil.calculateDiff(
            BillDiffCallback(
                bills,
                newBills
            )
        )
        bills = newBills
        diffResult.dispatchUpdatesTo(this)  // Aplica los cambios
    }

    /**
     * Formatea un valor monetario según la configuración regional actual.
     *
     * Este método toma un valor de tipo `Int` y lo formatea como una cadena de texto en el formato de
     * moneda correspondiente a la configuración regional del dispositivo.
     *
     * @param value El valor monetario que se desea formatear.
     * @return Una cadena de texto que representa el valor monetario formateado.
     */
    private fun formatCurrency(value: Int): String {
        val formatter = NumberFormat.getCurrencyInstance(Locale.getDefault())
        return formatter.format(value)
    }

    /**
     * Clase para calcular las diferencias entre dos listas de billetes.
     *
     * Esta clase usa [DiffUtil] para comparar dos listas de objetos [BillData] y determinar qué elementos
     * han cambiado, se han agregado o se han eliminado, lo que permite actualizar el RecyclerView de manera
     * más eficiente.
     */
    class BillDiffCallback(
        private val oldList: List<BillData>,
        private val newList: List<BillData>
    ) : DiffUtil.Callback() {

        /**
         * Devuelve el tamaño de la lista antigua.
         *
         * @return El tamaño de la lista antigua.
         */
        override fun getOldListSize(): Int = oldList.size

        /**
         * Devuelve el tamaño de la nueva lista.
         *
         * @return El tamaño de la nueva lista.
         */
        override fun getNewListSize(): Int = newList.size

        /**
         * Compara si dos elementos de la lista son el mismo objeto.
         *
         * En este caso, se considera que los elementos son los mismos si tienen la misma fecha.
         * Esto se usa para determinar si un billete en la lista vieja corresponde al mismo billete en la nueva lista.
         *
         * @param oldItemPosition La posición del ítem en la lista antigua.
         * @param newItemPosition La posición del ítem en la lista nueva.
         * @return `true` si los ítems son el mismo (por ejemplo, tienen la misma fecha), `false` de lo contrario.
         */
        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].date == newList[newItemPosition].date
        }

        /**
         * Compara si los contenidos de dos elementos son los mismos.
         *
         * @param oldItemPosition La posición del ítem en la lista antigua.
         * @param newItemPosition La posición del ítem en la lista nueva.
         * @return `true` si los contenidos de los ítems son los mismos, `false` de lo contrario.
         */
        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }
}