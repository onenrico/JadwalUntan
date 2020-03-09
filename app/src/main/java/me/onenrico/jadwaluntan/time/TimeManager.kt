package me.onenrico.jadwaluntan.time

import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.round

object TimeManager {
    enum class Hari(val id: Int) {
        MINGGU(1), SENIN(2), SELASA(3), RABU(4), KAMIS(5), JUMAT(6), SABTU(7);

        fun getName(): String {
            return toString().toLowerCase(Locale.ROOT).capitalize()
        }
    }

    enum class Bulan(val id: Int) {
        JANUARI(0), FEBRUARI(1), MARET(2), APRIL(3), MEI(4), JUNI(5),
        JULI(6), AGUSTUS(7), SEPTEMBER(8),
        OKTOBER(9), NOVEMBER(10), DESEMBER(11);

        fun getName(): String {
            return toString().toLowerCase(Locale.ROOT).capitalize()
        }
    }

    fun getTodayValue(): Long {
        val now = "${getToday()}  ${getNow()}"
        return getValue(now)
    }

    fun getValue(waktu: String): Long {
        val milishari = ((Hari.valueOf(waktu.split("  ")[0].toUpperCase()).id + 1) * 86400)
        val milisjam =
            SimpleDateFormat("HH:mm", Locale.ROOT).parse(waktu.split("  ")[1].split("-")[0].trim())
                ?.time
        return milishari + (milisjam ?: 0)
    }

    fun getToday(offset: Int = 0): String {
        var id = (Calendar.getInstance().get(Calendar.DAY_OF_WEEK) + offset) % 7
        if (id < 1) id += 7
        return (Hari.values().firstOrNull {
            it.id == id
        } ?: Hari.MINGGU).getName()
    }

    fun getMonth(): String {
        val id = Calendar.getInstance().get(Calendar.MONTH)
        return (Bulan.values().firstOrNull { it.id == id } ?: Bulan.JANUARI).getName()
    }

    fun getDay(): String {
        return Calendar.getInstance().get(Calendar.DATE).toString()
    }

    fun getYear(): String {
        return Calendar.getInstance().get(Calendar.YEAR).toString()
    }

    fun getDate(): String {
        return "${getDay()} ${getMonth()} ${getYear()}"
    }

    fun getNow(): String {
        var hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY).toString()
        if (hour.length == 1) hour = "0$hour"
        var minute = Calendar.getInstance().get(Calendar.MINUTE).toString()
        if (minute.length == 1) minute = "0$minute"
        return "$hour:$minute"
    }

}

fun Double.round(decimals: Int): Double {
    var multiplier = 1.0
    repeat(decimals) { multiplier *= 10 }
    return round(this * multiplier) / multiplier
}
fun String.capitalizeWords(): String = split(" ").joinToString(" ") { it.toLowerCase().capitalize() }
