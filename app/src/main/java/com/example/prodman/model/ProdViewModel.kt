package com.example.prodman.model

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.room.Transaction
import com.example.prodman.ProductApplication
import com.example.prodman.data.Hazard
import com.example.prodman.data.HazardDao
import com.example.prodman.data.Measure
import com.example.prodman.data.MeasureDao
import com.example.prodman.data.ProdBatchStep
import com.example.prodman.data.ProdBatchStepDao
import com.example.prodman.data.ProdVersion
import com.example.prodman.data.ProdVersionBatch
import com.example.prodman.data.ProdVersionBatchDao
import com.example.prodman.data.ProdVersionDao
import com.example.prodman.data.Product
import com.example.prodman.data.ProductDao
import com.example.prodman.data.StepHazard
import com.example.prodman.data.User
import com.example.prodman.data.UserDao
import com.example.prodman.repository.ProdRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import okhttp3.ConnectionSpec
import okhttp3.OkHttpClient
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.Arrays
import java.util.Date
import java.util.Locale


class ProdViewModel(private val productDao: ProductDao,
                    private val prodVersionDao: ProdVersionDao,
                    private val prodVersionBatchDao: ProdVersionBatchDao,
                    private val prodBatchStepDao: ProdBatchStepDao,
                    private val hazardDao: HazardDao,
                    private val measureDao: MeasureDao,
                    private val userDao: UserDao,
                    productApplication:ProductApplication
) : ViewModel() {


    private val prodRepository = ProdRepository(productApplication.database)

    var prodlist = prodRepository.products
    var stepLock:String=""
    //Log.d("DownloadFolder", "Path: $downloadPath")

    val downloadDir =
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
    val downloadPath = downloadDir.absolutePath

    // Now you can use 'downloadPath' to reference the download folder

    init {
        // Permission is granted
        Log.d("DownloadFolder", "Path: $downloadPath")

        // TODO: Replace with a call to the refreshDataFromRepository9) method
        refreshDataFromRepository(productApplication.applicationContext)
    }


    private var _fragmentName: String = ""
    var fragmentName
        get() = _fragmentName
        set(value) {
            _fragmentName = value

        }


    private var _listening: Boolean = false
    var listening
        get() = _listening
        set(value) {
            _listening = value
        }

    private var _picture: String = ""
    var picture
        get() = _picture
        set(value) {
            _picture = value
        }

    private var _prodId: Int = 0
    var prodId: Int
        get() = _prodId
        set(value) {
            _prodId = value
        }
    private var _prodname: String = ""
    var prodName: String
        get() = _prodname
        set(value) {
            _prodname = value
        }


    private var _packaging: String = ""
    var packaging: String
        get() = _packaging
        set(value) {
            _packaging = value
        }
    private var _labelling: String = ""
    var labelling: String
        get() = _labelling
        set(value) {
            _labelling = value
        }

    private var _version: Int = 0
    var version: Int
        get() = _version
        set(value) {
            _version = value
        }
    private var _comments: String = ""
    var comments: String
        get() = _comments
        set(value) {
            _comments = value
        }

    private var _batch: Int = 0
    var batch: Int
        get() = _batch
        set(value) {
            _batch = value
        }
    private var _startDate: String = ""
    var startDate: String
        get() = _startDate
        set(value) {
            _startDate = value
        }


    private var _stepId: Int = 0
    var stepId: Int
        get() = _stepId
        set(value) {
            _stepId = value
        }


    private var _stepName: String = ""
    var stepName: String
        get() = _stepName
        set(value) {
            _stepName = value
        }

    // Cache all items form the database using LiveData.
    val allProducts: LiveData<List<Product>> = productDao.getProducts().asLiveData()

    private var _currentProduct: MutableLiveData<Product> = MutableLiveData<Product>()
    val currentProduct: LiveData<Product>
        get() = _currentProduct

    var _stepInputMode: String = "none"
    var stepInputMode: String
        get() = _stepInputMode
        set(value) {
            _stepInputMode = value
        }

    /**
     * Retrieve an item from the repository.
     */
    fun retrieveProduct(id: Int): LiveData<Product> {
        var prodInfo = productDao.getProduct(id).asLiveData()
        viewModelScope.launch {
            //if (prodInfo?.value?.last_updated=="")
            //{
                try {
                    prodRepository.refreshProdVersions(id)
                    prodRepository.refreshProdSteps(id)
                    prodRepository.refreshSteps()

                    _eventNetworkError.value = false
                    _isNetworkErrorShown.value = false

                } catch (networkError: IOException) {
                    if (prodlist.value.isNullOrEmpty())
                        _eventNetworkError.value = true
                }


            }


        //}
        return prodInfo
    }

   private var loggedIn:Boolean = false
    public var modelUsername = ""
    public var modelRole = ""
    private var modellogtime = ""
    private var modelPreviousLog = ""
    private var modelPass =""

    public fun checkPass(pass:String):Boolean{
        var passOk = runBlocking {
            prodRepository.checkLogin(modelUsername, pass)
        }


        return passOk
    }

    public fun attemptLogin(username: String, password: String, context: Context): Boolean {
        //val userDao = database.userDao()
        var userInfo:User? = userDao.getUser()

        if (userInfo != null) {


            modelUsername = userInfo.username!!
            modelRole = userInfo.role!!
            modellogtime= userInfo.logtime!!
            modelPreviousLog = userInfo.prevLoginTime
            loggedIn= true

            /* val db = dbHelper.readableDatabase
         val cursor = db.rawQuery("SELECT * FROM users WHERE username = ? AND password = ?", arrayOf(username, password))
         val loggedIn = cursor.count > 0
         cursor.close()

         */
        }
        else{
            loggedIn = runBlocking { prodRepository.checkLogin(username, password) }
            viewModelScope.launch {
                 if (loggedIn)
                    prodRepository.refreshUsers(username, password)
            }

            //val usr = userInfo!!


              }

        return loggedIn
    }

    public fun logout() {
        //val userDao = database.userDao()
        userDao.deleteAll()

    }
        public fun checkIfLoggedIn(): Boolean {
        //val userDao = database.userDao()
        var userInfo:User? = userDao.getUser()

        if (userInfo != null) {


            modelUsername = userInfo.username!!
            modelRole = userInfo.role!!
            modellogtime= userInfo.logtime!!
            modelPreviousLog = userInfo.prevLoginTime!!
            loggedIn= true

            /* val db = dbHelper.readableDatabaseiouslogin
         val cursor = db.rawQuery("SELECT * FROM users WHERE username = ? AND password = ?", arrayOf(username, password))
         val loggedIn = cursor.count > 0
         cursor.close()

         */
        }
        else{
            loggedIn = false

            //val usr = userInfo!!


        }
        return loggedIn
    }

    fun handleMeasure(measureId:Int, hazardId:Int){
        viewModelScope.launch {
            hazardDao.handleMeasure(measureId, hazardId)
        }
    }

    fun clearMeasure(measureId:Int, hazardId:Int){
        viewModelScope.launch {
            hazardDao.clearMeasure(measureId, hazardId)
        }
    }

    fun checkIfStepHandled(id:Int, prodId: Int, version: Int,batch: Int,step: Int,
                 reading:Int, comments:String, checkby: String, risk: String, checkedRisk:String ):Boolean {
        //var prodInfo = productDao.getProduct(id).asLiveData()

                var hazList =stepHazardList(prodId, version, batch, step)
                //Toast.makeText(context, "Saving step information "+hazList.size.toString(), Toast.LENGTH_SHORT).show()
                var numMeasures =0
                var alertStr =""
                var currHaz =""
                var handled = true
                var numHazards = 0
                for (i in 0..hazList.size-1)
                {
                    numHazards=1

                    if (hazList[i].rank==0) {
                        if (currHaz != hazList[i].name) {
                            if ((numMeasures == 0) and (currHaz.length>0))
                                handled = false
                        }
                        numMeasures = 0
                        currHaz = hazList[i].name
                        //prodRepository.insertHazardFromStep( hazList[i].hazardid,prodId,version, batch, step,
                        //    hazList[i].name, hazList[i].type,hazList[i].forStep?:0)
                    }
                    else
                           numMeasures+=hazList[i].handled!!
                       // prodRepository.insertMeasureFromStep( prodId,version, batch, step,
                       //     hazList[i].measureName!!, hazList[i].hazardid,hazList[i].name)
                }
             if ((numMeasures == 0) and (numHazards>0))
                handled = false

             return handled
                //}

        //}
        // return prodInfo
    }

    fun saveStep(id:Int, prodId: Int, version: Int,batch: Int,step: Int,
                 reading:Int, comments:String, checkby: String, risk: String, checkedRisk:String ) {
        //var prodInfo = productDao.getProduct(id).asLiveData()
        viewModelScope.launch {
            //if (prodInfo?.value?.last_updated=="")
            //{f
            try {
                prodRepository.updateProdBatchStep(id, prodId,version, batch, step,
                    reading, comments, checkby, risk, checkedRisk)

                var hazList =stepHazardList(prodId, version, batch, step)
                //Toast.makeText(context, "Saving step information "+hazList.size.toString(), Toast.LENGTH_SHORT).show()

                for (i in 0..hazList.size-1)
                {
                    if (hazList[i].rank==0)
                        prodRepository.insertHazardFromStep( hazList[i].hazardid,prodId,version, batch, step,
                            hazList[i].name, hazList[i].type,hazList[i].forStep?:0)
                    else
                        prodRepository.insertMeasureFromStep( prodId,version, batch, step,
                            hazList[i].measureName!!, hazList[i].hazardid,hazList[i].name)
                }


                //}

                    //.refreshProdVersions(id)h
                _eventNetworkError.value = false
                _isNetworkErrorShown.value = false

            } catch (networkError: IOException) {
                if (prodlist.value.isNullOrEmpty())
                    _eventNetworkError.value = true
            }
        }
        //}
       // return prodInfo
    }

    private var _currentVersionBatch: MutableLiveData<ProdVersionBatch> =
        MutableLiveData<ProdVersionBatch>()
    val currentVersionBatch: LiveData<ProdVersionBatch>
        get() = _currentVersionBatch


    fun versionBatches(prodId: Int, version: Int): Flow<List<ProdVersionBatch>> =
        prodVersionBatchDao.getProdVersionBatches(prodId, version)

    /**
     * Retrieve an item from the repository.
     */
    fun retrieveBatch(prodId: Int, version: Int, batch: Int): LiveData<ProdVersionBatch> {
        var prodVersionBatchInfo =prodVersionBatchDao.getProdVersionBatch(prodId, version, batch).asLiveData()
        //if prodVersionBatchInfo.value.
        viewModelScope.launch {
            try {
                prodRepository.refreshProdBatchSteps(prodId, version, batch)
                _eventNetworkError.value = false
                _isNetworkErrorShown.value = false

            } catch (networkError: IOException) {
                if (prodlist.value.isNullOrEmpty())
                    _eventNetworkError.value = true
            }
        }

        return prodVersionBatchInfo
    }
   ////////////////////////////////////////////////////////////////////////////////////////

    suspend fun newStepHazard(
        prodId:Int,
        versionId:Int,
        batch:Int,
        step:Int,
        descript:String,
        forStep:Int,
        type:String,
        name:String,
        hazardName:String,
        measureId:Int

    ) {
        withContext(Dispatchers.IO){
            val updatedHazard = getNewHazard(prodId,versionId,batch,step, descript, forStep, type, hazardName, measureId)
            val updatedStepHazard = getNewStepHazard(updatedHazard.hazardid, updatedHazard.prodId!!,
                updatedHazard.version!!,  updatedHazard.batch!!, updatedHazard.stepId!!, updatedHazard.name!!, updatedHazard.measureId!!)


            insertStepHazard(updatedHazard, updatedStepHazard)

            //prodRepository.insertHazardFromStep( updatedHazard.hazardid,prodId,versionId, batch, step,  descript, "app",1)

            prodRepository.insertHazardFromStep( 0,prodId,versionId, batch, step,
                descript, "app",1)
        }

    }

    @Transaction
    private fun insertStepHazard(newhazard: Hazard, newStepHazard: StepHazard) {
        viewModelScope.launch {
            hazardDao.insert(newhazard)
            hazardDao.insertStep(newStepHazard)
        }
    }

    suspend fun getNewStepHazard(
        hazardId:Int,
        prodId:Int,
        version:Int,
        batch:Int,
        step:Int,
        hazardName:String,
        measureId:Int,


    ): StepHazard {

        return StepHazard(
            prodId =prodId,
            version=version,
            batchId = batch,
            stepId=step,
            hazId = hazardId,
            recordDate =Date().toString(),
             last_updated = Date().toString(),
            hazardName = hazardName,
            measureId = measureId
        )
    }




    suspend fun newHazard(
        prodId:Int,
        versionId:Int,
        batch:Int,
        step:Int,
        descript:String,
        forStep:Int,
        type:String,
        name:String,
        measureId:Int

    ) {
        withContext(Dispatchers.IO){
            val updatedHazard = getNewHazard(prodId,versionId,batch,step, descript,forStep, type, name, measureId)


            insertHazard(updatedHazard)

        }

    }

    @Transaction
    private fun insertHazard(newhazard: Hazard) {
        viewModelScope.launch {
            hazardDao.insert(newhazard)
        }
    }


    suspend fun getNewHazard(
        prodId:Int,
        versionId:Int,
        batch:Int,
        step:Int,
        descript:String,
        forStep:Int,
        type:String,
        name: String,
        measureId:Int
    ): Hazard {
        var hazrank=0
        var newHazardId = 0
         //var hazard:Hazard =retrieveHazard(localHazId).as
        //val hazard: Hazard = hazardDao.getHazard(localHazId)//...asLiveData()
        //hazard.observe(this, new Observer)



        return Hazard(
            hazardid=newHazardId,//hazard.hazardid,
            prodId = prodId,
            version = versionId,
            batch = batch,
            stepId = step,
            type = type,//hazard.type ,
            name = name,//,//hazard.name,
            description= descript,
            justification="", //"",hazard.justification,
            measureName="",
            rank = hazrank,
            cat_bio = 0,//hazard.cat_bio,
            cat_chem = 0,//hazard.cat_chem,
            cat_phys = 0,//hazard!!.cat_phys,
            last_updated = Date().toString(),
            forStep=forStep,
            measureId = measureId,
            handled =0

        )
    }



    suspend fun newMeasure(
        localHazId: Int,
        hazardId: Int,
        prodId:Int,
        versionId:Int,
        batch:Int,
        step:Int

    ) {
        withContext(Dispatchers.IO){
            val updatedMeasure = getNewMeasure(localHazId,hazardId,prodId,versionId,batch,step)


            insertMeasure(updatedMeasure)

        }
    }

    fun getNextRankForHazard(id: Int): Int {
        var hazardInfo =hazardDao.getNextRankForHazard(id)
        //if prodVersionBatchInfo.value.

        return hazardInfo
    }


    suspend fun getNewMeasure(
        localHazId: Int,
        hazardId:Int,
        prodId:Int,
        versionId:Int,
        batch:Int,
        step:Int
    ): Hazard {
        var hazrank=getNextRankForHazard(hazardId)
        var newRank = hazrank+1
        //var hazard:Hazard =retrieveHazard(localHazId).as
        val hazard: Hazard = hazardDao.getHazard(localHazId)//...asLiveData()
        //hazard.observe(this, new Observer)

        return Hazard(
            hazardid=hazard.hazardid,
            prodId = prodId,
            version = versionId,
            batch = batch,
            stepId = step,
            type = hazard.type ,
            name = hazard.name,
            description= hazard.description,
            justification= hazard.justification,
            measureName="",
            rank = newRank,
            cat_bio = hazard.cat_bio,
            cat_chem = hazard.cat_chem,
            cat_phys = 0,//hazard!!.cat_phys,

            last_updated = Date().toString(),
            forStep = hazard.forStep,
            measureId = hazard.measureId,
            handled = hazard.handled
        )
    }


    @Transaction
    private fun insertMeasure(newmeasure: Hazard) {
        viewModelScope.launch {
            hazardDao.insert(newmeasure)
            }
    }



    suspend fun setMeasure(
        localHazId: Int,
        measureName:String

    ) {
        withContext(Dispatchers.IO){
            val updatedMeasure = getUpdatedMeasure(localHazId, measureName)


             updateMeasure(updatedMeasure)

        }
    }



    suspend fun getUpdatedMeasure(
        localHazId: Int,
        updatedMeasureName: String
    ): Hazard {

        //var hazard:Hazard =retrieveHazard(localHazId).as
        val hazard: Hazard = hazardDao.getHazard(localHazId)//...asLiveData()
        //hazard.observe(this, new Observer)

        return Hazard(
            id = localHazId,
            hazardid=hazard.hazardid,
            prodId = hazard.prodId,
            version = hazard.version,
            batch = hazard.batch,
            stepId = hazard.stepId,
            type = hazard.type ,
            name = hazard.name,
            description= hazard.description,
            justification= hazard.justification,
            measureName=updatedMeasureName,
            rank = hazard.rank,
            cat_bio = hazard.cat_bio,
            cat_chem = hazard.cat_chem,
            cat_phys = 0,//hazard!!.cat_phys,

            last_updated = Date().toString(),
            forStep = hazard.forStep,
            measureId = hazard.measureId,
            handled = hazard.handled
        )
    }


    @Transaction
    private fun updateMeasure(updatedmeasure: Hazard) {
        viewModelScope.launch {
            hazardDao.update(updatedmeasure)
        }
    }





    suspend fun clearMeasure(
        localHazId: Int,
        measureName: String

    ) {
        withContext(Dispatchers.IO){
            val updatedMeasure = getUpdatedMeasure(localHazId, measureName)


            deleteMeasure(updatedMeasure)

        }
    }




    @Transaction
    private fun deleteMeasure(updatedmeasure: Hazard) {
        viewModelScope.launch {
            hazardDao.delete(updatedmeasure)
        }
    }



    /////////////////////////////////////////////////////////////////////////////

    suspend fun newBatch(
        prodId: Int,
        version: Int

    ) {
        withContext(Dispatchers.IO){
        val updatedBatch = getNewBatch(prodId, version)


        insertBatch(updatedBatch)

        }
    }
    @Transaction
    private fun insertBatch(newbatch: ProdVersionBatch) {
        viewModelScope.launch {
            prodVersionBatchDao.insert(newbatch)
            prodVersionBatchDao.insertNewBatchSteps(newbatch.prodId, newbatch.batch)
            prodRepository.insertProdBatch(newbatch.prodId, newbatch.version)
            //prodVersionBatchDao.insertNewBatchSteps(newbatch.prodId,newbatch.version, newbatch.batch)
        }
        }

    suspend fun getNewBatch(
        prodId: Int,
        version: Int

    ): ProdVersionBatch {
        //var lastbatch:Int? =0
        //lastbatch= getLastBatch(prodId,version)

        return ProdVersionBatch(
            prodId = prodId,
            version = version,
            batch = getLastBatch(prodId,version)+1,
            startDate = Date().toString(),
            finishDate = "",
            progress=0,
            status = "",
            last_updated=""//current time
        )
    }


    fun getLastBatch(prodId:Int,version: Int): Int {
        return prodVersionBatchDao.getLastBatch(prodId,version)
    }

    // Cache all items form the database using LiveData.
    val versionsOfProduct: LiveData<List<ProdVersion>> = prodVersionDao.getProdVersions(prodId).asLiveData()

    private var _currentVersion: MutableLiveData<ProdVersion> = MutableLiveData<ProdVersion>()
    val currentVersion: LiveData<ProdVersion>
        get() = _currentVersion


    fun productVersions(prodId: Int): Flow<List<ProdVersion>> = prodVersionDao.getProdVersions(prodId)

    /**
     * Retrieve an item from the repository.
     */
    fun retrieveVersion(prodId: Int, version:  Int): LiveData<ProdVersion> {
        viewModelScope.launch {
            try {
                prodRepository.refreshProdVersionBatches(prodId, version)
                _eventNetworkError.value = false
                _isNetworkErrorShown.value = false

            } catch (networkError: IOException) {
                if (prodlist.value.isNullOrEmpty())
                    _eventNetworkError.value = true
            }
        }

        return prodVersionDao.getProdVersion(prodId, version).asLiveData()
    }



    private var _currentStep: MutableLiveData<ProdBatchStep> = MutableLiveData<ProdBatchStep>()
    val currentStep: LiveData<ProdBatchStep>
        get() = _currentStep


    fun batchSteps(prodId:Int,batch:Int, version: Int): Flow<List<ProdBatchStep>> = prodBatchStepDao.getProdBatchSteps(prodId, batch, version)

    fun currentBatchStep(prodId:Int,batch:Int, version: Int, step:Int)= prodBatchStepDao.getCurrentBatchStep(prodId, batch, version, step)


    fun retrieveStep(prodId: Int, version: Int, batchId: Int, stepId: Int): LiveData<ProdBatchStep> {
       var stepdata = prodBatchStepDao.getProdBatchStep(stepId, prodId, batchId).asLiveData()

        viewModelScope.launch {
            try {
                if (stepLock.isNullOrEmpty())
                      prodRepository.refreshHazards(prodId, version, batchId, stepId)

                _eventNetworkError.value = false
                _isNetworkErrorShown.value = false

            } catch (networkError: IOException) {
                if (prodlist.value.isNullOrEmpty())
                    _eventNetworkError.value = true
            }
        }

        return stepdata


    }


    fun getStep(prodId: Int, version: Int, batchId: Int, stepId: Int): Flow<ProdBatchStep> {
        var stepdata = prodBatchStepDao.getProdBatchStep(stepId, prodId, batchId)


        return stepdata


    }



    fun isEntryValid(checkedBy: String): Boolean {
        if (checkedBy.isBlank() ) {
            return false
        }
        return true
    }

    private fun updateStep(step: ProdBatchStep) {
        viewModelScope.launch {
            prodBatchStepDao.update(step)
        }
    }


    fun updateStep(
        id: Int,
        prodId: Int,
        version:Int,
        batchId: Int,
        stepId: Int,
        stepName: String,
        datetime: String?,
        checkby: String?,
        reading: Int?,
        detail:String?,
        risk:String?,
        checkedRisk:String?

        ) {
        val updatedStep = getUpdatedStepEntry(  id, prodId,version, batchId,stepId, stepName,
            datetime, checkby,reading,detail, risk, checkedRisk )
        updateStep(updatedStep)

    }

    private fun getUpdatedStepEntry(
        id: Int,
        prodId: Int,
        version:Int,
        batchId: Int,
        stepId: Int,
        stepName: String,
        checkTime: String?,
        checkby: String?,
        reading: Int?,
         detail:String?,
        risk:String?,
        checkedRisk:String?
    ): ProdBatchStep {
        return ProdBatchStep(
            id = id,
            prodId = prodId,
            version=version,
            batchId = batchId,
            stepId = stepId,
            stepName = stepName,
            stepCode = "",
            checkTime = checkTime,
            checkby = checkby,
            reading = reading,
            detail=detail,
            risk = risk,
            checkedRisk=checkedRisk,
            last_updated=""//current time --controls to manage device time will eventually be done
                           //for now depending on server time for consistency

        )
    }

    fun stepHazards(prodId: Int, version:Int, batch:Int, step:Int): Flow<List<Hazard>> = hazardDao.getStepHazards(prodId , version , batch, step)

    fun stepHazardList(prodId: Int, version:Int, batch:Int, step:Int): List<Hazard> = hazardDao.getStepHazardList(prodId , version , batch, step)

//    fun stepHazards(step:Int): Flow<List<Hazard>> = hazardDao.getStepHazards(step)

    /**
     * Retrieve an item from the repository.Flow
     */
    fun retrieveHazard(id: Int):  Hazard{
        return hazardDao.getHazard(id)
    }




    fun allMeasures(): Flow<List<Measure>> {

        return measureDao.getAllMeasures()

    }

    fun hazardMeasures(hazid:Int): Flow<List<Measure>> = measureDao.getHazardMeasures(hazid)

    /**
     * Retrieve an item from the repository.
     */
    fun retrieveMeasure(id: Int): LiveData<Measure> {
        return measureDao.getMeasure(id).asLiveData()
    }
    private var _eventNetworkError = MutableLiveData<Boolean>(false)


    val eventNetworkError: LiveData<Boolean>
        get() = _eventNetworkError


    private var _isNetworkErrorShown = MutableLiveData<Boolean>(false)


    val isNetworkErrorShown: LiveData<Boolean>
        get() = _isNetworkErrorShown


    fun doesFileExist(context: Context, fileName: String): Boolean {
        //val file = File(context.getExternalFilesDir(null), fileName)
        val file = File(fileName)
        return file.exists()
    }


    private fun refreshDataFromRepository(context:Context) {


        val client = OkHttpClient.Builder()
            .connectionSpecs(
                Arrays.asList(
                ConnectionSpec.MODERN_TLS,
                ConnectionSpec.COMPATIBLE_TLS,
                ConnectionSpec.CLEARTEXT
            ))
            .build()
        viewModelScope.launch {
            try {
                // makeCall()
                prodRepository.refreshProducts()
                var prodData =productDao.getProductList()
                var url:String

                for (i in 0..prodData.size-1)
                {
                    if (prodData[i].picture?.length!!>0) {
                          // var filePath = "/storage/emulated/0/Download/prodMan/"+prodData[i].picture?:"" // Replace with the actual file path

                        //showToasfileExistst(context, filePath)

                         //val fileExists = doesFileExist(context, "/storage/emulated/0/Download/prodMan/"+prodData[i].picture?:"")
                        //  if (!(doesFileExist(context, "/storage/emulated/0/Download/prodMan/"+prodData[i].picture?:""))) {
                            if (!(doesFileExist(context, downloadPath+"/prodMan/"+prodData[i].picture?:""))) {
                                try {

                                    val imgUrl =
                                    "https://stackseven.lat/static/uploads/" + prodData[i].picture
                                        ?: ""
                                downloadAndSaveImage(context, imgUrl, prodData[i].picture ?: "")
                                } catch (e: Exception) {
                                    Log.e("ImageDownloader", "Error downloading image: ${e.message}")
                                }


                            }


                    }
                }
                _eventNetworkError.value = false
                _isNetworkErrorShown.value = false

            } catch (networkError: IOException) {
                // Show a Toast error message and hide the progress bar.
                Log.d(
                    "restlog",
                    networkError.localizedMessage + "|" + networkError.stackTraceToString()
                )
                if (prodlist.value.isNullOrEmpty())
                    _eventNetworkError.value = true
            }


        }



    }

/*
    fun downloadImage(context: Context, imageUrl: String, fileName:String) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val url = URL(imageUrl)
                 //val connection: HttpsURLConnection = setTLSVersion(imageUrl)!!

                //val url = URL("https://stackseven.lat:your_port/path") // Replace your_port and path
                val connection = url.openConnection() as HttpURLConnection
                //connection.connect()

               // val connection: HttpsURLConnection = url.openConnection() as HttpsURLConnection
                connection.doInput = true
                connection.connect()
                val imagePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString()+"/s7ProdTrac"
                val inputStream: InputStream = connection.inputStream
                val directory = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "s7ProdTrac")
                if (!directory.exists()) {
                    //directory.mkdirs()
                    createFolder(context, imagePath)
                }
                val file = File(directory, fileName)

                val outputStream = FileOutputStream(file)
                val buffer = ByteArray(1024)
                var bytesRead: Int

                while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                    outputStream.write(buffer, 0, bytesRead)
                }

                outputStream.close()
                inputStream.close()

                showToast(context, "Image downloaded and saved to: ${file.absolutePath}")
            } catch (e: Exception) {
                e.printStackTrace()
                showToast(context, "Failed to download image: ${e.message}")
            }
        }
    }
  */
    fun downloadAndSaveImage(context: Context, imageUrl: String, fileName:String) {
    GlobalScope.launch(Dispatchers.IO) {
        var urlConnection: HttpURLConnection? = null
        var inputStream: BufferedInputStream? = null
        var outputStream: FileOutputStream? = null

        try {
            // Create a URL object from the image URL
            val url = URL(imageUrl)

            // Open a connection to the URL
            urlConnection = url.openConnection() as HttpURLConnection
            urlConnection.connect()

            // Get the input stream
            inputStream = BufferedInputStream(urlConnection!!.inputStream)

            // Decode the input stream into a Bitmap
            val bitmap = BitmapFactory.decodeStream(inputStream)
            val imagePath =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                    .toString()

            // Get the file directory
            val directory =
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            if (!directory.exists()) {
                //directory.mkdirs()
                createFolder(context, imagePath)
            }
            // Determine the file extension based on the URL
            val fileExtension = getFileExtension(imageUrl)

            // Create a new file for the image
         val filePart = fileName.split(".")[0]
         // val imageFile = File(directory, "$filePart.$fileExtension")
         val imageFile = File(directory, "$fileName")

        // Create a FileOutputStream to write the bitmap to the file
        //val file = File(imageFile)

            outputStream = FileOutputStream(imageFile)

            // Compress and write the bitmap to the output stream
            if (fileExtension.equals("jpg", ignoreCase = true) || fileExtension.equals(
                    "jpeg",
                    ignoreCase = true
                )
            ) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            } else if (fileExtension.equals("png", ignoreCase = true)) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            } else if (fileExtension.equals("gif", ignoreCase = true)) {
                // Handle GIF images differently (not supported by BitmapFactory)
                // You may need to use specialized libraries for handling GIF images
            }

            // Flush the output stream
            outputStream.flush()

            // Close the output stream
            outputStream.close()

            // Insert the image file into the MediaStore
            insertImageIntoMediaStore(
                context.contentResolver,
                imageFile.absolutePath,
                filePart,
                filePart,
                fileExtension
            )
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            // Close the input stream and disconnect the connection
            urlConnection?.disconnect()
            try {
                inputStream?.close()
                outputStream?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

      }
    }

    private fun insertImageIntoMediaStore(
        contentResolver: ContentResolver,
        imagePath: String,
        title: String,
        description: String,
        fileExtension:String
    ) {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, title)
        values.put(MediaStore.Images.Media.DISPLAY_NAME, title)
        values.put(MediaStore.Images.Media.DESCRIPTION, description)
        //values.put(MediaStore.Images.Media.MIME_TYPE, "image/*")


        if (fileExtension.equals("jpg", ignoreCase = true) || fileExtension.equals(
                "jpeg",
                ignoreCase = true
            )
        ) {
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        } else if (fileExtension.equals("png", ignoreCase = true)) {
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/png")
        } else if (fileExtension.equals("gif", ignoreCase = true)) {
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/gif")
            // Handle GIF images differently (not supported by BitmapFactory)
            // You may need to use specialized libraries for handling GIF images
        }


        values.put(MediaStore.Images.Media.DATA, imagePath)
        contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
    }

    private fun getFileExtension(url: String): String {
        var extension = ""
        val lastDotIndex = url.lastIndexOf('.')
        if (lastDotIndex > 0) {
            extension = url.substring(lastDotIndex + 1)
        }
        return extension.lowercase(Locale.getDefault())
    }

    fun createFolder(
        context: Context,
        folderName: String
    ):Int//: Uri?
    {
        // Create a new directory in the external storage
        val newFolder =
            File(Environment.getExternalStorageDirectory(), folderName)
        if (!newFolder.exists()) {
            if (!newFolder.mkdirs()) {
                // Folder creation failed
                return -1
            }
        }

        // Insert folder into MediaStore
        val contentValues = ContentValues()
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, folderName)
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "vnd.android.cursor.dir/$folderName")
        val contentResolver = context.contentResolver
        /*return contentResolver.insert(
            MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY),
            contentValues
        ) */
        return 1
    }
    /*
    fun setTLSVersion(url: String): HttpsURLConnection? {
        try {
            val trustAllCerts: Array<TrustManager> = arrayOf(object : X509TrustManager {
                override fun checkClientTrusted(chain: Array<java.security.cert.X509Certificate>, authType: String) {}

                override fun checkServerTrusted(chain: Array<java.security.cert.X509Certificate>, authType: String) {}

                override fun getAcceptedIssuers(): Array<java.security.cert.X509Certificate> {
                    return arrayOf()
                }
            })

            val trustManager = trustAllCerts[0] as X509TrustManager
            val sslContext: SSLContext = SSLContext.getInstance("TLS")
            sslContext.init(null, trustAllCerts, java.security.SecureRandom())
            val sslSocketFactory: SSLSocketFactory = sslContext.socketFactory

            val connection = URL(url).openConnection() as HttpsURLConnection
            connection.sslSocketFactory = sslSocketFactory
            connection.connectTimeout = 10000
            connection.readTimeout = 15000

            return connection
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

     */
    fun showToast(context: Context, message: String) {
        GlobalScope.launch(Dispatchers.Main) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }


    fun makeRequest(url: String) {
        val httpClient = OkHttpClient()
        val request = okhttp3.Request.Builder().url(url).build()
        val response = httpClient.newCall(request).execute()
        val responseBody = response.body
        if (responseBody != null) {
            val responseString = responseBody.string()
            Log.d("Retrofit", responseString)
        }

    }



    fun readStatusMessage(response: retrofit2.Response<*>): String {
        return response.raw().message
    }


    /**
     * Resets the network error flag.
     */
    fun onNetworkErrorShown() {
        _isNetworkErrorShown.value = true
    }


}
class ProdViewModelFactory(private val productDao: ProductDao,
                           private val prodVersionDao: ProdVersionDao,
                           private val prodVersionBatchDao: ProdVersionBatchDao,
                           private val prodBatchStepDao: ProdBatchStepDao,
                           private val hazardDao: HazardDao,
                           private val measureDao: MeasureDao,
                           private val userDao: UserDao,
                           private val productApplication: ProductApplication) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProdViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProdViewModel(productDao,prodVersionDao,prodVersionBatchDao,prodBatchStepDao,
                                  hazardDao, measureDao,userDao, productApplication) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")

    }
}


