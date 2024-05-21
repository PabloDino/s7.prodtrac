package com.example.prodman.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity
data class Product(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "name")
    val prodName: String,
    @ColumnInfo(name = "packaging")
    val packaging: String,
    @ColumnInfo(name = "purpose")
    val purpose: String,
    @ColumnInfo(name = "shelfLife")
    val shelfLife: String,
    @ColumnInfo(name = "netWeight")
    val netWeight: String,
    @ColumnInfo(name = "labeling")
    val labeling: String,
    @ColumnInfo(name = "description")
    val description: String,
    @ColumnInfo(name = "updated")
    val updated: Int,
    @ColumnInfo(name = "picture")
    val picture: String?,
    @ColumnInfo(name = "created")
    val created: String?,
    @ColumnInfo(name = "last_updated")
    val last_updated: String?


)