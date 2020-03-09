package me.onenrico.jadwaluntan.activity

import android.net.Uri
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import android.view.animation.OvershootInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.robinhood.ticker.TickerUtils
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_profile.*
import me.onenrico.jadwaluntan.R
import me.onenrico.jadwaluntan.model.UserLogin
import me.onenrico.jadwaluntan.repository.LocalSource
import me.onenrico.jadwaluntan.time.capitalizeWords
import me.onenrico.jadwaluntan.time.round

class ProfileActivity : AppCompatActivity() {

    lateinit var localSource: LocalSource
    var userLoginData = MutableLiveData<UserLogin>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )

        setContentView(R.layout.activity_profile)

        localSource = LocalSource(this)
        logout_btn.setOnClickListener {
            localSource.logout()
            finish()
        }
        back_btn.setOnClickListener {
            finish()
        }

        userLoginData.value = localSource.getUser()
        userLoginData.observe(this, Observer { userLogin ->
            Picasso.get()
                .load(Uri.parse("http://service.untan.ac.id:7488/mhs/getpicbynim/${userLogin.username}"))
                .placeholder(resources.getDrawable(R.drawable.profile))
                .error(resources.getDrawable(R.drawable.profile))
                .into(profile_img)

            nama_mhs.text = userLogin.nama.capitalizeWords()
            nim_mhs.text = userLogin.username
            fakultas_mhs.text = userLogin.fakultas
            jurusasn_mhs.text = userLogin.progdi

            ips_mhs.setCharacterLists(TickerUtils.provideNumberList())
            ips_mhs.animationInterpolator = OvershootInterpolator()
            ips_mhs.setText(userLogin.ips, true)

            val ipk = userLogin.ipk.round(2)
            ipk_mhs.setCharacterLists(TickerUtils.provideNumberList())
            ipk_mhs.animationInterpolator = OvershootInterpolator()
            ipk_mhs.setText(ipk.toString(), true)
        })

    }
}
