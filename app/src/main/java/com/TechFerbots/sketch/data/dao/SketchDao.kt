package com.TechFerbots.sketch.data.dao

import androidx.room.*
import com.TechFerbots.sketch.data.models.SketchEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SketchDao {

    @Query("SELECT * from sketch")
    fun getSketches():Flow<List<SketchEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(sketchEntity: SketchEntity)

    @Update
    suspend fun update(sketchEntity: SketchEntity)

    @Delete
    suspend fun delete(sketchEntity: SketchEntity)

}