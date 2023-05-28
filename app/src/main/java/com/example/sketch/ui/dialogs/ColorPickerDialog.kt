package com.example.sketch.ui.dialogs

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.sketch.R
import com.example.sketch.databinding.DialogColorPickerBinding
import com.skydoves.colorpickerview.ColorEnvelope
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener
import com.skydoves.colorpickerview.listeners.ColorPickerViewListener


class ColorPickerDialog(val swapColors:(String)-> Unit) : DialogFragment() {

    private var _binding :DialogColorPickerBinding? = null
    val binding get() = _binding!!

    var currentColor = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DialogColorPickerBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.colorPickerView.attachAlphaSlider(binding.alphaSlideBar)
        binding.colorPickerView.attachBrightnessSlider(binding.brightnessSlideBar)

        binding.swapBtn.setOnClickListener {
            Log.v("Vasi testing","color swapped...${binding.colorPickerView.color}")
            swapColors(currentColor)
            dismiss()
        }
        binding.cancelBtn.setOnClickListener {
            dismiss()
        }

        binding.colorPickerView.setColorListener(object :ColorEnvelopeListener{
            override fun onColorSelected(envelope: ColorEnvelope?, fromUser: Boolean) {
                Log.v("Vasi testing","color selected...${envelope?.color}")
                envelope?.let {
                    currentColor = it.hexCode
                }
            }

        })


    }

}