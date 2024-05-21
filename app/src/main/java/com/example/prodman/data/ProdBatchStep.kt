package com.example.prodman.data

import androidx.annotation.Nullable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity
data class ProdBatchStep(
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
    @ColumnInfo(name = "stepCode")
    val stepCode: String?,
    @ColumnInfo(name = "stepName")
    val stepName: String,
    @ColumnInfo(name = "checkTime")
    val checkTime: String?,
    @ColumnInfo(name = "checkedby")
    val checkby: String?,
    @ColumnInfo(name = "reading")
    val reading: Int?,
    @ColumnInfo(name = "detail")
    val detail: String?,
    @ColumnInfo(name = "risk")
    val risk: String?,
    @ColumnInfo(name = "checkedRisk")
    val checkedRisk: String?,
    @ColumnInfo(name = "last_updated")
    val last_updated: String?
)
