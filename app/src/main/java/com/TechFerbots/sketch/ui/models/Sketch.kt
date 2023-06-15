package com.TechFerbots.sketch.ui.models

import android.graphics.Bitmap
import android.os.Parcelable
import com.TechFerbots.sketch.data.models.SketchEntity
import kotlinx.parcelize.Parcelize

@Parcelize
data class Sketch(
    val id: Int,
    val sketchTitle: String,
    val sketchData: String? = null,
    val createdAt: String,
    var modifiedAt: String,
    var bg: String = "#00000000",
    var thumbnailBitmap: Bitmap? = null
):Parcelable

fun Sketch.asSketchEntity() = SketchEntity(
    id = id,
    sketchTitle = sketchTitle,
    sketchData = sketchData,
    createdAt = createdAt,
    modifiedAt = modifiedAt,
    bg = bg,
    thumbnailBitmap = thumbnailBitmap
)