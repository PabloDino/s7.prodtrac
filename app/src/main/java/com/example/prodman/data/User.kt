package com.example.prodman.data



import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "result")
    val result: String,
    @ColumnInfo(name = "username")
    val username: String,
    @ColumnInfo(name = "role")
    val role: String,
    @ColumnInfo(name = "logtime")
    val logtime: String,
    @ColumnInfo(name = "prevLoginTime")
    val prevLoginTime: String
)
