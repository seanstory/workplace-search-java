package com.sstory.workplace.search.client

import spock.lang.Ignore
import spock.lang.Specification

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
        def ouptut = client.get(path, [:])
        println ouptut

        then:
        1==1
    }
}
