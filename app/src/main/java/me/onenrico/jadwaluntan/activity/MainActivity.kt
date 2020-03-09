package me.onenrico.jadwaluntan.activity

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import me.onenrico.jadwaluntan.R
import me.onenrico.jadwaluntan.adapter.JadwalAdapter
import me.onenrico.jadwaluntan.model.Jadwal
import me.onenrico.jadwaluntan.model.UserLogin
import me.onenrico.jadwaluntan.repository.LocalSource
import me.onenrico.jadwaluntan.repository.RemoteSource
import me.onenrico.jadwaluntan.time.TimeManager


class MainActivity : AppCompatActivity() {
    lateinit var remoteSource: RemoteSource
    lateinit var localSource: LocalSource
    lateinit var userLogin: UserLogin

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )

        localSource = LocalSource(this)
        if (!localSource.isLogin()) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }
        userLogin = localSource.getUser()

        setContentView(R.layout.activity_main)
        setupTime()
        setupProfile()
    }

    private fun setupProfile() {
        profile_btn.setOnClickListener {
            Intent(this, ProfileActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                startActivity(this)
            }
        }
    }

    private fun setupTime() {
        updateTime()
        remoteSource = RemoteSource()
        updateList()
    }

    @SuppressLint("SetTextI18n")
    fun updateTime() {
        val today = TimeManager.getToday()
        header_text.text = TimeManager.getNow()
        sub_text.text = "$today, ${TimeManager.getDate()}"
    }

    fun updateList() {
        remoteSource.getJadwal(userLogin.idmhs.toString(), userLogin.idperiode.toString())
            .observe(this@MainActivity, Observer { jadwals ->
                Log.i("MEOW","Padahal Disini")
                val todayAdapter = JadwalAdapter(this)
                if (jadwals.isNotEmpty()) {
                    todayAdapter.localData.addAll(jadwals.filter {
                        it.waktu.contains(TimeManager.getToday())
                    }.sortedBy { TimeManager.getValue(it.waktu) })
                }
                if (todayAdapter.localData.isEmpty()) {
                    todayAdapter.localData.add(Jadwal("Libur", "", "", "", TimeManager.getToday()))
                }
                today_jadwal_list.adapter = todayAdapter
                today_jadwal_list.layoutManager = LinearLayoutManager(this)

                val tommorowAdapter = JadwalAdapter(this, false)
                if (jadwals.isNotEmpty()) {
                    tommorowAdapter.localData.addAll(jadwals.filter {
                        it.waktu.contains(TimeManager.getToday(1))
                    }.sortedBy { TimeManager.getValue(it.waktu) })
                }
                if (tommorowAdapter.localData.isEmpty()) {
                    tommorowAdapter.localData.add(Jadwal("Libur", "", "", "", TimeManager.getToday(1)))
                }
                tommorow_jadwal_list.adapter = tommorowAdapter
                tommorow_jadwal_list.layoutManager = LinearLayoutManager(this)

                val yesterdayAdapter = JadwalAdapter(this, false)
                if (jadwals.isNotEmpty()) {
                    yesterdayAdapter.localData.addAll(jadwals.filter {
                        it.waktu.contains(TimeManager.getToday(-1))
                    }.sortedBy { TimeManager.getValue(it.waktu) })
                }
                if (yesterdayAdapter.localData.isEmpty()) {
                    yesterdayAdapter.localData.add(Jadwal("Libur", "", "", "", TimeManager.getToday(-1)))
                }
                yesterday_jadwal_list.adapter = yesterdayAdapter
                yesterday_jadwal_list.layoutManager = LinearLayoutManager(this)

                jadwal_progress.visibility = View.GONE
            })
    }

    var tickReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            updateTime()
        }
    }

    override fun onResume() {
        super.onResume()
        registerReceiver(tickReceiver, IntentFilter(Intent.ACTION_TIME_TICK))

        localSource = LocalSource(this)
        if (!localSource.isLogin()) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(tickReceiver)
    }
}
