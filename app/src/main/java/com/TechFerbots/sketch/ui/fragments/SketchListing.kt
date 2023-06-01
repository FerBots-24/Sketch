package com.TechFerbots.sketch.ui.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.example.sketch.databinding.FragmentSketchListingBinding
import com.TechFerbots.sketch.ui.activities.Editor
import com.TechFerbots.sketch.ui.adapters.SketchListRecyclerViewAdapter
import com.TechFerbots.sketch.ui.models.Sketch

class SketchListing : Fragment() {

    private var _binding : FragmentSketchListingBinding? = null
    val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

        val adapter = SketchListRecyclerViewAdapter {
//            val action = SketchListingDirections.actionSketchListingToSketchEditor()
//            findNavController().navigate(action)
            val intent = Intent(activity, Editor::class.java)
            startActivity(intent)
        }

        binding.sketchListRv.adapter = adapter
        binding.sketchListRv.layoutManager = GridLayoutManager(requireContext(),2)

        adapter.submitList(
            listOf(
                Sketch(
                    id = 0,
                    name = "chumma"
                ),
                Sketch(
                    id = 1,
                    name = "chumma"
                ),
                Sketch(
                    id = 2,
                    name = "chumma"
                ),
                Sketch(
                    id = 3,
                    name = "chumma"
                ),
                Sketch(
                    id = 4,
                    name = "chumma"
                ),
                Sketch(
                    id = 5,
                    name = "chumma"
                ),
            )
        )

    }

}