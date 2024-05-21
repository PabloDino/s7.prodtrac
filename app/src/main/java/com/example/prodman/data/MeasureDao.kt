

package com.example.prodman.data


import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * Database access object to access the Inventory database
 */
@Dao
interface MeasureDao {

    @Query("SELECT m.id, name, description, m.last_updated from Measure m " +
            "inner join HazardMeasure hm on m.id = hm.measureId " +
            "order by hm.hazId")

    fun getAllMeasures(): Flow<List<Measure>>

    @Query("SELECT m.id, name, description, m.last_updated from Measure m " +
            "inner join HazardMeasure hm on m.id = hm.measureId " +
            "where hm.hazId = :hazId")
    fun getHazardMeasures(hazId: Int): Flow<List<Measure>>

    @Query("SELECT * from Measure WHERE id = :id")
    fun getMeasure(id: Int): Flow<Measure>

    // Specify the conflict strategy as IGNORE, when the user tries to add an
    // existing Item into the database Room ignores the conflict.
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(measure: Measure)


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll( measures: List<Measure>)


    @Update
    suspend fun update(measure: Measure)

    @Delete
    suspend fun delete(measure: Measure)
}
