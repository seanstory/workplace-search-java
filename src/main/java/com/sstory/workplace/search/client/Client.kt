package com.sstory.workplace.search.client

import java.lang.RuntimeException
import java.net.URLEncoder
import javax.ws.rs.client.ClientBuilder
import javax.ws.rs.client.Entity
import javax.ws.rs.client.WebTarget
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

val CLIENT_VERSION = "0.0.1"
val CLIENT_NAME = "elastic-workplace-search-ruby"
val DEFAULT_ENDPOINT = "http://localhost:3002/api/ws/v1/"
val DEFAULT_TIMEOUT = 15
data class Client @JvmOverloads constructor(var accessToken: String,
                   var endpoint: String = DEFAULT_ENDPOINT,
                   var userAgent: String? = null
                   //var openTimeout: Int = DEFAULT_TIMEOUT,
                   //var proxy: String? = null,
                   //var overallTimeout : Float = DEFAULT_TIMEOUT.toFloat()
                )
    : BaseClient, Permissions, ContentSourceDocuments {
    
    val restClient = ClientBuilder.newClient()
    val webTarget: WebTarget = restClient.target(endpoint)
    

    override fun <T> get(path: String, params: Map<String, Any>, outputClass: Class<T>) = request("GET", path, params, outputClass)
    override fun <T> post(path: String, params: Map<String, Any>, outputClass: Class<T>) = request("POST", path, params, outputClass)
    override fun <T> post(path: String, params: List<Any>, outputClass: Class<T>) = request("POST", path, params, outputClass)
    override fun <T> put(path: String, params: Map<String, Any>, outputClass: Class<T>) = request("PUT", path, params, outputClass)
    override fun <T> put(path: String, params: List<Any>, outputClass: Class<T>) = request("PUT", path, params, outputClass)
    override fun <T> delete(path: String, params: Map<String, Any>, outputClass: Class<T>) = request("DELETE", path, params, outputClass)
    
    private fun <T> request(method: String, path: String, params: Any, outputClass: Class<T>) : T {
        // TODO, figure out timeouts
        // TODO, proxy?
        // TODO, https?
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
        
        val response = handleErrors(invocation.invoke())
        
        return response.readEntity(outputClass)
    }

    
    class InvalidCredentialsException: RuntimeException()
    class NonExistentRecordException: RuntimeException()
    class BadRequestException(message: String): RuntimeException(message)
    class ForbiddenException: RuntimeException()
    class UnexpectedHTTPException(message: String): RuntimeException(message)
    private fun handleErrors(response: Response) : Response {
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
