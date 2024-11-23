package com.example.reconocimiento_billetes.presentation

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.reconocimiento_billetes.R
import com.example.reconocimiento_billetes.domain.BillData

class BillsAdapter(private var bills: List<BillData>) :
    RecyclerView.Adapter<BillsAdapter.BillViewHolder>() {

    class BillViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val valueTextView: TextView = itemView.findViewById(R.id.valueTextView)
        val dateTextView: TextView = itemView.findViewById(R.id.dateTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BillViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_bill, parent, false)
        return BillViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: BillViewHolder, position: Int) {
        val bill = bills[position]
        holder.valueTextView.text = "$${bill.value}"
        holder.dateTextView.text = bill.date
    }

    override fun getItemCount() = bills.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateBills(newBills: List<BillData>) {
        bills = newBills
        notifyDataSetChanged()
    }
}