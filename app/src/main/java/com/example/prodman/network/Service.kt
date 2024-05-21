package com.example.prodman.network

import CustomTrustManager
import android.net.Uri.parse
import android.util.Log
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Url
import java.lang.reflect.Type
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager


interface ImageDownloadService {
    //@POST("product/pic/")
    //suspend fun getPicture(@Body body: Map<String, String>): Call<ResponseBody>
    @GET
    suspend fun getPicture(@Url imageUrl: String): Call<ResponseBody>


}

interface ProdService {

    // @GET("product/getread/")
    // suspend fun getProdlist(): NetworkProductContainer

    @POST("product/pic/")
    suspend fun getPicture(@Body body: Map<String, String>): Call<ResponseBody>



    @POST("product/read/") // Replace with your endpoint URLBitmap//
    suspend fun getProdlist(@Body body: Map<String, String>):NetworkProductContainer

    @POST("product/batchstepmeasures/") // Replace with your endpoint URL
    suspend fun getHazList(@Body body: Map<String, String>):NetworkHazardContainer


    @POST("product/updateprodbatchstep/") // Replace with your endpoint URL
    suspend fun updateProdBatchStep(@Body body: Map<String, String>)


    @POST("product/insertbatch/") // Replace with your endpoint URL
    suspend fun insertProdBatch(@Body body: Map<String, String>)


    @POST("hazard/inserthazardfromstep/") // Replace with your endpoint URL
    suspend fun insertHazardFromStep(@Body body: Map<String, String>)


    @POST("hazard/insertmeasurefromstep/") // Replace with your endpoint URL
    suspend fun insertMeasureFromStep(@Body body: Map<String, String>)


    @POST("measure/read/") // Replace with your endpoint URL
    suspend fun getMeasList(@Body body: Map<String, String>):NetworkMeasureContainer

    @POST("user/login/") // Replace with your endpoint URL
    suspend fun getUserData(@Body body: Map<String, String>):NetworkUserContainer

    @POST("hazardmeasure/read/") // Replace with your endpoint URL
    suspend fun getHazardMeasureList(@Body body: Map<String, String>):NetworkHazardMeasureContainer



    @POST("product/batchsteps/")
    suspend fun getProdBatchStepList(@Body body: Map<String, String>):NetworkProdBatchStepContainer

    @POST("product/prodstep/")
    suspend fun getProdStepList(@Body body: Map<String, String>):NetworkProdStepContainer


    @POST("product/getversions/")
    suspend fun getProdVersionList(@Body body: Map<String, String>):NetworkProdVersionContainer

    @POST("product/versionbatches/")
    suspend fun getProdVersionBatchList(@Body body: Map<String, String>):NetworkProdVersionBatchContainer


    @POST("product/prodstep/")
    suspend fun getProdVersionStepList(@Body body: Map<String, String>):NetworkProdVersionStepContainer


    @POST("step/read/")
    suspend fun getStepList(@Body body: Map<String, String>):NetworkStepContainer

    @POST("stephazard/read/")
    suspend fun getStepHazardList(@Body body: Map<String, String>):NetworkStepHazardContainer




}


object ProdNetwork {

    var  sslContext = SSLContext.getInstance("TLS")


    // Create a custom TrustManager
    private var _tm1: CustomTrustManager?=null
    var tm1 : CustomTrustManager

    get() = _tm1!!
    set(value) {
        _tm1 = value
    }


    val trustManager = object : X509TrustManager {
        override fun getAcceptedIssuers(): Array<X509Certificate?>? {
            return arrayOfNulls(0)
           // return null
        }
        override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {
            // Accept all certificates

        }

        override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {
            // Accept all certificates
        }

        /*
        fun SSLContext.X509TrustManager(): X509ExtendedTrustManager {
            return socketFactory as X509ExtendedTrustManager
        }

         */
        init {
             sslContext.init(null, arrayOf(this as X509TrustManager), null)




        }


    }






   /*
    fun SSLContext.getSocketFactory(): X509ExtendedTrustManager {
        return socketFactory as X509ExtendedTrustManager
    }

    */
    // Get the socket factory

