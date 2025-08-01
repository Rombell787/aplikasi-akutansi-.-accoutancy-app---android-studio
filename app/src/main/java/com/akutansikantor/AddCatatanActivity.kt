package com.akutansikantor

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class AddCatatanActivity : AppCompatActivity() {

    private lateinit var etTanggal: EditText
    private lateinit var etKeterangan: EditText
    private lateinit var etPemasukan: EditText
    private lateinit var etPengeluaran: EditText
    private lateinit var btnSimpan: Button
    private lateinit var db: KeuanganDBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_catatan)

        etTanggal = findViewById(R.id.etTanggal)
        etKeterangan = findViewById(R.id.etKeterangan)
        etPemasukan = findViewById(R.id.etPemasukan)
        etPengeluaran = findViewById(R.id.etPengeluaran)
        btnSimpan = findViewById(R.id.btnSimpan)

        db = KeuanganDBHelper(this)

        val bulan = intent.getStringExtra("bulan")

        btnSimpan.setOnClickListener {
            // Pastikan bulan tidak kosong
            if (bulan.isNullOrEmpty()) {
                Toast.makeText(this, "Bulan tidak valid.", Toast.LENGTH_SHORT).show()
                finish()
                return@setOnClickListener
            }

            val tanggal = etTanggal.text.toString().trim()
            val keterangan = etKeterangan.text.toString().trim()
            val pemasukan = etPemasukan.text.toString().toIntOrNull() ?: 0
            val pengeluaran = etPengeluaran.text.toString().toIntOrNull() ?: 0

            val data = KeuanganModel(
                id = 0, // id akan di-generate otomatis
                bulan = bulan,
                keterangan = "[tgl $tanggal] $keterangan", // Gabungkan tanggal dan keterangan
                pemasukan = pemasukan,
                pengeluaran = pengeluaran
            )

            db.insertData(data)
            Toast.makeText(this, "Catatan berhasil disimpan!", Toast.LENGTH_SHORT).show()

            finish()
        }
    }
}