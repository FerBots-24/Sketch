package com.example.sketch.ui.activities

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.sketch.R
import com.example.sketch.databinding.ActivityEditorBinding
import com.example.sketch.ui.models.SelectMode

class Editor : AppCompatActivity() {

    private var _binding :ActivityEditorBinding?= null
    val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityEditorBinding.inflate(layoutInflater)
        val view: View = binding.getRoot()
        setContentView(view)

        val sketchCanvas = binding.sketchCanvas
        binding.apply {
            drawBtn.setOnClickListener {
                Log.v("Vasi","change mode...draw")
                sketchCanvas.setSelectMode(SelectMode.DRAW)
                setBackgroundToModesTab(SelectMode.DRAW)
            }
            eraseBtn.setOnClickListener {
                Log.v("Vasi","change mode...erase")
                sketchCanvas.setSelectMode(SelectMode.ERASE)
                setBackgroundToModesTab(SelectMode.ERASE)
            }
            selectBtn.setOnClickListener {
                sketchCanvas.setSelectMode(SelectMode.SELECT)
                setBackgroundToModesTab(SelectMode.SELECT)
            }
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