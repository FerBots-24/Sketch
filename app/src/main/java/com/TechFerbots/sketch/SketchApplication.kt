package com.TechFerbots.sketch

import android.app.Application
import com.TechFerbots.sketch.data.roomdatabase.SketchDatabase

class SketchApplication:Application() {

    val database:SketchDatabase by lazy {
        SketchDatabase.getDatabase(this)
    }
}