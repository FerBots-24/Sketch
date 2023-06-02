package com.TechFerbots.sketch.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.provider.SyncStateContract.Constants
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import com.TechFerbots.sketch.SketchApplication
import com.TechFerbots.sketch.data.models.SketchEntity
import com.example.sketch.databinding.FragmentSketchListingBinding
import com.TechFerbots.sketch.ui.activities.Editor
import com.TechFerbots.sketch.ui.adapters.SketchListRecyclerViewAdapter
import com.TechFerbots.sketch.ui.models.Sketch
import com.TechFerbots.sketch.ui.viewmodels.SketchListingViewModel
import com.TechFerbots.sketch.ui.viewmodels.SketchListingViewModelFactory
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class SketchListing : Fragment() {

    private var _binding : FragmentSketchListingBinding? = null
    val binding get() = _binding!!

    lateinit var adapter: SketchListRecyclerViewAdapter

    val sketchListingViewModel :SketchListingViewModel by activityViewModels {
        SketchListingViewModelFactory((activity?.application as SketchApplication).sketchRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                sketchListingViewModel.sketches.collect{
                    adapter.submitList(it.toList())
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSketchListingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = SketchListRecyclerViewAdapter {
            val intent = Intent(activity, Editor::class.java)
            intent.putExtra(com.TechFerbots.sketch.utils.Constants.SKETCH_ID,it)
            startActivity(intent)
        }
        binding.sketchListRv.adapter = adapter
        binding.sketchListRv.layoutManager = GridLayoutManager(requireContext(),2)

        binding.addSketchBtn.setOnClickListener {
            sketchListingViewModel.addNewSketch(SketchEntity())
        }

    }

}