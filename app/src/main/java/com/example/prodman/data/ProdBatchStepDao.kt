package com.example.prodman.data


import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RoomWarnings
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * Database access object to access the Product database
 */
@Dao
interface ProdBatchStepDao {

    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query("SELECT pbs.id, pbs.prodId, pbs.version, pbs.stepCode , batchId, pbs.stepId, stepName, checkedBy, " +
            "risk,checkedRisk, checkTime, pbs.last_updated, reading, detail from ProdBatchStep pbs\n" +
            //"case count(sh.id)  when 0 then \"safe\" when 1 then \"care\"  " +
            //"else \"danger\" end as risk from ProdBatchStep pbs\n" +
            " inner join ProdVersionBatch pvb " +
            " on pbs.prodId = pvb.prodId " +
            " and pbs.batchId = pvb.batch " +
            " and pvb.version = :version " +
            //"  left outer join StepHazard sh on pbs.stepId =sh.stepId   " +
            "where pbs.prodId = :prodId and batchId =:batch   " +
            "group by  pbs.prodId, batchId, pbs.stepId, stepName, checkedBy  " +
            "order by pbs.stepId")
    fun getProdBatchSteps(prodId: Int, batch:Int, version:Int): Flow<List<ProdBatchStep>>

    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query("SELECT * from ProdBatchStep " +
            "WHERE stepId = :stepId and prodId = :prodId " +
            "and batchId=:batchId")
    fun getProdBatchStep(stepId: Int, prodId:Int, batchId:Int): Flow<ProdBatchStep>

    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query("SELECT * from ProdBatchStep " +
            "WHERE stepId = :stepId and prodId = :prodId " +
            " and version = :version and batchId=:batchId")
     fun getCurrentBatchStep(prodId:Int, version:Int, batchId:Int, stepId: Int, ): ProdBatchStep

    // Specify the conflict strategy as IGNORE, when the user tries to add an
    // existing Item into the database Room ignores the conflict.
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(prodBatchStep: ProdBatchStep)


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll( prodbatchsteplist: List<ProdBatchStep>)


    @Update
    suspend fun update(prodBatchStep: ProdBatchStep)

    @Delete
    suspend fun delete(prodBatchStep: ProdBatchStep)
}
