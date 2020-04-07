package com.sstory.workplace.search.client

import spock.lang.Ignore
import spock.lang.Specification

//TODO actually write tests
class ClientTest extends Specification {

    def "test client access"(){
        setup:
        def accessToken = "abcdefg"
        def client = new Client()

        when:
        def token = client.accessToken
        def endpoint = client.endpoint


        then:
        token == "abcdefg"
        endpoint == ClientKt.DEFAULT_ENDPOINT
    }

    @Ignore
    def "test client get"(){
        setup:
        def accessToken = "3a423c597442eddb09baad64793ff342fc0aa6da357f5227888d44b3386cf722"
        def path = "sources/5e87603bf74c32fe5fa37d86/permissions"
        def client = new Client(accessToken)

        when:
        def ouptut = client.get(path, [:], Map)
        println ouptut

        then:
        1==1
    }

    @Ignore
    def "test api calls"() {
        setup:
        def key = "5e87603bf74c32fe5fa37d86"
        def accessToken = "3a423c597442eddb09baad64793ff342fc0aa6da357f5227888d44b3386cf722"
        def client = new Client(accessToken)

        when:
        println client.listAllPermissions(key)

        then:
        1==1

        when:
        println client.get("sources/${key}/permissions", ["page[current]" : 1, "page[size]" : 10], Map)

        then:
        1==1

    }
}