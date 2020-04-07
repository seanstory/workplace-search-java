package com.sstory.workplace.search.client

interface Permissions{
    fun listAllPermissions(contentSourceKey: String, current: Int = 1, size: Int = 25) {
        TODO()
    }
    
    fun getUserPermissions(contentSourceKey: String, user: String) {
        TODO()
    }
    
    fun updateUserPermissions(contentSourceKey: String, user: String, options: Map<String, Any>){
        TODO()
    }
    
    fun removeUserPermissions(contentSourceKey: String, user: String, options: Map<String, Any>){
        TODO()
    }
}
