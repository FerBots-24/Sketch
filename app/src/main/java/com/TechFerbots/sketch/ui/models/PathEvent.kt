package com.TechFerbots.sketch.ui.models

import android.graphics.Path
import android.view.translation.TranslationCapability
import java.math.BigDecimal

data class PathEvent(
    val path: Path,
    val selectMode: SelectMode,
    val strokeWidth: Float,
    val strokeColor: String,
    val scaleFactor: BigDecimal,
    val offsetX: List<Pair<Float, BigDecimal>>,
    val offsetY: List<Pair<Float, BigDecimal>>
):java.io.Serializable
