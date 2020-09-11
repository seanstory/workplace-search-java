package com.seanjstory.workplace.search.client

//TODO: Javadoc
interface Permissions : BaseClient {
    
    fun listAllPermissions(contentSourceKey: String) : Map<String, Any> {
        return listAllPermissions(contentSourceKey, 1, 25)
    }
    
    fun listAllPermissions(contentSourceKey: String, current: Int) : Map<String, Any> {
        return listAllPermissions(contentSourceKey, current, 25)
    }
    
    fun listAllPermissions(contentSourceKey: String, current: Int, size: Int) : Map<String, Any> {
        return get("sources/${contentSourceKey}/permissions", mapOf("page[current]" to current, "page[size]" to size), Map::class.java) as Map<String, Any>
    }
    
    fun getUserPermissions(contentSourceKey: String, user: String) : Map<String, Any> {
        return get("sources/${contentSourceKey}/permissions/${user}", emptyMap(), Map::class.java) as Map<String, Any>
    }
    
    fun updateUserPermissions(contentSourceKey: String, user: String, options: Map<String, Any>) : Map<String, Any>{
        return post("sources/${contentSourceKey}/permissions/${user}", options, Map::class.java) as Map<String, Any>
    }
    
    fun addUserPermissions(contentSourceKey: String, user: String, options: Map<String, Any>) : Map<String, Any>{
        return post("sources/${contentSourceKey}/permissions/${user}/add", options, Map::class.java) as Map<String, Any>
    }
    
    fun removeUserPermissions(contentSourceKey: String, user: String, options: Map<String, Any>) : Map<String, Any>{
        return post("sources/${contentSourceKey}/permissions/${user}/remove", options, Map::class.java) as Map<String, Any>
    }
}
