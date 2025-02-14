package com.alfie.whitepaper.core.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

const val FORMAT_dd_MM_yyyy_hh_mm_ss = "dd_MM_yyyy_hh_mm_ss"
fun getCurrentDateTime(format: String = FORMAT_dd_MM_yyyy_hh_mm_ss): String {
    val sdf = SimpleDateFormat(format, Locale.ENGLISH)
    return sdf.format(Date())
}