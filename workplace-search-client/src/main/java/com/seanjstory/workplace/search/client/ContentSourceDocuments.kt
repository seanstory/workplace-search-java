package com.seanjstory.workplace.search.client

//TODO, javadoc
interface ContentSourceDocuments : BaseClient {
    
    fun indexDocuments(contentSourceKey: String, documents: List<Map<String, Any>>) : Map<String, Any> {
        return asyncCreateOrUpdateDocuments(contentSourceKey, documents)
    }
    
    fun destroyDocuments(contentSourceKey: String, documentIds: List<String>) : Map<String, Any> {
        return post("sources/${contentSourceKey}/documents/bulk_destroy.json", documentIds, List::class.java) as Map<String, Any>
    }
    
    private fun asyncCreateOrUpdateDocuments(contentSourceKey: String, documents: List<Map<String, Any>>) : Map<String, Any> {
        return post("sources/${contentSourceKey}/documents/bulk_create.json", documents, Map::class.java) as Map<String, Any>
    }
    
    
}
