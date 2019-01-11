package com.a9itgroup.crossstitchart

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.fragment_config.*


class FragmentConfig : Fragment() {

    companion object {
        fun newInstance(gridSizeX: Int, gridSizeY: Int, pixelSizeX: Int, pixelSizeY: Int): FragmentConfig {
            val frag = FragmentConfig()
            val bundle = Bundle()
            bundle.putInt("gridSizeX", gridSizeX)
            bundle.putInt("gridSizeY", gridSizeY)
            bundle.putInt("pixelSizeX", pixelSizeX)
            bundle.putInt("pixelSizeY", pixelSizeY)

            frag.arguments = bundle
            return frag
        }
    }

    var myView: View? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        myView = inflater.inflate(R.layout.fragment_config, container, false)
        return myView
    }

    override fun onViewCreated(@NonNull view: View, @Nullable savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val gridSizeX = arguments?.getInt("gridSizeX")
        val gridSizeY = arguments?.getInt("gridSizeY")
        val pixelSizeX = arguments?.getInt("pixelSizeX")
        val pixelSizeY = arguments?.getInt("pixelSizeY")

        grid_size_x.setText(gridSizeX!!.toString())
        grid_size_y.setText(gridSizeY!!.toString())
        pixel_size_x.setText(pixelSizeX!!.toString())
        pixel_size_y.setText(pixelSizeY!!.toString())

        grid_size_x.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val gridSizeX = if (grid_size_x.text.toString().isNotEmpty()) grid_size_x.text.toString().toInt() else 1
                val gridSizeY = if (grid_size_y.text.toString().isNotEmpty()) grid_size_y.text.toString().toInt() else 1

                (activity as MainActivity).changeGridSize(gridSizeX, gridSizeY)
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

        grid_size_y.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val gridSizeX = if (grid_size_x.text.toString().isNotEmpty()) grid_size_x.text.toString().toInt() else 1
                val gridSizeY = if (grid_size_y.text.toString().isNotEmpty()) grid_size_y.text.toString().toInt() else 1

                (activity as MainActivity).changeGridSize(gridSizeX, gridSizeY)
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })

        pixel_size_x.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val pixelSizeX = if (pixel_size_x.text.toString().isNotEmpty()) pixel_size_x.text.toString().toInt() else 1
                val pixelSizeY = if (pixel_size_y.text.toString().isNotEmpty()) pixel_size_y.text.toString().toInt() else 1

                (activity as MainActivity).changePixelSize(pixelSizeX, pixelSizeY)
            }

            override fun afterTextChanged(s: Editable?) {}

        })

        pixel_size_y.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val pixelSizeX = if (pixel_size_x.text.toString().isNotEmpty()) pixel_size_x.text.toString().toInt() else 1
                val pixelSizeY = if (pixel_size_y.text.toString().isNotEmpty()) pixel_size_y.text.toString().toInt() else 1

                (activity as MainActivity).changePixelSize(pixelSizeX, pixelSizeY)
            }

            override fun afterTextChanged(s: Editable?) {}

        })

        (activity as MainActivity).initPixelLimitRecyclerView(this.myView!!)
    }
}