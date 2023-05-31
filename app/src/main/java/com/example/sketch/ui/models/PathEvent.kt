package com.example.sketch.ui.models

import android.graphics.Path

data class PathEvent(
    val path: Path,
    val selectMode: SelectMode,
    val strokeWidth: Float,
    val strokeColor: String,
)
