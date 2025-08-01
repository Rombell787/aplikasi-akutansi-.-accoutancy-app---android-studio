package com.akutansikantor

data class KeuanganModel(
    val id: Int,
    val bulan: String,
    val keterangan: String,
    val pemasukan: Int,
    val pengeluaran: Int,
)