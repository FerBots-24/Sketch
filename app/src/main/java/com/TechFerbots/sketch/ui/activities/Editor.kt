package com.TechFerbots.sketch.ui.activities

import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.Path
import android.graphics.PorterDuff
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.SeekBar
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.setPadding
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.TechFerbots.sketch.SketchApplication
import com.example.sketch.databinding.ActivityEditorBinding
import com.TechFerbots.sketch.ui.CustomViews.SketchCanvas
import com.TechFerbots.sketch.ui.interfaces.SketchCanvasEventsHandler
import com.TechFerbots.sketch.ui.adapters.DrawColorsRecyclerViewAdapter
import com.TechFerbots.sketch.ui.models.SelectMode
import com.TechFerbots.sketch.ui.dialogs.ColorPickerDialog
import com.TechFerbots.sketch.ui.models.SerializablePathEventsList
import com.TechFerbots.sketch.ui.models.SerializablePathEvent
import com.TechFerbots.sketch.ui.models.asSketchEntity
import com.TechFerbots.sketch.ui.viewmodels.SketchEditorViewModel
import com.TechFerbots.sketch.ui.viewmodels.SketchEditorViewModelFactory
import com.TechFerbots.sketch.ui.viewmodels.SketchListingViewModel
import com.TechFerbots.sketch.ui.viewmodels.SketchListingViewModelFactory
import com.TechFerbots.sketch.utils.Constants
import com.TechFerbots.sketch.utils.HelperClass
import com.google.gson.Gson
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class Editor : AppCompatActivity(), SketchCanvasEventsHandler {

    private var _binding :ActivityEditorBinding?= null
    val binding get() = _binding!!

    val sketchEditorViewModel : SketchEditorViewModel by viewModels {
        SketchEditorViewModelFactory((this.application as SketchApplication).sketchRepository)
    }

    private lateinit var sketchCanvas: SketchCanvas
    private lateinit var colorsAdapter: DrawColorsRecyclerViewAdapter
    var selectedColorPosition = 0
    val colorsViews = mutableListOf<ImageView>()
    var colors = mutableListOf<String>()
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

    var tempstring = ""
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
        sketchCanvas.sketchCanvasEventsHandler = this
        sketchCanvas.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                sketchEditorViewModel.getSketch(intent.getIntExtra(Constants.SKETCH_ID, 0 )).collect{sketch->
                    Log.v("Vasi testing","get sketch collect...${Gson().toJson(sketch)}")
                    if (sketchEditorViewModel.currentSketch == null){
                        sketchEditorViewModel.currentSketch = sketch
                        sketch.sketchData?.let { sPathEventsList->
                            deSerializePathEventList(sPathEventsList)
                        }
                    }
                    else if (sketchEditorViewModel.currentSketch!!.sketchData != sketch.sketchData){
                        sketch.sketchData?.let { sPathEventsList->
                            deSerializePathEventList(sPathEventsList)
                        }
                    }
                }
            }
        }
        setColorsTabAndOnClickListeners()
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
            undoBtn.setOnClickListener {
                sketchCanvas.undo()
            }
            redoBtn.setOnClickListener {
                sketchCanvas.redo()
            }
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
                    colorsViews[selectedColorPosition].setColorFilter(Color.parseColor(colorString), PorterDuff.Mode.MULTIPLY)
                    sketchCanvas.setStrokeColor(colorString)
                    colors[selectedColorPosition] = colorString
                }.show(supportFragmentManager,"color picker")

            }
            saveBtn.setOnClickListener {
                sketchEditorViewModel.currentSketch?.sketchData?.let { it1 ->
                    deSerializePathEventList(
                        it1
                    )
                }
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

    override fun addPathEventToRoom(serializablePathEvent: SerializablePathEvent) {
        sketchEditorViewModel.currentSketch?.let { sketch ->
            if (sketch.sketchData == null){
                val sPathEventsList = SerializablePathEventsList(pathEventsList = arrayListOf(serializablePathEvent))
                sketchEditorViewModel.updateSketch(sketch.copy(sketchData = Gson().toJson(sPathEventsList)).asSketchEntity())
            }
            else{
                val sPathEventsList = HelperClass.gson.fromJson(sketch.sketchData, SerializablePathEventsList::class.java)
                sPathEventsList.pathEventsList.add(serializablePathEvent)
                sketchEditorViewModel.updateSketch(sketch.copy(sketchData = Gson().toJson(sPathEventsList)).asSketchEntity())
            }
        }

    }

    fun deSerializePathEventList(pathEventListJson:String){
        sketchCanvas.setDeScaledPathEventList(
            HelperClass.gson.fromJson(pathEventListJson, SerializablePathEventsList::class.java)
        )
    }


    override fun onStop() {
        super.onStop()
        val currentTime = LocalDateTime.now().format(formatter)
        sketchEditorViewModel.currentSketch?.let {
            sketchEditorViewModel.updateSketch(it.copy(modifiedAt = currentTime.toString()).asSketchEntity())
        }
    }


}