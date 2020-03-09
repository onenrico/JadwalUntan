package me.onenrico.jadwaluntan.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UserLogin(
    val idmhs: Int,
    val idprogdi: Int,
    val idprogram: Int,
    val idperiode: Int,
    val nama: String,
    val progdi: String,
    val fakultas: String,
    val ips: String,
    val maxsks: String,
    val ipk: Double,
    val thakad: String,
    val smt: Int,
    val iden: Int,
    val username: String,
    val passwd: String
) : Parcelable