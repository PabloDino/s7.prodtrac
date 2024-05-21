package com.example.prodman.data

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey

data class TableDates(
    @PrimaryKey(autoGenerate = true)
    val tblid: Int = 0,
    val tblName: String,
    val last_updated:String
    )