    // Create a new OkHttpClient

    /*
    // Create a new SSLContext
    val sslContext= SSLContext.getInstance("TLS").init(null, arrayOf(trustManager), null)


    // Create a new OkHttpClient
    val client = OkHttpClient.Builder()
        .sslSocketFactory(sslContext.getSocketFactory(), trustManager)
        .build()
*/



    val logging = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)

    val httpClient = OkHttpClient.Builder().addInterceptor(logging)
    val   socketFactory = sslContext.getSocketFactory()
    val sockClient = OkHttpClient.Builder()
                .sslSocketFactory( socketFactory, trustManager  )
                .build()
    val retrofit = Retrofit.Builder()
        //.baseUrl("https://paulgaynor.com/restprod/api/")
        .baseUrl("https://stackseven.lat/restprod/api/")
        // .baseUrl("https://34.72.115.200/restprod/api/")
       // .baseUrl("https://android-kotlin-fun-mars-server.appspot.com/")
        .addConverterFactory(MoshiConverterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())

        //.addConverterFactory(ScalarsConverterFactory.create())

        //.client(sockClient)
        .client(getUnsafeOkHttpClient(httpClient)!!.build())//TEST ONLY!!!!!
        .build()

    val picRetrofit = Retrofit.Builder()
        .baseUrl("https://stackseven.lat/restprod/api/")
        .addConverterFactory(ScalarsConverterFactory.create())
        .client(getUnsafeOkHttpClient(httpClient)!!.build())//TEST ONLY!!!!!
        .build()

    val products = retrofit.create(ProdService::class.java)
    val hazards = retrofit.create(ProdService::class.java)
    val measures = retrofit.create(ProdService::class.java)
    val hazardmeasures = retrofit.create(ProdService::class.java)
    val prodbatchsteps = retrofit.create(ProdService::class.java)
    val prodsteps = retrofit.create(ProdService::class.java)
    val prodversions = retrofit.create(ProdService::class.java)
    val prodversionbatches = retrofit.create(ProdService::class.java)
    val prodversionsteps = retrofit.create(ProdService::class.java)
    val steps = retrofit.create(ProdService::class.java)
    val stephazards = retrofit.create(ProdService::class.java)
    val imageDownloadService = picRetrofit.create(ImageDownloadService::class.java)



    fun showStatus(msg:String){

      Log.d("restlog", "response status:" + msg+";"+ httpClient.toString())

    }

    private fun getUnsafeOkHttpClient(builder: OkHttpClient.Builder): OkHttpClient.Builder? {
        return try {
            val trustAllCerts = arrayOf<TrustManager>(
                object : X509TrustManager {
                    @Throws(CertificateException::class)
                    override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {}
                    @Throws(CertificateException::class)
                    override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {}
                    override fun getAcceptedIssuers(): Array<X509Certificate> { return arrayOf() }
                }
            )

            val sslContext = SSLContext.getInstance("SSL")
            sslContext.init(null, trustAllCerts, SecureRandom())

            // Create an ssl socket factory with our all-trusting manager
            val sslSocketFactory = sslContext.socketFactory
            builder.sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
            builder.hostnameVerifier { _, _ -> true }
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }




}

/*
class ToStringConverterFactory : Converter.Factory() {
    override fun responseBodyConverter(
        type: Type,
        annotations: Array<Annotation?>?,
        retrofit: Retrofit?
    ): Converter<ResponseBody, *>? {
        return if (String::class.java == type) {
            Converter { value -> value.string() }
        } else null
    }

    fun requestBodyConverter(
        type: Type,
        annotations: Array<Annotation?>?,
        retrofit: Retrofit?
    ): Converter<*, RequestBody>? {
        return if (String::class.java == type) {
            Converter<String?, RequestBody> { value ->
                RequestBody.create(
                    MEDIA_TYPE,
                    value
                )
            }
        } else null
    }

    companion object {
        private val MEDIA_TYPE: MediaType = "text/plain" as MediaType
    }
}

*/



