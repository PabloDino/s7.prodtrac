
package com.example.prodman.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity
data class Hazard(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "hazardid")
    val hazardid: Int,
@ColumnInfo(name = "prodId")
val prodId: Int?,
@ColumnInfo(name = "version")
val version: Int,
@ColumnInfo(name = "batch")
val batch: Int?,
@ColumnInfo(name = "stepId")
val stepId: Int?,
@ColumnInfo(name = "type")
val type: String,
@ColumnInfo(name = "name")
val name: String,
@ColumnInfo(name = "description")
    val description: String,
    @ColumnInfo(name = "justification")
    val justification: String?,

    @ColumnInfo(name = "measureName")
    val measureName: String?,
    @ColumnInfo(name = "rank")
    val rank: Int?,
    @ColumnInfo(name = "cat_bio")
    val cat_bio: Int?,
    @ColumnInfo(name = "cat_chem")
    val cat_chem: Int?,
    @ColumnInfo(name = "cat_phys")
    val cat_phys: Int?,
    @ColumnInfo(name = "last_updated")
    val last_updated: String?,
    @ColumnInfo(name = "measureId")
    val measureId: Int?,
    @ColumnInfo(name = "forStep")
    val forStep: Int?,
    @ColumnInfo(name = "handled")
    val handled: Int?
)


