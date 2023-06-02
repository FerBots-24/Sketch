package com.TechFerbots.sketch.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.TechFerbots.sketch.ui.models.Sketch

@Entity
data class SketchEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "sketch_data")
    val sketchData: String? = null
)


fun SketchEntity.asSketch() = Sketch(
    id = id,
    sketchData = sketchData
)