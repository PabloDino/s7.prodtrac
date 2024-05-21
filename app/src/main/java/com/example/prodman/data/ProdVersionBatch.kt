package com.example.prodman.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity
data class ProdVersionBatch(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "prodId")
    val prodId: Int,
    @ColumnInfo(name = "version")
    val version: Int,
    @ColumnInfo(name = "batch")
    val batch: Int,
    @ColumnInfo(name = "startDate")
    val startDate: String,
    @ColumnInfo(name = "finishDate")
    val finishDate: String?,
    @ColumnInfo(name = "progress")
    val progress: Int?,
    @ColumnInfo(name = "status")
    val status: String?,
    @ColumnInfo(name = "last_updated")
    val last_updated: String?
)

