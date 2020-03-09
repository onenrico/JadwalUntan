package me.onenrico.jadwaluntan.repository

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import me.onenrico.jadwaluntan.model.UserLogin

class LocalSource(val context: Context) {
    companion object {
        private const val login_data = "LOGIN_DATA"
    }

    val sharedPreferences: SharedPreferences by lazy {
        context.getSharedPreferences("localsource", Context.MODE_PRIVATE)
    }

    fun isLogin(): Boolean {
        return sharedPreferences.getString(login_data, "")?.isNotBlank() ?: false
    }

    @SuppressLint("ApplySharedPref")
    fun saveUser(userLogin: UserLogin) {
        sharedPreferences.edit().putString(login_data, Gson().toJson(userLogin)).commit()
    }

    fun getUser(): UserLogin {
        if (!isLogin()) {
            return UserLogin(
                0, 0, 0, 0, "?", "?",
                "?", "0", "0", 0.0, "?", 0, 0, "", ""
            )
        }
        return Gson().fromJson(sharedPreferences.getString(login_data, ""), UserLogin::class.java)
    }

    fun logout() {
        sharedPreferences.edit()
            .putString(login_data, "")
            .apply()
    }

}