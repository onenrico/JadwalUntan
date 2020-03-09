package me.onenrico.jadwaluntan.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Jadwal(
    @SerializedName("namamk") val nama: String,
    val sks: String,
    val semester: String,
    val kelas: String,
    @SerializedName("pertemuan") var waktu: String
) : Parcelable