
package com.example.prodman.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity
data class StepHazard(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "prodId")
    val prodId: Int,
    @ColumnInfo(name = "version")
    val version: Int,
    @ColumnInfo(name = "batchId")
    val batchId: Int,
    @ColumnInfo(name = "stepId")
    val stepId: Int,
    @ColumnInfo(name = "hazId")
    val hazId: Int,
    @ColumnInfo(name = "recordDate")
    val recordDate: String?,
    @ColumnInfo(name = "last_updated")
    val last_updated: String?,
    @ColumnInfo(name = "hazardName")
    val hazardName: String?,
    @ColumnInfo(name = "measureId")
    val measureId: Int?
   )