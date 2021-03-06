package com.seanjstory.workplace.search.client

import com.seanjstory.workplace.search.client.ssl.NullHostnameVerifier
import com.seanjstory.workplace.search.client.ssl.NullX509TrustManager
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.lang.IllegalArgumentException
import java.net.URLEncoder
import javax.net.ssl.SSLContext
import javax.ws.rs.client.Client
import javax.ws.rs.client.ClientBuilder
import javax.ws.rs.client.Entity
import javax.ws.rs.client.WebTarget
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

const val CLIENT_VERSION = "0.0.1"
const val CLIENT_NAME = "elastic-workplace-search-java"
const val DEFAULT_ENDPOINT = "http://localhost:3002/api/ws/v1/"
const val SECURE = "secure"
const val INSECURE = "insecure"
const val DEFAULT_SECURITY = SECURE
val SECURITY_OPTIONS = listOf(DEFAULT_SECURITY, INSECURE)
const val DEFAULT_TIMEOUT = 15
data class Client @JvmOverloads constructor(var accessToken: String,
                                            var endpoint: String = DEFAULT_ENDPOINT,
                                            var security: String = DEFAULT_SECURITY,
                                            var userAgent: String? = null)
    : BaseClient, Permissions, ContentSourceDocuments {
    
    val restClient : Client = buildClient()
    val webTarget: WebTarget = restClient.target(endpoint)
    val log: Logger = LoggerFactory.getLogger(this.javaClass)
    

    override fun <T> get(path: String, params: Map<String, Any>, outputClass: Class<T>) = request("GET", path, params, outputClass)
    override fun <T> post(path: String, params: Map<String, Any>, outputClass: Class<T>) = request("POST", path, params, outputClass)
    override fun <T> post(path: String, params: List<Any>, outputClass: Class<T>) = request("POST", path, params, outputClass)
    override fun <T> put(path: String, params: Map<String, Any>, outputClass: Class<T>) = request("PUT", path, params, outputClass)
    override fun <T> put(path: String, params: List<Any>, outputClass: Class<T>) = request("PUT", path, params, outputClass)
    override fun <T> delete(path: String, params: Map<String, Any>, outputClass: Class<T>) = request("DELETE", path, params, outputClass)

    private fun buildClient() : Client {
        return if (this.security == SECURE) {
            buildSecureClient()
        } else if (this.security == INSECURE) {
            buildInsecureClient()
        } else {
            throw IllegalArgumentException("Security options are: $SECURITY_OPTIONS")
        }
    }

    private fun buildSecureClient() : Client {
        return ClientBuilder.newBuilder().sslContext(SSLContext.getDefault()).build() // TODO, make this capable of taking a custom truststore
    }

    private fun buildInsecureClient() : Client {
        val sslContext = SSLContext.getInstance("TLSv1.2")
        val trustManagerArray = arrayOf(NullX509TrustManager())
        sslContext.init(null, trustManagerArray, null)
        return ClientBuilder.newBuilder()
                .hostnameVerifier(NullHostnameVerifier())
                .sslContext(sslContext)
                .build()
    }

    private fun <T> request(method: String, path: String, params: Any, outputClass: Class<T>) : T {
        // TODO, figure out timeouts
        // TODO, proxy?
        // TODO, https?
        // TODO, add logging
        log.debug("Attempting {} {}", method, path)
        log.trace("(with params: {})", params)
        var target = webTarget.path(path)
        
        var builder = if (method in listOf("GET", "DELETE")){
            when (params) {
                is Map<*, *> -> params.forEach { (key, value) -> target = target.queryParam(URLEncoder.encode(key.toString(), "UTF-8"), URLEncoder.encode(value.toString(), "UTF-8"))}
            }
            target.request()
        } else {
            target.request()
        }
        
        builder = builder
                .header("User-Agent", userAgent)
                .header("Content-Type", "application/json")
                .header("X-Swiftype-Client", CLIENT_NAME)
                .header("X-Swiftype-Client-Version", CLIENT_VERSION)
                .header("Authorization", "Bearer $accessToken")
        
        val invocation = if (method in listOf("POST", "PUT")) {
            val entity = Entity.entity(params, MediaType.APPLICATION_JSON)
            when (method) {
                "POST" -> builder.buildPost(entity)
                "PUT" -> builder.buildPut(entity)
                else -> throw RuntimeException("Unreachable code")
            }
        } else {
            when (method) {
                "GET" -> builder.buildGet()
                "DELETE" -> builder.buildDelete()
                else -> throw RuntimeException("Unreachable code")
            }
        }
        
        val response = handleResponse(invocation.invoke())
        
        return response.readEntity(outputClass)
    }

    open class WorkplaceSearchClientException(message: String?): RuntimeException(message)
    class InvalidCredentialsException: WorkplaceSearchClientException(null)
    class NonExistentRecordException: WorkplaceSearchClientException(null)
    class BadRequestException(message: String): WorkplaceSearchClientException(message)
    class ForbiddenException: WorkplaceSearchClientException(null)
    class UnexpectedHTTPException(message: String): WorkplaceSearchClientException(message)
    private fun handleResponse(response: Response) : Response {
        log.debug("Got response with code: {}", response.status)
        when (response.statusInfo.toEnum()) {
            Response.Status.OK -> return response
            Response.Status.UNAUTHORIZED -> throw InvalidCredentialsException()
            Response.Status.NOT_FOUND -> throw NonExistentRecordException()
            Response.Status.BAD_REQUEST -> throw BadRequestException("${response.status} ${response.readEntity(String::class.java)}")
            Response.Status.FORBIDDEN -> throw ForbiddenException()
            else -> throw UnexpectedHTTPException("${response.status} ${response.readEntity(String::class.java)}")
        }
    }
}
