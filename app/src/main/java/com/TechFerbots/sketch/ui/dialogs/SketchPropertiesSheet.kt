package com.TechFerbots.sketch.ui.dialogs

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavArgs
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.TechFerbots.sketch.SketchApplication
import com.TechFerbots.sketch.ui.activities.Editor
import com.TechFerbots.sketch.ui.models.Sketch
import com.TechFerbots.sketch.ui.models.asSketchEntity
import com.TechFerbots.sketch.ui.viewmodels.SketchListingViewModel
import com.TechFerbots.sketch.ui.viewmodels.SketchListingViewModelFactory
import com.example.sketch.R
import com.example.sketch.databinding.FragmentSketchPropertiesSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class SketchPropertiesSheet : BottomSheetDialogFragment() {

    private var _binding : FragmentSketchPropertiesSheetBinding? = null
    val binding get() = _binding!!
    val safeArgs:SketchPropertiesSheetArgs by navArgs()

    val sketchListingViewModel : SketchListingViewModel by activityViewModels {
        SketchListingViewModelFactory((activity?.application as SketchApplication).sketchRepository)
    }

    private var currentSketch: Sketch? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSketchPropertiesSheetBinding.inflate(inflater, container, false)
        currentSketch = safeArgs.sketch
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.editBtn.setOnClickListener {
            currentSketch?.let {
                val intent = Intent(activity, Editor::class.java)
                intent.putExtra(com.TechFerbots.sketch.utils.Constants.SKETCH_ID,it.id)
                startActivity(intent)
                dismiss()
            }
        }
        binding.deleteBtn.setOnClickListener {
            currentSketch?.let { sketch -> sketchListingViewModel.deleteSketch(sketch.asSketchEntity()) }
            dismiss()
        }
    }

}