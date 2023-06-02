package com.TechFerbots.sketch.data.repositary

import com.TechFerbots.sketch.data.dao.SketchDao
import com.TechFerbots.sketch.data.models.SketchEntity
import kotlinx.coroutines.flow.Flow

class SketchRepository(val sketchDao: SketchDao) {

    val sketches = sketchDao.getSketches()

    fun getSketch(id:Int):Flow<SketchEntity>{
        return sketchDao.getSketch(id)
    }

    suspend fun insertSketch(sketch: SketchEntity){
        sketchDao.insert(sketch)
    }

    suspend fun deleteSketch(sketch: SketchEntity){
        sketchDao.delete(sketch)
    }

    suspend fun updateSketch(sketch: SketchEntity){
        sketchDao.update(sketch)
    }

}