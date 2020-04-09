package com.sstory.workplace.search.sdk.api;

import java.util.LinkedHashMap;
import java.util.Map;

//TODO add some validations? Like against a schema, and the list of allowed props?
public abstract class DocumentBase{

    private final String id;
    private final String body;

    public DocumentBase(String id, String body){
        this.id = id;
        this.body = body;
    }

    public String getId() {
        return id;
    }

    public String getBody() {
        return body;
    }

    public abstract Map<String, Object> getMetadata();

    public Map<String, Object> toMap(){
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", getId());
        map.put("body", getBody());
        for(Map.Entry<String, Object> metaEntry: getMetadata().entrySet()){
            map.put(metaEntry.getKey(), metaEntry.getValue());
        }
        return map;
    }

    @Override
    public boolean equals(Object obj){
        if (obj instanceof DocumentBase){
            return getId().equals(((DocumentBase)obj).getId());
        }
        return false;
    }

    @Override
    public int hashCode(){
        return getId().hashCode();
    }

    @Override
    public String toString(){
        return this.getClass().getSimpleName() + "(id: '" + getId() + "', body: '" + getBody() + "')";
    }
}
