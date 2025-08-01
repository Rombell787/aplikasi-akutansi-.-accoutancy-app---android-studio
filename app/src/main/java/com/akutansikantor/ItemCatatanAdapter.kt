package com.akutansikantor

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.NumberFormat
import java.util.Locale

class ItemCatatanAdapter(
    private var catatanList: List<KeuanganModel>,
    private val onDelete: (Int) -> Unit,
    private val onEdit: (KeuanganModel) -> Unit // <-- TAMBAHKAN FUNGSI EDIT
) : RecyclerView.Adapter<ItemCatatanAdapter.ViewHolder>() {

    private val formatter = NumberFormat.getNumberInstance(Locale("in", "ID"))

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val keteranganText: TextView = view.findViewById(R.id.tvKeterangan)
        val pemasukanText: TextView = view.findViewById(R.id.tvPemasukan)
        val pengeluaranText: TextView = view.findViewById(R.id.tvPengeluaran)
        val btnMenu: ImageButton = view.findViewById(R.id.btnMenu) // <-- GANTI DENGAN INI
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_catatan, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = catatanList[position]

        holder.keteranganText.text = item.keterangan

        holder.pemasukanText.text = if (item.pemasukan > 0) "Rp ${formatter.format(item.pemasukan)}" else "-"
        holder.pengeluaranText.text = if (item.pengeluaran > 0) "Rp ${formatter.format(item.pengeluaran)}" else "-"

        holder.btnMenu.setOnClickListener { view ->
            val popup = PopupMenu(view.context, view)
            popup.menuInflater.inflate(R.menu.item_menu, popup.menu)

            popup.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.action_edit -> {
                        onEdit(item) // <-- Panggil onEdit
                        true
                    }
                    R.id.action_delete -> {
                        onDelete(item.id) // <-- Panggil onDelete
                        true
                    }
                    else -> false
                }
            }
            popup.show()
        }
    }

    override fun getItemCount(): Int = catatanList.size

    fun updateData(newData: List<KeuanganModel>) {
        this.catatanList = newData
        notifyDataSetChanged()
    }
}