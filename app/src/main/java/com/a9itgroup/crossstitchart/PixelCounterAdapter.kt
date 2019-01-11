package com.a9itgroup.crossstitchart

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PixelCounterAdapter(private val myPixelCounterSet: ArrayList<MyColor>) :
    RecyclerView.Adapter<PixelCounterAdapter.ViewHolder>() {
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val tvColor = holder.view.findViewById<TextView>(R.id.tvColor_pixel_counter_adapter)
        val tvNumber = holder.view.findViewById<TextView>(R.id.tvNumber_pixel_counter_adapter)

        tvColor.setTextColor(myPixelCounterSet[position].getColor())
        val colorName: String = when (myPixelCounterSet[position].getColor()) {
            //SILVER //GRAY //BLACK //RED
            Color.parseColor("#C0C0C0") -> "SILVER"
            Color.parseColor("#808080") -> "GRAY"
            Color.parseColor("#000000") -> "BLACK"
            Color.parseColor("#FF0000") -> "RED"
            //MAROON //YELLOW //OLIVE //LIME
            Color.parseColor("#800000") -> "MAROON"
            Color.parseColor("#FFFF00") -> "YELLOW"
            Color.parseColor("#808000") -> "OLIVE"
            Color.parseColor("#00FF00") -> "LIME"
            //GREEN //AQUA //TEAL //BLUE
            Color.parseColor("#008000") -> "GREEN"
            Color.parseColor("#00FFFF") -> "AQUA"
            Color.parseColor("#008080") -> "TEAL"
            Color.parseColor("#0000FF") -> "BLUE"
            // NAVY //FUCHSIA //PURPLE //ORANGE
            Color.parseColor("#000080") -> "NAVY"
            Color.parseColor("#FF00FF") -> "FUCHSIA"
            Color.parseColor("#800080") -> "PURPLE"
            Color.parseColor("#FFA500") -> "ORANGE"
            else -> "UNKNOWN COLOR"
        }
        tvColor.text = colorName
        tvNumber.text = myPixelCounterSet[position].getCount().toString()
    }

    override fun getItemCount() = myPixelCounterSet.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.pixel_counter_adapter, parent, false)
        return PixelCounterAdapter.ViewHolder(view)
    }

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)

}