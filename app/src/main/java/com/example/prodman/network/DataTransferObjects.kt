package com.example.prodman.network

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey
import com.example.prodman.data.Hazard
import com.example.prodman.data.HazardMeasure
import com.example.prodman.data.Measure
import com.example.prodman.data.ProdBatchStep
import com.example.prodman.data.ProdStep
import com.example.prodman.data.ProdVersion
import com.example.prodman.data.ProdVersionBatch
import com.example.prodman.data.ProdVersionStep
import com.example.prodman.data.Product
import com.example.prodman.data.Step
import com.example.prodman.data.StepHazard
import com.example.prodman.data.User
import com.example.prodman.domain.DProduct
import com.squareup.moshi.JsonClass

/**
 * DataTransferObjects go in this file. These are responsible for parsing responses from the server
 * or formatting objects to send to the server. You should convert these to domain objects before
 * using them.
 *
 * @see domain package for
 */

/**
 * VideoHolder holds a list of Videos.
 *
 * This is to parse first level of our network result which looks like
 *
 * {
 *   "videos": []
 * }
 */
@JsonClass(generateAdapter = true)
data class NetworkProductContainer(val products: List<NetworkProduct>)


@JsonClass(generateAdapter = true)
data class NetworkHazardContainer(val hazards: List<NetworkHazard>)

@JsonClass(generateAdapter = true)
data class NetworkMeasureContainer(val measures: List<NetworkMeasure>)
@JsonClass(generateAdapter = true)
data class NetworkHazardMeasureContainer(val hazardmeasures: List<NetworkHazardMeasure>)
@JsonClass(generateAdapter = true)
data class NetworkProdBatchStepContainer(val prodbatchsteps: List<NetworkProdBatchStep>)
@JsonClass(generateAdapter = true)
data class NetworkProdStepContainer(val prodsteps: List<NetworkProdStep>)
@JsonClass(generateAdapter = true)
data class NetworkProdVersionContainer(val prodversions: List<NetworkProdVersion>)
@JsonClass(generateAdapter = true)
data class NetworkProdVersionBatchContainer(val prodversionbatches: List<NetworkProdVersionBatch>)
@JsonClass(generateAdapter = true)
data class NetworkProdVersionStepContainer(val prodversionsteps: List<NetworkProdVersionStep>)
@JsonClass(generateAdapter = true)
data class NetworkStepContainer(val steps: List<NetworkStep>)
@JsonClass(generateAdapter = true)
data class NetworkStepHazardContainer(val stephazards: List<NetworkStepHazard>)
@JsonClass(generateAdapter = true)
data class NetworkUserContainer(val users: List<NetworkUser>)


@JsonClass(generateAdapter = true)
data class NetworkUser constructor(
    val id: Int = 0,
    val result: String,
    val username: String,
    val role: String,
    val logtime: String,
    val prevLoginTime: String
)
@JsonClass(generateAdapter = true)
data class NetworkProduct constructor(
    val id: Int = 0,
    val name: String,
    val packaging: String,
    val purpose: String,
    val shelfLife: String,
    val netWeight: String,
    val labeling: String,
    val description: String,
    val updated: Int,
    val picture:String?,
    val created:String?,
    val last_updated:String?
)

@JsonClass(generateAdapter = true)
data class NetworkProdVersion(
    val id: Int = 0,
    val prodId: Int,
    val version: Int,
    val saveDate: String,
    val comments: String,
    val last_updated:String?

)

@JsonClass(generateAdapter = true)
data class NetworkProdVersionBatch(
    val id: Int = 0,
    val prodId: Int,
    val version: Int,
    val batch: Int,
    val startDate: String,
    val finishDate: String?,
    val progress: Int?,
    val status: String?,
    val last_updated:String?
)

@JsonClass(generateAdapter = true)
data class NetworkProdBatchStep(
    val id: Int = 0,
    val prodId: Int,
    val version:Int,
    val batchId: Int,
    val stepId: Int,
    val stepCode: String?,
    val stepName: String,
    val checkTime: String?,
    val checkby: String?,
    val reading: Int?,
    val detail: String?,
    val result: String?,
    val risk: String?,
    val checkedRisk:String?,
    val last_updated:String?
)

@JsonClass(generateAdapter = true)
data class NetworkProdStep(
    val id: Int = 0,
    val prodId: Int,
    val stepId: Int,
    val stepCode: String,
    val sequence: Int,
    val pred: Int,
    val risk: String?,
    val last_updated:String?
)


@JsonClass(generateAdapter = true)
data class NetworkProdVersionStep(
    val id: Int = 0,
    val version: Int,
    val prodId: Int,
    val stepId: Int,
    val sequence: Int,
    val pred: Int,
    val last_updated:String?
)


@JsonClass(generateAdapter = true)
data class NetworkStep(
    val id: Int = 0,
    val name: String,
    val part_of: Int?
)


