package com.TechFerbots.sketch

import android.app.Application
import com.TechFerbots.sketch.data.repositary.SketchRepository
import com.TechFerbots.sketch.data.roomdatabase.SketchDatabase

class SketchApplication:Application() {

    val database:SketchDatabase by lazy {
        SketchDatabase.getDatabase(this)
    }

    val sketchRepository:SketchRepository by lazy {
        SketchRepository(database.sketchDao())
    }
}