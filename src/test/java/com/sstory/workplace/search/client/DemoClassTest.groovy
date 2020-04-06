package com.sstory.workplace.search.client

import spock.lang.Specification

class DemoClassTest extends Specification {

    def "test java"(){
        expect:
        new DemoJavaClass().foo == "foo"
    }

    def "test kotlin"(){
        expect:
        new DemoKotlinClass().foo == "baz"
    }
}