@JsonClass(generateAdapter = true)
data class NetworkStepHazard(
    val id: Int = 0,
    val prodId:Int,
    val version:Int,
    val batch:Int,
    val stepId: Int,
    val hazId: Int,
    val recordDate: String?,
    val last_updated:String?,
    val hazardName:String?,
    val measureId:Int?
)


@JsonClass(generateAdapter = true)
data class NetworkMeasure(
    val id: Int = 0,
    val name: String,
    val description: String,
    val last_updated:String?
)

@JsonClass(generateAdapter = true)
data class NetworkHazardMeasure(
    val id: Int = 0,
    val hazId: String,
    val measureId: String,
    val effectDate: String,
    val last_updated:String?
)


@JsonClass(generateAdapter = true)
data class NetworkHazard(
    val id: Int = 0,
    val prodId:Int,
    val version:Int,
    val batch:Int,
    val step:Int,
    val type: String,
    val name: String,
    val description: String,
    val justification: String?,
    val measureName: String?,
    val rank: Int?,
    val cat_bio: Int?,
    val cat_chem: Int?,
    val cat_phys: Int?,
    val last_updated:String?,
    val measureId:Int?,
    val handled:Int?
)


    /**
 * Convert Network results to database objects
 */


/**
 * Convert Network results to database objects
 */
fun NetworkProductContainer.asDatabaseModel(): List<Product> {
    return products.map {
        Product(
            id=it.id,
            prodName=it.name,
            packaging=it.packaging,
            purpose=it.purpose,
            shelfLife=it.shelfLife,
            netWeight=it.netWeight,
            labeling=it.labeling,
            description=it.description,
            updated=it.updated,
            picture = it.picture,
            created = it.created,
            last_updated=it.last_updated
        )    }
}


fun NetworkProdVersionContainer.asDomainModel(): List<ProdVersion> {
    return prodversions.map {
        ProdVersion(
            id = it.id,
            prodId = it.prodId,
            version = it.version,
            saveDate = it.saveDate,
            comments = it.comments,
            last_updated=it.last_updated

        )
    }
 }

fun NetworkProdVersionContainer.asDatabaseModel(): List<ProdVersion> {
    return prodversions.map {
        ProdVersion(
            id = it.id,
            prodId = it.prodId,
            version = it.version,
            saveDate = it.saveDate,
            comments = it.comments,
            last_updated=it.last_updated

        )   }
}



fun NetworkProdVersionBatchContainer.asDomainModel(): List<ProdVersionBatch> {
    return prodversionbatches.map {
        ProdVersionBatch(
            id = it.id,
            prodId = it.prodId,
            version = it.version,
            batch = it.batch,
            startDate = it.startDate,
            finishDate = it.finishDate,
            progress = it.progress,
            status = it.status,
            last_updated=it.last_updated

        )
    }
}



fun NetworkProdVersionBatchContainer.asDatabaseModel(): List<ProdVersionBatch> {
    return prodversionbatches.map {
        ProdVersionBatch(
            id = it.id,
            prodId = it.prodId,
            version = it.version,
            batch = it.batch,
            startDate = it.startDate,
            finishDate = it.finishDate,
            progress = it.progress,
            status = it.status,
            last_updated=it.last_updated

        )
    }
}


fun NetworkProdBatchStepContainer.asDomainModel(): List<ProdBatchStep> {
    return prodbatchsteps.map {
        ProdBatchStep(
            id = it.id,
            prodId = it.prodId,
            version=it.version,
            batchId = it.batchId,
            stepId = it.stepId,
            stepName = it.stepName,
            stepCode= it.stepCode,
            checkTime = it.checkTime,
            checkby = it.checkby,
            reading = it.reading,
            detail = it.detail,
            risk = it.risk,
            checkedRisk = it.checkedRisk,
            last_updated=it.last_updated

        )
    }
}



fun NetworkProdBatchStepContainer.asDatabaseModel(): List<ProdBatchStep> {
    return prodbatchsteps.map {
        ProdBatchStep(
            id = it.id,
            prodId = it.prodId,
            version = it.version,
            batchId = it.batchId,
            stepId = it.stepId,
            stepCode = it.stepCode,
            stepName = it.stepName,
            checkTime = it.checkTime,
            checkby = it.checkby,
            reading = it.reading,
            detail = it.detail,
            risk = it.risk,
            checkedRisk = it.checkedRisk,
            last_updated=it.last_updated

        )
    }
}


fun NetworkProdStepContainer.asDomainModel(): List<ProdStep> {
    return prodsteps.map {
        ProdStep(
            id = it.id,
            prodId = it.prodId,
            stepId = it.stepId,
            stepCode= it.stepCode,
            sequence = it.sequence,
            pred = it.pred,
            risk = it.risk,
            last_updated=it.last_updated

        )
    }
}


