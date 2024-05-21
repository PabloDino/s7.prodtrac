/*
 * Copyright (C) 2021 The Android Open Source Project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.prodman.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Database class with a singleton INSTANCE object.
 */
@Database(entities = [Product::class, ProdVersion::class, ProdVersionBatch::class,
                      ProdBatchStep::class, ProdStep::class, ProdVersionStep::class,
                      Hazard::class, Measure::class, HazardMeasure::class, Step::class,
                       StepHazard::class, User::class
]                     ,
                     version = 69, exportSchema = false)

abstract class ProdRoomDatabase : RoomDatabase() {

    abstract val videoDao: VideoDao
    abstract fun productDao(): ProductDao
    abstract fun prodVersionDao(): ProdVersionDao
    abstract fun prodVersionStepDao(): ProdVersionStepDao
    abstract fun prodVersionBatchDao(): ProdVersionBatchDao
    abstract fun prodBatchStepDao(): ProdBatchStepDao
    abstract fun prodStepDao(): ProdStepDao
    abstract fun measureDao(): MeasureDao
    abstract fun hazardDao(): HazardDao
    abstract fun userDao(): UserDao

    //abstract val prodNetDao: ProductDao

    companion object {
        @Volatile
        private var INSTANCE: ProdRoomDatabase? = null

        fun getDatabase(context: Context): ProdRoomDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ProdRoomDatabase::class.java,
                    "prod_db"
                )
                    // Wipes and rebuilds instead of migrating if no Migration object.
                    // Migration is not part of this codelab.
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}