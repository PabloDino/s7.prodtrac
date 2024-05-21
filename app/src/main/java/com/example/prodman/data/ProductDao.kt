package com.example.prodman.data


import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.prodman.domain.DProduct
import com.example.prodman.network.NetworkProduct
import kotlinx.coroutines.flow.Flow

/**
 * Database access object to access the Inventory database
 */
@Dao
interface ProductDao {


    @Query("SELECT * from Product ORDER BY name ASC")
    fun getProductList(): List<Product>

    @Query("SELECT * from Product ORDER BY name ASC")
    fun getProducts(): Flow<List<Product>>

    @Query("SELECT * from Product WHERE id = :id")
    fun getProduct(id: Int): Flow<Product>

    @Query("SELECT * from Product")
    fun getNetProducts(): LiveData<List<Product>>

    // Specify the conflict strategy as IGNORE, when the user tries to add an
    // existing Item into the database Room ignores the conflict.
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(product: Product)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll( products: List<Product>)

    @Update
    suspend fun update(product: Product)

    @Delete
    suspend fun delete(product: Product)
}
