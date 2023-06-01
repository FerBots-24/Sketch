package com.TechFerbots.sketch.data.roomdatabase

import android.content.Context
import androidx.lifecycle.ViewModelProvider.NewInstanceFactory.Companion.instance
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.TechFerbots.sketch.data.dao.SketchDao
import com.TechFerbots.sketch.data.models.SketchEntity


@Database(entities = [SketchEntity::class], version = 1, exportSchema = false)
abstract class SketchDatabase: RoomDatabase() {

    abstract fun sketchDao():SketchDao

    companion object{
        @Volatile
        private var INSTANCE:SketchDatabase? = null

        fun getDatabase(context: Context):SketchDatabase{
            return INSTANCE?: synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SketchDatabase::class.java,
                    "sketch_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }



}