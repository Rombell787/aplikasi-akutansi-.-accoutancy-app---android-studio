package com.akutansikantor

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

// AddEditActivity.kt
class AddEditActivity : AppCompatActivity() {
    private lateinit var edtBulan: EditText
    private lateinit var edtTahun: EditText
    private lateinit var edtKeterangan: EditText
    private lateinit var edtPemasukan: EditText
    private lateinit var edtPengeluaran: EditText
    private lateinit var btnSimpan: Button
    private lateinit var db: KeuanganDBHelper

    // Variabel untuk menyimpan ID catatan jika dalam mode edit
    private var catatanID = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit)

        edtBulan = findViewById(R.id.edtBulan)
        edtTahun = findViewById(R.id.edtTahun)
        edtKeterangan = findViewById(R.id.edtKeterangan)
        edtPemasukan = findViewById(R.id.edtPemasukan)
        edtPengeluaran = findViewById(R.id.edtPengeluaran)
        btnSimpan = findViewById(R.id.btnSimpan)

        db = KeuanganDBHelper(this)

        // --- Logika Tambahan untuk Mode Edit ---
        catatanID = intent.getIntExtra("catatan_id", -1)

        if (catatanID != -1) {
            // Mode EDIT: Ambil data dari database dan isi ke EditText
            val catatan = db.getDataById(catatanID)

            // Pisahkan bulan dan tahun dari string "bulan - tahun"
            val parts = catatan.bulan.split(" - ")
            if (parts.size == 2) {
                edtBulan.setText(parts[0])
                edtTahun.setText(parts[1])
            }

            edtKeterangan.setText(catatan.keterangan)
            edtPemasukan.setText(catatan.pemasukan.toString())
            edtPengeluaran.setText(catatan.pengeluaran.toString())

            btnSimpan.text = "Update"
        } else {
            // Mode TAMBAH: Biarkan EditText kosong
            btnSimpan.text = "Simpan"
        }
        // ----------------------------------------

        btnSimpan.setOnClickListener {
            val bulanawal = edtBulan.text.toString().trim()
            val tahun = edtTahun.text.toString().trim()
            val bulan = "$bulanawal - $tahun"
            val keterangan = edtKeterangan.text.toString().trim()
            val pemasukan = edtPemasukan.text.toString().toIntOrNull() ?: 0
            val pengeluaran = edtPengeluaran.text.toString().toIntOrNull() ?: 0

            // Buat objek KeuanganModel dari input
            val data = KeuanganModel(
                id = catatanID, // Gunakan ID yang sudah ada jika mode edit
                bulan = bulan,
                keterangan = keterangan,
                pemasukan = pemasukan,
                pengeluaran = pengeluaran
            )

            if (catatanID != -1) {
                // Panggil metode untuk MENGUPDATE data
                db.updateData(data)
                Toast.makeText(this, "Data berhasil diperbarui!", Toast.LENGTH_SHORT).show()
            } else {
                // Panggil metode untuk MENYIMPAN data baru
                db.insertData(data)
                Toast.makeText(this, "Data berhasil disimpan!", Toast.LENGTH_SHORT).show()
            }
            finish()
        }
    }
}