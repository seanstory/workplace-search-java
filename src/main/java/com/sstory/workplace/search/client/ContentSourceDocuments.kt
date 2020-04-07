package com.sstory.workplace.search.client

//TODO, javadoc
interface ContentSourceDocuments : BaseClient {
    
    fun indexDocuments(contentSourceKey: String, documents: List<Map<String, Any>>) : List<Map<String, Any>> {
        return asyncCreateOrUpdateDocuments(contentSourceKey, documents)
    }
    
    fun destroyDocuments(contentSourceKey: String, documentIds: List<String>) : List<Map<String, Any>> {
        return post("sources/${contentSourceKey}/documents/bulk_destroy.json", documentIds, List::class.java) as List<Map<String, Any>>
    }
    
    private fun asyncCreateOrUpdateDocuments(contentSourceKey: String, documents: List<Map<String, Any>>) : List<Map<String, Any>> {
        return post("sources/${contentSourceKey}/documents/bulk_create.json", documents, List::class.java) as List<Map<String, Any>>
    }
    
    
}
