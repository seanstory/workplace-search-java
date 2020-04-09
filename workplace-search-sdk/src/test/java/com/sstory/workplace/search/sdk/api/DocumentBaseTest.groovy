package com.sstory.workplace.search.sdk.api

import spock.lang.Specification

class DocumentBaseTest extends Specification {


    class MyDocument extends DocumentBase {

        MyDocument(String id, String body){
            super(id, body)
        }

        @Override
        Map<String, Object> getMetadata() {
            return null
        }
    }

    def "test toString"(){
        setup:
        def document = new MyDocument("id", "body")

        when:
        def string = document.toString()
        println string

        then:
        string.contains("id")
        string.contains("body")
    }
}
