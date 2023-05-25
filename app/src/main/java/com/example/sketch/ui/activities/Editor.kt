package com.example.sketch.ui.activities

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sketch.R
import com.example.sketch.databinding.ActivityEditorBinding
import com.example.sketch.ui.CustomViews.SketchCanvas
import com.example.sketch.ui.adapters.DrawColorsRecyclerViewAdapter
import com.example.sketch.ui.models.ChooseColor
import com.example.sketch.ui.models.SelectMode

class Editor : AppCompatActivity() {

    private var _binding :ActivityEditorBinding?= null
    val binding get() = _binding!!

    private lateinit var sketchCanvas: SketchCanvas
    private lateinit var colorsAdapter: DrawColorsRecyclerViewAdapter
    var selectedColor = "#FFFF6600"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityEditorBinding.inflate(layoutInflater)
        val view: View = binding.getRoot()
        setContentView(view)
        val colors = mutableListOf<ChooseColor>(
            ChooseColor("#FF000000"),
            ChooseColor("#FFFFFFFF"),
            ChooseColor("#FFFF0000"),
            ChooseColor("#FFFF6600", true),
            ChooseColor("#FF0000FF"),
            ChooseColor("#FF00FF00")
        )
        sketchCanvas = binding.sketchCanvas
        colorsAdapter = DrawColorsRecyclerViewAdapter(this){colorString->
            colors.find { it.colorString == selectedColor }?.isSelected = false
            colors.find { it.colorString == colorString }?.isSelected = true
            selectedColor = colorString
            sketchCanvas.setStrokeColor(colorString)
            colorsAdapter.submitList(colors.toList())
        }
        binding.apply {
            drawBtn.setOnClickListener {
                Log.v("Vasi","change mode...draw")
                sketchCanvas.setSelectMode(SelectMode.DRAW)
                setBackgroundToModesTab(SelectMode.DRAW)
                colorsLay.visibility = View.VISIBLE
                strokeWidthLay.visibility = View.VISIBLE
            }
            eraseBtn.setOnClickListener {
                Log.v("Vasi","change mode...erase")
                sketchCanvas.setSelectMode(SelectMode.ERASE)
                setBackgroundToModesTab(SelectMode.ERASE)
                colorsLay.visibility = View.GONE
                strokeWidthLay.visibility = View.VISIBLE
            }
            selectBtn.setOnClickListener {
                sketchCanvas.setSelectMode(SelectMode.SELECT)
                setBackgroundToModesTab(SelectMode.SELECT)
                colorsLay.visibility = View.GONE
                strokeWidthLay.visibility = View.GONE
            }
            binding.colorsRv.adapter = colorsAdapter
            binding.colorsRv.layoutManager = LinearLayoutManager(this@Editor, LinearLayoutManager.HORIZONTAL, false)
            colorsAdapter.submitList(colors.toList())
            thicknessSeekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    sketchCanvas.setStrokeWidth(progress.toFloat())
                    binding.strokeWidthTv.text = progress.toString()
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                }

            })
        }

    }

    fun setBackgroundToModesTab(mode: SelectMode){
        when(mode){
            SelectMode.SELECT -> {
                binding.selectBtn.background = resources.getDrawable(R.drawable.mode_enabled)
                binding.eraseBtn.background = null
                binding.drawBtn.background = null
            }
            SelectMode.DRAW -> {
                binding.selectBtn.background = null
                binding.eraseBtn.background = null
                binding.drawBtn.background = resources.getDrawable(R.drawable.mode_enabled)
            }
            SelectMode.ERASE -> {
                binding.selectBtn.background = null
                binding.eraseBtn.background = resources.getDrawable(R.drawable.mode_enabled)
                binding.drawBtn.background = null
            }
        }
    }
}