package com.sstory.workplace.search.sdk.api

import spock.lang.Specification

class SourcesUtilsTest extends Specification{

    def "test source lookup"(){
        when:
        def stream = new ByteArrayInputStream(text.getBytes("UTF-8"))
        def parser = new SourcesUtils(stream)

        then:
        parser.isEnabled(expectedContains)

        where:
        text             | expectedContains
        TestSource.getCanonicalName() | "test"
        TestSource.getCanonicalName() | "Test"
        TestSource.getCanonicalName() | "TestSource"
        TestSource.getCanonicalName() | "testsource"
    }
}
