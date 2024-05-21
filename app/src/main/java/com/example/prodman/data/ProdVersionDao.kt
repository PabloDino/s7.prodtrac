package com.example.prodman.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * Database access object to access the Product database
 */
@Dao
interface ProdVersionDao {

    @Query("SELECT * from ProdVersion where prodId =:prodId order by id ASC")
    fun getProdVersions(prodId: Int): Flow<List<ProdVersion>>

    @Query("SELECT * from ProdVersion WHERE prodId = :prodId and version =:version ")
    fun getProdVersion(prodId: Int, version:Int): Flow<ProdVersion>

    // Specify the conflict strategy as IGNORE, when the user tries to add an
    // existing Item into the database Room ignores the conflict.
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(prodVersion: ProdVersion)


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll( measures: List<ProdVersion>)

    @Update
    suspend fun update(prodVersion: ProdVersion)

    @Delete
    suspend fun delete(prodVersion: ProdVersion)
}
