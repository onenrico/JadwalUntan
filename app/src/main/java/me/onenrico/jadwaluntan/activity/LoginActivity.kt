package me.onenrico.jadwaluntan.activity

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.onenrico.jadwaluntan.R
import me.onenrico.jadwaluntan.repository.LocalSource
import me.onenrico.jadwaluntan.repository.RemoteSource

class LoginActivity : AppCompatActivity() {
    val hidden = MutableLiveData<Boolean>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )

        hidden.observe(this, Observer {
            if (it) {
                reveal_password.setColorFilter(resources.getColor(R.color.coolGray))
                inp_password.transformationMethod = PasswordTransformationMethod.getInstance()
            } else {
                reveal_password.setColorFilter(resources.getColor(R.color.colorPrimary))
                inp_password.transformationMethod = HideReturnsTransformationMethod.getInstance()
            }
        })

        setContentView(R.layout.activity_login)

        hidden.value = true
        reveal_password.setOnClickListener {
            hidden.value = !hidden.value!!
        }

        val remoteSource = RemoteSource()
        btn_login.setOnClickListener {
            login_error.visibility = View.GONE
            var nim: String
            var password: String
            inp_nim.let {
                nim = it.text.toString()
                if (nim.isBlank() || nim.length != 11) {
                    it.error = getString(R.string.nim_salah)
                    it.requestFocus()
                    return@setOnClickListener
                }
            }
            inp_password.let {
                password = it.text.toString()
                if (password.isBlank()) {
                    it.error = getString(R.string.password_salah)
                    it.requestFocus()
                    return@setOnClickListener
                }
            }
            btn_login.isEnabled = false
            val dialog = Dialog(this)
            dialog.setContentView(R.layout.dialog_login)
            dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
            dialog.setCancelable(false)
            dialog.show()
            CoroutineScope(Dispatchers.IO).launch {
                val user = remoteSource.getUser(nim, password)
                withContext(Dispatchers.Main) {
                    btn_login.isEnabled = true
                    Log.i("MEOW", "User $user")
                    dialog.dismiss()
                    when {
                        user == null -> {
                            login_error.visibility = View.VISIBLE
                            login_error.text = getString(R.string.login_error)
                        }
                        user.idmhs == 0 -> {
                            login_error.visibility = View.VISIBLE
                            login_error.text = getString(R.string.login_salah)
                        }
                        else -> {
                            val localSource = LocalSource(this@LoginActivity)
                            localSource.saveUser(user)
                            Intent(this@LoginActivity,MainActivity::class.java).apply {
                                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                                startActivity(this)
                            }
                            finish()
                        }
                    }
                }
            }
        }
        inp_password.setOnEditorActionListener { _, _, _ ->
            btn_login.performClick()
        }
    }
}
