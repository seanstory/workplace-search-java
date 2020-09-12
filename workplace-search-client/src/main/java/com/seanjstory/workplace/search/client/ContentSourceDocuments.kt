package com.seanjstory.workplace.search.client

//TODO, javadoc
interface ContentSourceDocuments : BaseClient {

    /**
     * Add/update the list of documents into the specified content source
     */
    fun indexDocuments(contentSourceKey: String, documents: List<Map<String, Any>>) : Map<String, Any> {
        return asyncCreateOrUpdateDocuments(contentSourceKey, documents)
    }

    /**
     * Destroy the documents specified by the list of IDs
     */
    fun destroyDocuments(contentSourceKey: String, documentIds: List<String>) : Map<String, Any> {
        return post("sources/${contentSourceKey}/documents/bulk_destroy.json", documentIds, List::class.java) as Map<String, Any>
    }

    /**
     * Add/update the list of documents into the specified content source
     */
    private fun asyncCreateOrUpdateDocuments(contentSourceKey: String, documents: List<Map<String, Any>>) : Map<String, Any> {
        return post("sources/${contentSourceKey}/documents/bulk_create.json", documents, Map::class.java) as Map<String, Any>
    }
    
    
}
