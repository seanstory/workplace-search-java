package com.sstory.workplace.search.client

import java.lang.RuntimeException
import java.net.URLEncoder
import javax.ws.rs.client.ClientBuilder
import javax.ws.rs.client.Entity
import javax.ws.rs.client.WebTarget
import javax.ws.rs.core.GenericType
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

val CLIENT_VERSION = "0.0.1"
val CLIENT_NAME = "elastic-workplace-search-ruby"
val DEFAULT_ENDPOINT = "http://localhost:3002/api/ws/v1/"
val DEFAULT_TIMEOUT = 15
data class Client @JvmOverloads constructor(var accessToken: String,
                   var endpoint: String = DEFAULT_ENDPOINT,
                   var userAgent: String? = null,
                   var openTimeout: Int = DEFAULT_TIMEOUT,
                   var proxy: String? = null,
                   var overallTimeout : Float = DEFAULT_TIMEOUT.toFloat())
    : Permissions, ContentSourceDocuments {
    
    val restClient = ClientBuilder.newClient()
    val webTarget: WebTarget = restClient.target(endpoint)
    

    fun get(path: String, params: Map<String, String>) = request("GET", path, params)
    fun post(path: String, params: Map<String, String>) = request("POST", path, params)
    fun put(path: String, params: Map<String, String>) = request("PUT", path, params)
    fun delete(path: String, params: Map<String, String>) = request("DELETE", path, params)
    
    
    private fun request(method: String, path: String, params: Map<String,String>) : Map<String, Any>? {
        // TODO, figure out timeouts
        // TODO, proxy?
        // TODO, https?
        var target = webTarget.path(path)
        
        var builder = if (method in listOf("GET", "DELETE")){
            params.forEach { (key, value) -> target = target.queryParam(URLEncoder.encode(key, "UTF-8"), URLEncoder.encode(value, "UTF-8"))}
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
        
        val klass = object : GenericType<HashMap<String, Any>>() {
        
        }
        return response.readEntity(klass)
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
