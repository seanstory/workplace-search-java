package com.sstory.workplace.search.sdk.api

import groovy.transform.InheritConstructors

class TestSource implements Source<TestDocument>{

    @Override
    Iterator<TestDocument> getDocuments() {
        return [new TestDocument("foo", "bar")].iterator()
    }

    @InheritConstructors
    static class TestDocument extends DocumentBase{

        @Override
        Map<String, Object> getMetadata() {
            return [:]
        }
    }
}
