package com.example.prodman.data


import androidx.lifecycle.LiveData
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
interface ProdVersionBatchDao {

    //@Query("SELECT * from ProdVersionBatch WHERE prodId = :prodId and version = :versionNo  ORDER BY id ASC")
    @Query("select b.id, b.prodId ,b.version,b.batch,b.startDate,b.finishDate, " +
            "case when count(bs.checkedBy)= 0 then 'new'" +
            " when count(bs.checkedBy)> 0 and count(bs.batchId)= count(bs.checkedBy) then 'done'" +
            " else 'inprogress' end as status , bs.last_updated, " +
            "round(100 * count(bs.checkedBy)/count(bs.batchId),0) as progress " +
            "from ProdVersionBatch b left outer join ProdBatchStep bs " +
            "on b.id = bs.batchId and b.prodId = bs.prodId " +
            "WHERE b.prodId = :prodId and b.version = :versionNo " +
            "group by bs.batchid ORDER BY b.id ASC")

    fun getProdVersionBatches(prodId: Int, versionNo: Int): Flow<List<ProdVersionBatch>>

    @Query("SELECT * from ProdVersionBatch WHERE prodId = :prodId " +
            "and version = :versionNo and batch = :batch")
    fun getProdVersionBatch(prodId: Int, versionNo: Int, batch: Int): Flow<ProdVersionBatch>


    @Query("SELECT max(batch) as maxBatch from ProdVersionBatch group by  prodId, version " +
            "having prodId =:prodId and version =:version")

    fun getLastBatch(prodId: Int, version:Int):Int


    @Query("Insert into ProdBatchStep (prodId, batchId,stepId,stepName, version) " +
            "select ps.prodId, :batch, ps.stepId, s.name, 1 " +
            " from ProdStep ps " +
            "   inner join Step s on ps.stepId = s.id " +
             "   where ps.prodId = :prodId "  )
   // +  "     and version = :version"
    //suspend fun insertNewBatchSteps(prodId: Int, version:Int, batch:Int)
    suspend fun insertNewBatchSteps(prodId: Int, batch:Int)



    // Specify the conflict strategy as IGNORE, when the user tries to add an
    // existing Item into the database Room ignores the conflict.
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(prodVersionBatch: ProdVersionBatch)


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll( measures: List<ProdVersionBatch>)

    @Update
    suspend fun update(prodVersionBatch: ProdVersionBatch)

    @Delete
    suspend fun delete(prodVersionBatch: ProdVersionBatch)
}
