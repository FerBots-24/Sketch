package com.TechFerbots.sketch.ui.models

import android.graphics.Path

data class SerializablePathEvent(
    val path: MutableList<PathUserAction>,
    val selectMode: SelectMode,
    val strokeWidth: Float,
    val strokeColor: String,
    val scaleFactor: Float,
    val offsetX:Float,
    val offsetY:Float,
    val width: Int,
    val height: Int
):java.io.Serializable
