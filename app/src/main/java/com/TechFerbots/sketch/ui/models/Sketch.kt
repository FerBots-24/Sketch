package com.TechFerbots.sketch.ui.models

import com.TechFerbots.sketch.data.models.SketchEntity

data class Sketch(
    val id: Int,
    val sketchData: String? = null,
)

fun Sketch.asSketchEntity() = SketchEntity(
    id = id,
    sketchData = sketchData
)