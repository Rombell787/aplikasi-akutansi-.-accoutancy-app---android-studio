package com.akutansikantor

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.NumberFormat
import java.util.Locale

class DetailKeuanganActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var tvTotalMasuk: TextView
    private lateinit var tvTotalKeluar: TextView
    private lateinit var tvTotalSaldo: TextView // Tambahkan TextView untuk Saldo
    private lateinit var tvJudul: TextView
    private lateinit var btnTambah: Button

    private lateinit var db: KeuanganDBHelper
    private lateinit var adapter: ItemCatatanAdapter
    private var selectedBulan: String? = null

    private val formatter = NumberFormat.getNumberInstance(Locale("in", "ID"))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_keuangan)

        recyclerView = findViewById(R.id.rvCatatan)
        tvTotalMasuk = findViewById(R.id.tvTotalMasuk)
        tvTotalKeluar = findViewById(R.id.tvTotalKeluar)
        tvTotalSaldo = findViewById(R.id.tvTotalSaldo) // Inisialisasi TextView Saldo
        tvJudul = findViewById(R.id.tvJudul)
        btnTambah = findViewById(R.id.btnTambah)

        db = KeuanganDBHelper(this)

        selectedBulan = intent.getStringExtra("bulan")

        // Inisialisasi adapter dengan model yang benar dan logika delete
        adapter = ItemCatatanAdapter(
            catatanList = mutableListOf(),
            onEdit = { item ->
                val intent = Intent(this, AddEditActivity::class.java).apply {
                    putExtra("catatan_id", item.id) // Ini baris yang paling penting!
                }
                startActivity(intent)
            },
            onDelete = { id ->
                showDeleteConfirmation(id)
            }
        )

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        tvJudul.text = "Catatan Bulan $selectedBulan"

        btnTambah.setOnClickListener {
            val intent = Intent(this, AddCatatanActivity::class.java).apply {
                putExtra("bulan", selectedBulan)
            }
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        loadData()
    }

    private fun loadData() {
        selectedBulan?.let { bulan ->
            // Panggil fungsi yang mengambil data DETAIL HARIAN untuk bulan tertentu
            val dataDariDB = db.getAllDataByBulan(bulan)
            adapter.updateData(dataDariDB)
            updateTotal(dataDariDB)
        }
    }

    private fun updateTotal(data: List<KeuanganModel>) {
        val totalMasuk = data.sumOf { it.pemasukan }
        val totalKeluar = data.sumOf { it.pengeluaran }
        val totalSaldo = totalMasuk - totalKeluar // Hitung Saldo

        tvTotalMasuk.text = "Total Pemasukan: Rp ${formatter.format(totalMasuk)}"
        tvTotalKeluar.text = "Total Pengeluaran: Rp ${formatter.format(totalKeluar)}"
        tvTotalSaldo.text = "Saldo: Rp ${formatter.format(totalSaldo)}" // Tampilkan Saldo
    }

    private fun showDeleteConfirmation(id: Int) {
        android.app.AlertDialog.Builder(this)
            .setTitle("Hapus Catatan")
            .setMessage("Yakin ingin menghapus catatan ini?")
            .setPositiveButton("Hapus") { _, _ ->
                db.deleteData(id)
                loadData()
            }
            .setNegativeButton("Batal", null)
            .show()
    }
}