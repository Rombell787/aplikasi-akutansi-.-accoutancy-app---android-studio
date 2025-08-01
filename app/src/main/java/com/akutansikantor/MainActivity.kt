package com.akutansikantor

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.NumberFormat
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private lateinit var totalPemasukanTextView: TextView
    private lateinit var totalPengeluaranTextView: TextView
    private lateinit var totalSaldoTextView: TextView // Tambahkan TextView untuk total saldo
    private lateinit var recyclerView: RecyclerView
    private lateinit var btnTambah: Button

    private lateinit var db: KeuanganDBHelper
    private lateinit var adapter: KeuanganAdapter

    private val formatter = NumberFormat.getNumberInstance(Locale("in", "ID"))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        totalPemasukanTextView = findViewById(R.id.totalPemasukanTextView)
        totalPengeluaranTextView = findViewById(R.id.totalPengeluaranTextView)
        totalSaldoTextView = findViewById(R.id.totalSaldoTextView) // Inisialisasi TextView
        recyclerView = findViewById(R.id.rvCatatan)
        btnTambah = findViewById(R.id.btnTambah)

        db = KeuanganDBHelper(this)

        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = KeuanganAdapter(
            onClick = { bulan ->
                val intent = Intent(this, DetailKeuanganActivity::class.java)
                intent.putExtra("bulan", bulan)
                startActivity(intent)
            }
        )

        recyclerView.adapter = adapter

        btnTambah.setOnClickListener {
            val intent = Intent(this, AddEditActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        loadData()
    }

    private fun loadData() {
        val newData = db.getMonthlySummary()
        val totalPemasukan = newData.sumOf { it.pemasukan }
        val totalPengeluaran = newData.sumOf { it.pengeluaran }
        val totalSaldo = totalPemasukan - totalPengeluaran

        totalPemasukanTextView.text = "Rp ${formatter.format(totalPemasukan)}"
        totalPengeluaranTextView.text = "Rp ${formatter.format(totalPengeluaran)}"
        totalSaldoTextView.text = "Rp ${formatter.format(totalSaldo)}" // Tampilkan total saldo

        // Panggil fungsi updateData() yang sudah kamu buat di KeuanganAdapter
        adapter.updateData(newData)
    }
}