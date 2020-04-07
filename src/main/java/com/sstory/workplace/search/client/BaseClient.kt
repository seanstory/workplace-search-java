package com.sstory.workplace.search.client

interface BaseClient {
    fun <T> get(path: String, params: Map<String, Any>, outputClass: Class<T>): T
    fun <T> post(path: String, params: Map<String, Any>, outputClass: Class<T>): T
    fun <T> post(path: String, params: List<Any>, outputClass: Class<T>): T
    fun <T> put(path: String, params: Map<String, Any>, outputClass: Class<T>): T
    fun <T> put(path: String, params: List<Any>, outputClass: Class<T>): T
    fun <T> delete(path: String, params: Map<String, Any>, outputClass: Class<T>): T
}
