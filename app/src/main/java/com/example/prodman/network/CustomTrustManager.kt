import java.security.cert.X509Certificate


import javax.net.ssl.X509TrustManager
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManager

class CustomTrustManager: X509TrustManager  {
    fun getTrustAllSSLSocketFactory(): SSLSocketFactory? {
        return try {
            // Create a TrustManager that trusts all certificates
            val trustAllCertificates = arrayOf<TrustManager>(
                object : X509TrustManager {
                    override fun getAcceptedIssuers(): Array<X509Certificate> {
                        return arrayOf()
                    }

                    override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {
                    }

                    override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {
                    }
                }
            )

            // Initialize an SSL context with the custom TrustManager
            val sslContext = SSLContext.getInstance("TLS")
            sslContext.init(null, trustAllCertificates, java.security.SecureRandom())

            // Create an SSLSocketFactory that uses the custom TrustManager
            sslContext.socketFactory
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override fun checkClientTrusted(p0: Array<out X509Certificate>?, p1: String?) {
        TODO("Not yet implemented")
    }

    override fun checkServerTrusted(p0: Array<out X509Certificate>?, p1: String?) {
        TODO("Not yet implemented")
    }

    override fun getAcceptedIssuers(): Array<X509Certificate> {
        return arrayOf()
    }
}