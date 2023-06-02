package com.TechFerbots.sketch.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.TechFerbots.sketch.data.models.SketchEntity
import com.TechFerbots.sketch.data.models.asSketch
import com.TechFerbots.sketch.data.repositary.SketchRepository
import com.TechFerbots.sketch.ui.models.Sketch
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class SketchEditorViewModel (val sketchRepository: SketchRepository):ViewModel() {

    var currentSketch: Sketch? = null

    fun getSketch(id:Int):Flow<Sketch>{
        return sketchRepository.getSketch(id).map { it.asSketch() }
    }

    fun updateSketch(sketchEntity: SketchEntity){
        currentSketch = sketchEntity.asSketch()
        viewModelScope.launch {
            sketchRepository.updateSketch(sketchEntity)
        }
    }

}



class SketchEditorViewModelFactory(private val sketchRepository: SketchRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SketchEditorViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SketchEditorViewModel(sketchRepository) as T
        }
        throw IllegalArgumentException("Unknown viewmodel class")
    }
}