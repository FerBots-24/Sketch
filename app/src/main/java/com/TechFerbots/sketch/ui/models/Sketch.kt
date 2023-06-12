package com.TechFerbots.sketch.ui.models

import android.os.Parcelable
import com.TechFerbots.sketch.data.models.SketchEntity
import kotlinx.parcelize.Parcelize

@Parcelize
data class Sketch(
    val id: Int,
    val sketchData: String? = null,
    val createdAt: String,
    var modifiedAt: String,
    var bg: String = "#00000000"
):Parcelable

fun Sketch.asSketchEntity() = SketchEntity(
    id = id,
    sketchData = sketchData,
    createdAt = createdAt,
    modifiedAt = modifiedAt,
    bg = bg
)