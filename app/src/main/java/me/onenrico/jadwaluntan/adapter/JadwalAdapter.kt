package me.onenrico.jadwaluntan.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.row_jadwal.view.*
import me.onenrico.jadwaluntan.R
import me.onenrico.jadwaluntan.model.Jadwal
import me.onenrico.jadwaluntan.time.TimeManager

class JadwalAdapter(
    private val context: Context,
    private val today: Boolean = true
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val localData: ArrayList<Jadwal> = arrayListOf()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.row_jadwal, parent, false)
        return JadwalHolder(view)
    }

    override fun getItemCount(): Int {
        return localData.size ?: 0
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is JadwalHolder)
            holder.bind(localData[position])
    }

    inner class JadwalHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(data: Jadwal) {
            itemView.jadwal_name.text = data.nama
            if(data.nama == "Libur"){
                itemView.jadwal_time.text = data.waktu.trim()
                itemView.jadwal_ruangan.visibility = View.GONE
                itemView.time_estimate.visibility = View.GONE
            }else{
                itemView.jadwal_time.text = data.waktu.split("\\(".toRegex())[0].trim()
                itemView.jadwal_ruangan.text = data.waktu.trim().split("(")[1].split(")")[0]

                val todayValue = TimeManager.getTodayValue()
                val jadwalValue = TimeManager.getValue(data.waktu)
                val dif = jadwalValue - todayValue
                if(today){
                    if(dif > 0){
                        itemView.time_estimate.text = "${dif/1000/60}m"
                    }else{
                        itemView.time_estimate.text = "Done"
                    }
                }else{
                    itemView.time_estimate.visibility = View.GONE
                }
            }
        }
    }
}