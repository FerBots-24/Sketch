package com.TechFerbots.sketch.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.TechFerbots.sketch.data.models.SketchEntity
import com.TechFerbots.sketch.data.models.asSketch
import com.TechFerbots.sketch.data.repositary.SketchRepository
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class SketchListingViewModel(val sketchRepository: SketchRepository):ViewModel() {

    val sketches = sketchRepository.sketches.map { it.map { it.asSketch() } }

    fun addNewSketch(sketchEntity: SketchEntity){
        viewModelScope.launch {
            sketchRepository.insertSketch(sketchEntity)
        }
    }

    fun deleteSketch(sketchEntity: SketchEntity){
        viewModelScope.launch {
            sketchRepository.deleteSketch(sketchEntity)
        }
    }

    fun updateSketch(sketchEntity: SketchEntity){
        viewModelScope.launch {
            sketchRepository.updateSketch(sketchEntity)
        }
    }



}



class SketchListingViewModelFactory(private val sketchRepository: SketchRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SketchListingViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SketchListingViewModel(sketchRepository) as T
        }
        throw IllegalArgumentException("Unknown viewmodel class")
    }
}