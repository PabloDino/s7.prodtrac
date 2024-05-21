package com.example.prodman.data


import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity
data class ProdVersionStep(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "version")
    val version: Int,
    @ColumnInfo(name = "prodId")
    val prodId: Int,

    @ColumnInfo(name = "stepId")
    val stepId: Int,
    @ColumnInfo(name = "sequence")
    val sequence: Int,
    @ColumnInfo(name = "pred")
    val pred: Int,
    @ColumnInfo(name = "last_updated")
    val last_updated: String?


)
