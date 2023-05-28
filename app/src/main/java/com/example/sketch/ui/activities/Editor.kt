package com.example.sketch.ui.activities

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.setPadding
import com.example.sketch.R
import com.example.sketch.databinding.ActivityEditorBinding
import com.example.sketch.ui.CustomViews.SketchCanvas
import com.example.sketch.ui.adapters.DrawColorsRecyclerViewAdapter
import com.example.sketch.ui.models.SelectMode
import com.example.sketch.ui.dialogs.ColorPickerDialog
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener


class Editor : AppCompatActivity() {

    private var _binding :ActivityEditorBinding?= null
    val binding get() = _binding!!

    private lateinit var sketchCanvas: SketchCanvas
    private lateinit var colorsAdapter: DrawColorsRecyclerViewAdapter
    var selectedColorPosition = 0
    val colorsViews = mutableListOf<ImageView>()
    var colors = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityEditorBinding.inflate(layoutInflater)
        val view: View = binding.getRoot()
        setContentView(view)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        colors = mutableListOf<String>(
            "#FF000000",
            "#FFFFFFFF",
            "#FFFF0000",
            "#FFFF6600",
            "#FF0000FF",
           "#FF00FF00"
        )
        colorsViews.addAll(
            listOf(
                binding.color1Iv,
                binding.color2Iv,
                binding.color3Iv,
                binding.color4Iv,
                binding.color5Iv,
                binding.color6Iv
            )
        )
        sketchCanvas = binding.sketchCanvas
        sketchCanvas.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
        setColorsTabAndOnClickListeners()
//        colorsAdapter = DrawColorsRecyclerViewAdapter(this){position->
//            colors[position] = colors[position].copy(isSelected = true)
//            if (position != selectedColorPosition){
//                colors[selectedColorPosition] = colors[selectedColorPosition].copy(isSelected = false)
//            }
//            selectedColorPosition = position
//            sketchCanvas.setStrokeColor(colors[position].colorString)
//            colorsAdapter.submitList(colors.toList())
//        }
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
//            colorsAdapter.submitList(colors.toList())
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
            swapColorBtn.setOnClickListener {

                ColorPickerDialog {
                    val colorString = "#${it}"
                    Log.v("Vasi testing","swapped color received...${it}")
                    colorsViews[selectedColorPosition].setColorFilter(Color.parseColor(colorString), android.graphics.PorterDuff.Mode.MULTIPLY)
                    sketchCanvas.setStrokeColor(colorString)
                    colors[selectedColorPosition] = colorString
                }.show(supportFragmentManager,"color picker")

//             ColorPickerDialog.Builder(this@Editor)
//                    .setTitle("ColorPicker Dialog")
//                    .setPreferenceName("MyColorPickerDialog")
//                    .setPositiveButton(getString(com.example.sketch.R.string.confirm),
//                        ColorEnvelopeListener { envelope, fromUser ->
//                            Log.v("Vasi testing"," color...${envelope}")
//                        }
//                    )
//                    .setNegativeButton(
//                        getString(com.example.sketch.R.string.cancel)
//                    ) { dialogInterface, i -> dialogInterface.dismiss() }
//                    .attachAlphaSlideBar(true) // the default value is true.
//                    .attachBrightnessSlideBar(true) // the default value is true.
//                    .setBottomSpace(12) // set a bottom space between the last slidebar and buttons.
//                    .show()
            }
        }

    }

    fun setColorsTabAndOnClickListeners(){
        val density = resources.displayMetrics.density
        val selectedPadding = (5 * density).toInt()
        val unselectedPadding = (9 * density).toInt()

        binding.apply {
            color1Iv.setColorFilter(Color.parseColor(colors[0]), android.graphics.PorterDuff.Mode.MULTIPLY)
            color2Iv.setColorFilter(Color.parseColor(colors[1]), android.graphics.PorterDuff.Mode.MULTIPLY)
            color3Iv.setColorFilter(Color.parseColor(colors[2]), android.graphics.PorterDuff.Mode.MULTIPLY)
            color4Iv.setColorFilter(Color.parseColor(colors[3]), android.graphics.PorterDuff.Mode.MULTIPLY)
            color5Iv.setColorFilter(Color.parseColor(colors[4]), android.graphics.PorterDuff.Mode.MULTIPLY)
            color6Iv.setColorFilter(Color.parseColor(colors[5]), android.graphics.PorterDuff.Mode.MULTIPLY)
        }

        binding.apply {
            color1.setOnClickListener {
                colorsViews.forEach {
                    it.setPadding(unselectedPadding)
                }
                binding.color1Iv.setPadding(selectedPadding)
                sketchCanvas.setStrokeColor(colors[0])
                selectedColorPosition = 0
            }
            color2.setOnClickListener {
                colorsViews.forEach {
                    it.setPadding(unselectedPadding)
                }
                binding.color2Iv.setPadding(selectedPadding)
                sketchCanvas.setStrokeColor(colors[1])
                selectedColorPosition = 1
            }
            color3.setOnClickListener {
                colorsViews.forEach {
                    it.setPadding(unselectedPadding)
                }
                binding.color3Iv.setPadding(selectedPadding)
                sketchCanvas.setStrokeColor(colors[2])
                selectedColorPosition = 2
            }
            color4.setOnClickListener {
                colorsViews.forEach {
                    it.setPadding(unselectedPadding)
                }
                binding.color4Iv.setPadding(selectedPadding)
                sketchCanvas.setStrokeColor(colors[3])
                selectedColorPosition = 3
            }
            color5.setOnClickListener {
                colorsViews.forEach {
                    it.setPadding(unselectedPadding)
                }
                binding.color5Iv.setPadding(selectedPadding)
                sketchCanvas.setStrokeColor(colors[4])
                selectedColorPosition = 4
            }
            color6.setOnClickListener {
                colorsViews.forEach {
                    it.setPadding(unselectedPadding)
                }
                binding.color6Iv.setPadding(selectedPadding)
                sketchCanvas.setStrokeColor(colors[5])
                selectedColorPosition = 5
            }
            //selecting color initially.
            colorsViews.forEach {
                it.setPadding(unselectedPadding)
            }
            binding.color6Iv.setPadding(selectedPadding)
            sketchCanvas.setStrokeColor(colors[5])
            selectedColorPosition = 5
        }
    }

    fun setBackgroundToModesTab(mode: SelectMode){
        when(mode){
            SelectMode.SELECT -> {
                binding.selectBtn.background = resources.getDrawable(com.example.sketch.R.drawable.mode_enabled)
                binding.eraseBtn.background = null
                binding.drawBtn.background = null
            }
            SelectMode.DRAW -> {
                binding.selectBtn.background = null
                binding.eraseBtn.background = null
                binding.drawBtn.background = resources.getDrawable(com.example.sketch.R.drawable.mode_enabled)
            }
            SelectMode.ERASE -> {
                binding.selectBtn.background = null
                binding.eraseBtn.background = resources.getDrawable(com.example.sketch.R.drawable.mode_enabled)
                binding.drawBtn.background = null
            }
        }
    }
}