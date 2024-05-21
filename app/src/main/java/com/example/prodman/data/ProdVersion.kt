package com.example.prodman.data


import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity
data class ProdVersion(
    @PrimaryKey
    val id: Int = 0,
    @ColumnInfo(name = "prodId")
    val prodId: Int,
    @ColumnInfo(name = "version")
    val version: Int,
    @ColumnInfo(name = "saveDate")
    val saveDate: String,
    @ColumnInfo(name = "comments")
    val comments: String,
    @ColumnInfo(name = "last_updated")
    val last_updated: String?

)
