package com.example.prodman.repository


import android.graphics.Bitmap
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.asLiveData
import com.example.prodman.data.ProdRoomDatabase
import com.example.prodman.data.Product
import com.example.prodman.network.NetworkHazardMeasure
import com.example.prodman.network.NetworkMeasure
import com.example.prodman.network.NetworkProdBatchStep
import com.example.prodman.network.NetworkProdStep
import com.example.prodman.network.NetworkProdVersion
import com.example.prodman.network.NetworkProdVersionBatch
import com.example.prodman.network.NetworkProdVersionStep
import com.example.prodman.network.NetworkStep
import com.example.prodman.network.NetworkStepHazard

import com.example.prodman.network.ProdNetwork
import com.example.prodman.network.ProdNetwork.imageDownloadService
import com.example.prodman.network.ProdService
import com.example.prodman.network.asDatabaseModel
import com.squareup.moshi.JsonClass

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.BufferedReader
import java.io.File
import java.io.FileOutputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL


class ProdRepository(private val database: ProdRoomDatabase) {

    val products: LiveData<List<Product>> = database.productDao().getNetProducts()

    /*

val videos: LiveData<List<DevByteVideo>> = Transformations.map(database.videoDao.getVideos()) {
    it.asDomainModel()
}
*/
    suspend fun getPicture(image:String, savePath:String): Bitmap? {
        var bitmap:Bitmap?=null
        withContext(Dispatchers.IO) {
           /*
            val body = mapOf(
                "id" to id.toString()
            )
           */
           // bitmap = ProdNetwork.products.getPicture(body)
            //val call: Call<ResponseBody> = ProdNetwork.products.getPicture(body)
            val imageUrl:String = "http://stackseven.lat/static/uploads/"+image
            val call = imageDownloadService.getPicture(imageUrl)
            call.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        val body = response.body()

                        body?.let {
                            saveImageToDisk(it, savePath)
                        } ?: run {
                            println("Empty response body")
                        }
                    } else {
                        println("Failed to download image: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    println("Failed to download image: ${t.message}")
                }
            })

        }
        return bitmap
    }


    suspend fun refreshProducts() {
        withContext(Dispatchers.IO) {

            val body = mapOf(
                "id" to ""
            )

            val prodlist = ProdNetwork.products.getProdlist(body)
            val proddblist = prodlist.asDatabaseModel()
            database.productDao().insertAll(proddblist)
        }
    }

    suspend fun refreshHazards(prodId: Int, version: Int, batch: Int, step: Int) {
        withContext(Dispatchers.IO) {
            val body = mapOf(
                "prodid" to prodId.toString(),
                "version" to version.toString(),
                "batch" to batch.toString(),
                "step" to step.toString()
            )

            val hazlist = ProdNetwork.hazards.getHazList(body)
            val hazdblist = hazlist.asDatabaseModel()
            database.hazardDao().deleteAll()

            database.hazardDao().insertAll(hazdblist)
        }
    }


    suspend fun refreshUsers(user: String, pwd: String) {
        withContext(Dispatchers.IO) {
            val body = mapOf(
                "user" to user,
                "pwd" to pwd
            )

            val usrlist = ProdNetwork.products.getUserData(body)
            val usrdblist = usrlist.asDatabaseModel()
            database.userDao().deleteAll()

            database.userDao().insertAll(usrdblist)
        }
    }


     suspend fun checkLogin(user: String, pwd: String):Boolean {
        var retval  = false
        withContext(Dispatchers.IO) {
            val body = mapOf(
                "user" to user,
                "pwd" to pwd
            )

            val usrlist = ProdNetwork.products.getUserData(body)
            if (usrlist.users[0].result.indexOf("Incorrect username")<0) {
                retval = true
                val usrdblist = usrlist.asDatabaseModel()
                database.userDao().deleteAll()

                database.userDao().insertAll(usrdblist)
            }
            }

         return retval
    }

    suspend fun refreshMeasures() {
        withContext(Dispatchers.IO) {
            val body = mapOf(
                "id" to ""
            )

            val measlist = ProdNetwork.measures.getMeasList(body)
            val measdblist = measlist.asDatabaseModel()
            database.measureDao().insertAll(measdblist)
        }
    }


    data class NetworkHazardMeasureContainer(val hazardmeasures: List<NetworkHazardMeasure>)

    suspend fun refreshHazardMeasures() {
        withContext(Dispatchers.IO) {
            val body = mapOf(
                "id" to ""
            )

            val hazardmeasurelist = ProdNetwork.hazardmeasures.getHazardMeasureList(body)
            val hazmeasdblist = hazardmeasurelist.asDatabaseModel()
            database.hazardDao().insertAllMeasures(hazmeasdblist)
        }
    }


    suspend fun refreshProdSteps(id:Int) {
        withContext(Dispatchers.IO) {
            val body = mapOf(
                "id" to id.toString()
            )

            val prodsteplist = ProdNetwork.prodsteps.getProdStepList(body)
            val dbprodsteplist = prodsteplist.asDatabaseModel()
            database.prodStepDao().insertAll(dbprodsteplist)
        }
    }


    suspend fun refreshProdVersions(prodId: Int) {
        withContext(Dispatchers.IO) {
            val body = mapOf(
                "prodid" to prodId.toString()
            )

            val prodversionlist = ProdNetwork.prodversions.getProdVersionList(body)
            val dbprodversionlist = prodversionlist.asDatabaseModel()
            database.prodVersionDao().insertAll(dbprodversionlist)
        }
    }

    suspend fun refreshProdVersionBatches(prodId: Int, version: Int) {
        withContext(Dispatchers.IO) {
            val body = mapOf(
                "prodid" to prodId.toString(),
                "version" to version.toString()
            )

            val prodversionbatchlist = ProdNetwork.prodversionbatches.getProdVersionBatchList(body)
            val dbprodversionbatchlist = prodversionbatchlist.asDatabaseModel()
            database.prodVersionBatchDao().insertAll(dbprodversionbatchlist)
        }
    }

    suspend fun refreshProdBatchSteps(prodId: Int, version: Int, batch: Int) {
        withContext(Dispatchers.IO) {
            val body = mapOf(
                "prodid" to prodId.toString(),
                "version" to version.toString(),
                "batch" to batch.toString()
            )

            val prodbatchsteplist = ProdNetwork.prodbatchsteps.getProdBatchStepList(body)
            val dbprodbatchsteplist = prodbatchsteplist.asDatabaseModel()
            database.prodBatchStepDao().insertAll(dbprodbatchsteplist)
        }
    }

    suspend fun refreshProdVersionSteps(id:Int) {
        withContext(Dispatchers.IO) {
            val body = mapOf(
                "id" to id.toString()
            )

            val prodversionsteplist = ProdNetwork.prodversionsteps.getProdVersionStepList(body)
            val dbprodversionsteplist = prodversionsteplist.asDatabaseModel()
            database.prodVersionStepDao().insertAll(dbprodversionsteplist)
        }
    }

    suspend fun refreshSteps() {
        withContext(Dispatchers.IO) {
            val body = mapOf(
                "id" to ""
            )

            val steplist = ProdNetwork.steps.getStepList(body)
            val dbsteplist = steplist.asDatabaseModel()
            database.prodStepDao().insertAllSteps(dbsteplist)
        }
    }


    suspend fun refreshStepHazards() {
        withContext(Dispatchers.IO) {
            val body = mapOf(
                "id" to ""
            )

            val stephazardlist = ProdNetwork.stephazards.getStepHazardList(body)
            val dbstephazardlist = stephazardlist.asDatabaseModel()
            database.hazardDao().insertAllSteps(dbstephazardlist)
        }
    }


    suspend fun updateProdBatchStep(
        id: Int, prodId: Int, version: Int, batchId: Int, stepId: Int,
        reading: Int, comments: String, checkby:String, risk:String, checkedRisk:String)
     {
        withContext(Dispatchers.IO) {
            val body = mapOf(
                "sid" to id.toString(),
                "prodId" to prodId.toString(),
                "version" to version.toString(),
                "batchId" to batchId.toString(),
                "stepId" to stepId.toString(),
                "reading" to reading.toString(),
                "comments" to comments,
                "checkby" to checkby,
                "result" to risk,
                "checkedRisk" to checkedRisk
            )
            ProdNetwork.products.updateProdBatchStep(body).toString()
            //val pbsResult = ProdNetwork.products.updateProdBatchStep(body).toString()
            //return pbsResult
        }
    }


    suspend fun insertHazardFromStep(
        hid: Int, prodId: Int, version: Int, batchId: Int, stepId: Int,
        hazardName: String, hazardType: String, forStep: Int
    ) {
        withContext(Dispatchers.IO) {
            val body = mapOf(
                "hid" to hid.toString(),
                "prodId" to prodId.toString(),
                "version" to version.toString(),
                "batchId" to batchId.toString(),
                "stepId" to stepId.toString(),
                "hazardName" to hazardName,
                "forStep" to forStep.toString(),
                "hazardType" to hazardType
            )
            ProdNetwork.hazards.insertHazardFromStep(body)
            //val pbsResult = ProdNetwork.hazards.insertHazardFromStep(body)
            //return pbsResult
        }
    }
    suspend fun insertProdBatch(
        prodId: Int, version: Int
    ) {
        withContext(Dispatchers.IO) {
            val body = mapOf(
                "prodId" to prodId.toString(),
                "version" to version.toString()
            )
            ProdNetwork.hazards.insertProdBatch(body)
            //val pbsResult = ProdNetwork.hazards.insertHazardFromStep(body)
            //return pbsResult
        }
    }


    suspend fun insertMeasureFromStep(
        prodId: Int, version: Int, batchId: Int, stepId: Int,
        measureName: String, hazardId: Int, hazardName:String
    ) {
        withContext(Dispatchers.IO) {
            val body = mapOf(
                "prodId" to prodId.toString(),
                "version" to version.toString(),
                "batchId" to batchId.toString(),
                "stepId" to stepId.toString(),
                "measureName" to measureName,
                "hazardId" to hazardId.toString(),
                "hazardName" to hazardName
            )

            val pbsResult = ProdNetwork.hazards.insertMeasureFromStep(body)
            //return pbsResult
        }
    }




    fun saveImageToDisk(body: ResponseBody, savePath: String) {
        try {
            val file = File(savePath)
            val inputStream = body.byteStream()
            val outputStream = FileOutputStream(file)
            val buffer = ByteArray(4096)
            var bytesRead: Int

            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                outputStream.write(buffer, 0, bytesRead)
            }

            outputStream.flush()
            outputStream.close()
            inputStream.close()

            println("Image downloaded and saved to: $savePath")
        } catch (e: Exception) {
            println("Failed to save image: ${e.message}")
        }
    }


}


