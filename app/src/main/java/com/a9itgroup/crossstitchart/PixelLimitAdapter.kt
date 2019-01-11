package com.a9itgroup.crossstitchart

import android.graphics.Color
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PixelLimitAdapter(private val myPixelLimitSet: ArrayList<MyColor>, _pixelLimitAdapterListener: PixelLimitAdapterListener):RecyclerView.Adapter<PixelLimitAdapter.ViewHolder>(){
    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    private var pixelLimitAdapterListener: PixelLimitAdapterListener = _pixelLimitAdapterListener

    interface PixelLimitAdapterListener{
        fun changeColorLimit(position: Int, limit: Int)
    }

    lateinit var viewParent:ViewGroup

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PixelLimitAdapter.ViewHolder {
        viewParent = parent
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.pixel_limit_adapter, parent, false)
        return PixelLimitAdapter.ViewHolder(view)
    }

    override fun getItemCount() = myPixelLimitSet.size

    override fun onBindViewHolder(holder: PixelLimitAdapter.ViewHolder, position: Int) {
        val tvColor = holder.view.findViewById<TextView>(R.id.tvColor_pixel_limit_adapter)
        val etLimit = holder.view.findViewById<EditText>(R.id.etLimit_pixel_limit_adapter)

        tvColor.setTextColor(myPixelLimitSet[position].getColor())
        val colorName: String = when (myPixelLimitSet[position].getColor()) {
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
        etLimit.setText(myPixelLimitSet[position].getLimit().toString())

        etLimit.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val limit = if (etLimit.text.toString().isNotEmpty()) etLimit.text.toString().toInt() else 0

                pixelLimitAdapterListener.changeColorLimit(position, limit)
                viewParent
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
    }
}