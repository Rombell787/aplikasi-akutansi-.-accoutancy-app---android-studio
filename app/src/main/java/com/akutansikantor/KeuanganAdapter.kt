package com.akutansikantor

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.graphics.toColorInt
import androidx.recyclerview.widget.RecyclerView

class KeuanganAdapter(

    private val onClick: (String) -> Unit,
    private var data: MutableList<KeuanganModel> = mutableListOf()

) : RecyclerView.Adapter<KeuanganAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtBulan: TextView = view.findViewById(R.id.txtBulan)
        val txtKeterangan: TextView = view.findViewById(R.id.txtKeterangan)
        val txtPemasukan: TextView = view.findViewById(R.id.txtPemasukan)
        val txtPengeluaran: TextView = view.findViewById(R.id.txtPengeluaran)
        val txtSaldo: TextView = view.findViewById(R.id.txtSaldo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_keuangan, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]

        holder.txtBulan.text = item.bulan
        holder.txtKeterangan.text = item.keterangan
        holder.txtPemasukan.text = if (item.pemasukan != 0) "Rp ${item.pemasukan}" else "-"
        holder.txtPengeluaran.text = if (item.pengeluaran != 0) "Rp ${item.pengeluaran}" else "-"
        val saldo = item.pemasukan - item.pengeluaran
        holder.txtSaldo.text = "Rp $saldo"
        holder.txtSaldo.setTextColor(
            if (saldo >= 0) "#388E3C".toColorInt() else "#D32F2F".toColorInt()
        )

        holder.itemView.setOnClickListener {
            onClick(item.bulan)
        }
    }

    fun updateData(newData: List<KeuanganModel>) {
        this.data.clear()
        this.data.addAll(newData)
        notifyDataSetChanged()
    }
}
