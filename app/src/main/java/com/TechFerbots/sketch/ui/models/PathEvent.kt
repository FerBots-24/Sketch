package com.TechFerbots.sketch.ui.models

import android.graphics.Path
import android.view.translation.TranslationCapability

data class PathEvent(
    val path: Path,
    val selectMode: SelectMode,
    val strokeWidth: Float,
    val strokeColor: String,
    val scaleFactor: Float,
    val offsetX: Float,
    val offsetY: Float
):java.io.Serializable
