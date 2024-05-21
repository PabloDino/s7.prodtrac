
package com.example.prodman.data


import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * Database access object to access the Inventory database
 */
@Dao
interface HazardDao {


  /*


    @Query("WITH H0 as (" +
            " Select h.id,type,name, description,justification, " +
            "   '' as measureName, cat_bio, cat_chem, cat_phys, 0 as rank from Hazard  h" +
            "                                   inner join StepHazard sh on h.id = sh.hazId" +
            "                             where sh.stepId =:step" +
             ")SELECT h.id,h.type,h.name, h.description, h.justification, m.description as measureName, " +
            "cat_bio,  cat_chem, cat_phys, " +
            "  " +
            "(SELECT COUNT(*) + 1 FROM HazardMeasure AS m0 " +
            "WHERE m0.hazId = hm.hazId AND m0.measureId > m.id) as rank " +
            "from Hazard h inner join StepHazard sh on h.id = sh.hazId " +
            "inner join HazardMeasure hm on h.id = hm.hazId " +
            "inner join  Measure m on m.id = hm.measureId " +
            "where sh.stepId =:step       " +
            "union \n" +
            "        SELECT id,type,name, description, measureName, " +
            "                     justification, cat_bio," +
            "                     cat_chem, cat_phys, rank from H0" +
            "      ORDER BY h.id, rank ASC;")

*/

    @Query("SELECT rank, hazardId, h.id,h.prodId, h.version, h.batch,h.stepId, " +
            "h.type,h.name, h.description, h.justification, measureName, " +
            "cat_bio,  cat_chem, cat_phys, h.last_updated,h.forStep, h.measureId , " +
            "h.handled  " +
            "from Hazard h  " +
            "where h.stepId =:step and h.prodId=:prodId " +
            " and h.version=:version and h.batch=:batch"  +

            "      ORDER BY hazardid, rank ASC;")
    fun getStepHazards(prodId:Int, version:Int, batch:Int, step: Int): Flow<List<Hazard>>


    @Query("SELECT rank, hazardId, h.id,h.prodId, h.version, h.batch,h.stepId, " +
            "h.type,h.name, h.description, h.justification, measureName, " +
            "cat_bio,  cat_chem, cat_phys, h.last_updated, h.measureId, h.forStep,  " +
            " h.handled " +
            "from Hazard h  " +
            "where h.stepId =:step and h.prodId=:prodId " +
            " and h.version=:version and h.batch=:batch"  +

            "      ORDER BY hazardid, rank ASC;")
    fun getStepHazardList(prodId:Int, version:Int, batch:Int, step: Int): List<Hazard>

    @Query("SELECT * from Hazard WHERE id = :id")
    fun getHazard(id: Int): Hazard

    @Query("SELECT max(rank) as rank from Hazard WHERE hazardid = :id")
    fun getNextRankForHazard(id: Int): Int

    @Transaction
    suspend fun clearMeasure(measureId:Int, hazardId:Int) {
        // Step 1: Update user information
        unsetMeasure(measureId, hazardId)

        // Step 2: Update user's related data
        setHazStatus(hazardId)

    }



    @Transaction
    suspend fun handleMeasure(measureId:Int, hazardId:Int) {
        // Step 1: Update user information
        updateMeasure(measureId, hazardId)

        // Step 2: Update user's related data
        setHazStatus(hazardId)

         }

    @Query("UPDATE Hazard SET handled =0 WHERE measureId = :measureId and  hazardId = :hazardId")
    suspend fun unsetMeasure(measureId: Int,hazardId: Int )


    @Query("UPDATE Hazard SET handled =1 WHERE measureId = :measureId and  hazardId = :hazardId")
    suspend fun updateMeasure(measureId: Int,hazardId: Int )


    @Query("update Hazard set handled = " +
            "      case when " +
            "         (select sum(handled) from Hazard " +
            "             where rank = 1 and hazardid = :hazardId) = 0 " +
            "          then 0 " +
            "          else 1 " +
            "      end   " +
            "    where rank =0 and hazardId = :hazardId")
    suspend fun setHazStatus(hazardId: Int)
    // Specify the conflict strategy as IGNORE, when the user tries to add an
    // existing Item into the database Room ignores the conflict.

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(hazard: Hazard)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertStep(stepHazard: StepHazard)



    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll( hazards: List<Hazard>)


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllMeasures( hazardmeasures: List<HazardMeasure>)


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllSteps( hazardsteps: List<StepHazard>)

    @Update
    suspend fun update(hazard: Hazard)

    @Delete
    suspend fun delete(hazard: Hazard)

    @Query("DELETE FROM Hazard")
    fun deleteAll()
}
