package com.artry.scannerBarcode.Utilities

class Utils {
    fun convertIndoTgl(date: String, withYear:Boolean=false) : String{
        val raw = date.split("-")
        val bulan = when (raw[1]) {
            "01" -> { "Januari" }
            "02" -> { "Februari" }
            "03" -> { "Maret" }
            "04" -> { "April" }
            "05" -> { "Mei" }
            "06" -> { "Juni" }
            "07" -> { "Juli" }
            "08" -> { "Agustus" }
            "09" -> { "September" }
            "10" -> { "Oktober" }
            "11" -> { "November" }
            else -> { "Desember" }
        }

        return if (withYear) { "${raw[2]} $bulan ${raw[0]}" } else { "${raw[2]} $bulan" }
    }

}