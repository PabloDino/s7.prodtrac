
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
interface ProdStepDao {
   /*
    @Query("SELECT pbs.prodId, pbs.version, pbs.batchId, pbs.stepId, stepName, checkedBy,sh.id, " +
            "risk as verdict " +
            "from ProdBatchStep pbs\n" +
            "  left outer join StepHazard sh on pbs.stepId =sh.stepId   " +
            "  where pbs.prodId =:prodId "+
            "        and pbs.version = :version " +
            "group by  pbs.prodId, pbs.batchId, pbs.stepId, stepName, checkedBy ")
    fun getProdSteps(prodId: Int, version:Int): Flow<List<ProdStep>>
    */
    @Query("SELECT * from ProdStep WHERE id = :id")
    fun getProdStep(id: Int): Flow<ProdStep>

    // Specify the conflict strategy as IGNORE, when the user tries to add an
    // existing Item into the database Room ignores the conflict.
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(prodStep: ProdStep)


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll( prodsteps: List<ProdStep>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllSteps( steps: List<Step>)


    @Update
    suspend fun update(prodStep: ProdStep)

    @Delete
    suspend fun delete(prodStep: ProdStep)
}
