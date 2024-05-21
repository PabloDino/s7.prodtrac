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
interface ProdVersionStepDao {

    @Query("SELECT * from ProdVersionStep where prodId =:prodId and version =:version order by id ASC")
    fun getProdVersionSteps(prodId: Int, version:Int): Flow<List<ProdVersionStep>>

    @Query("SELECT * from ProdVersionStep WHERE id = :id")
    fun getProdVersionStep(id: Int): Flow<ProdVersionStep>

    // Specify the conflict strategy as IGNORE, when the user tries to add an
    // existing Item into the database Room ignores the conflict.
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(prodVersionStep: ProdVersionStep)


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll( prodversionsteps: List<ProdVersionStep>)

    @Update
    suspend fun update(prodVersionStep: ProdVersionStep)

    @Delete
    suspend fun delete(prodVersionStep: ProdVersionStep)
}
