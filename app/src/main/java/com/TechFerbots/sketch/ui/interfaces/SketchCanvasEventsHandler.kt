package com.TechFerbots.sketch.ui.interfaces

import com.TechFerbots.sketch.ui.models.SerializablePathEvent

interface SketchCanvasEventsHandler {

    fun addPathEventToRoom(serializablePathEvent: SerializablePathEvent)

}