fun NetworkProdStepContainer.asDatabaseModel(): List<ProdStep> {
    return prodsteps.map {
        ProdStep(
            id = it.id,
            prodId = it.prodId,
            stepId = it.stepId,
            stepCode = it.stepCode,
            sequence = it.sequence,
            pred = it.pred,
            risk = it.risk,
            last_updated=it.last_updated

        )
    }
}





fun NetworkProdVersionStepContainer.asDomainModel(): List<ProdVersionStep> {
    return prodversionsteps.map {
        ProdVersionStep(
            id = it.id,
            prodId = it.prodId,
            version = it.version,
            stepId = it.stepId,
            sequence = it.sequence,
            pred = it.pred,
            last_updated=it.last_updated

        )
    }
}


fun NetworkProdVersionStepContainer.asDatabaseModel(): List<ProdVersionStep> {
    return prodversionsteps.map {
        ProdVersionStep(
            id = it.id,
            prodId = it.prodId,
            stepId = it.stepId,
            sequence = it.sequence,
            version = it.version,
            pred = it.pred,
            last_updated=it.last_updated

        )
    }
}




fun NetworkStepContainer.asDomainModel(): List<Step> {
    return steps.map {
        Step(
            id = it.id,
            name = it.name,
            part_of = it.part_of

        )
    }
}

fun NetworkStepContainer.asDatabaseModel(): List<Step> {
    return steps.map {
        Step(
            id = it.id,
            name = it.name,
            part_of = it.part_of

        )
    }
}




fun NetworkStepHazardContainer.asDomainModel(): List<StepHazard> {
    return stephazards.map {
        StepHazard(
            id = it.id,

            prodId = it.prodId,
            version = it.version,
            batchId = it.batch,
            stepId = it.stepId,
            hazId = it.hazId,
            recordDate = it.recordDate,
            last_updated=it.last_updated,
            hazardName =  it.hazardName,
            measureId = it.measureId

        )
    }
}

fun NetworkStepHazardContainer.asDatabaseModel(): List<StepHazard> {
    return stephazards.map {
        StepHazard(
            id = it.id,
            prodId = it.prodId,
            version = it.version,
            batchId = it.batch,
            stepId = it.stepId,
            hazId = it.hazId,
            recordDate = it.recordDate,
            last_updated=it.last_updated,
            hazardName =  it.hazardName,
            measureId = it.measureId

        )
    }
}





fun NetworkMeasureContainer.asDomainModel(): List<Measure> {
    return measures.map {
        Measure(
            id = it.id,
            name = it.name,
            description = it.description,
            last_updated=it.last_updated

        )
    }
}



fun NetworkMeasureContainer.asDatabaseModel(): List<Measure> {
    return measures.map {
        Measure(
            id = it.id,
            name = it.name,
            description = it.description,
            last_updated=it.last_updated

        )
    }
}


fun NetworkHazardMeasureContainer.asDomainModel(): List<HazardMeasure> {
    return hazardmeasures.map {
        HazardMeasure(
            id = it.id,
            hazId = it.hazId,
            measureId = it.measureId,
            effectDate= it.effectDate,
            last_updated=it.last_updated

        )
    }
}

fun NetworkHazardMeasureContainer.asDatabaseModel(): List<HazardMeasure> {
    return hazardmeasures.map {
        HazardMeasure(
            id = it.id,
            hazId = it.hazId,
            measureId = it.measureId,
            effectDate= it.effectDate,
            last_updated=it.last_updated

        )
    }
}




fun NetworkHazardContainer.asDomainModel(): List<Hazard> {
    return hazards.map {
        Hazard(
            hazardid = it.id,
            prodId = it.prodId,
            version = it.version,
            batch = it.batch,
            stepId = it.step,
            type = it.type,
            name = it.name,
            description= it.description,
            justification = it.justification,
            measureName = it.measureName,
            rank = it.rank,
            cat_bio= it.cat_bio,
            cat_chem = it.cat_chem,
            cat_phys= it.cat_phys,
            last_updated=it.last_updated,
            forStep = 0,
            measureId = it.measureId,
            handled = it.handled!!

        )
    }
}

fun NetworkUserContainer.asDatabaseModel(): List<User> {
    return users.map {
        User(
            result = it.result,
            username = it.username,
            role = it.role,
            logtime = it.logtime,
            prevLoginTime = it.prevLoginTime

        )
    }
}



fun NetworkHazardContainer.asDatabaseModel(): List<Hazard> {
    return hazards.map {
        Hazard(
            hazardid = it.id,
            prodId = it.prodId,
            version = it.version,
            batch = it.batch,
            stepId = it.step,
            type = it.type,
            name = it.name,
            description= it.description,
            justification = it.justification,
            measureName = it.measureName,
            rank = it.rank,
            cat_bio= it.cat_bio,
            cat_chem = it.cat_chem,
            cat_phys= it.cat_phys,
            last_updated=it.last_updated,
            forStep = 0,
            measureId = it.measureId,
            handled = it.handled

        )
    }
}

