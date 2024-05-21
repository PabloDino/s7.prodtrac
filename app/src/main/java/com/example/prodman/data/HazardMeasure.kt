

package com.example.prodman.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity
data class HazardMeasure(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "hazId")
    val hazId: String,
    @ColumnInfo(name = "measureId")
    val measureId: String,
    @ColumnInfo(name = "effectDate")
    val effectDate: String,
    @ColumnInfo(name = "last_updated")
    val last_updated: String?
)

