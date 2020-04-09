package com.sstory.workplace.search.sdk.api

import spock.lang.Specification

class YamlConfigTest extends Specification {

    def "test yaml parsing"(){
        when:
        boolean wasException = false
        String exceptionMsg
        YamlConfig config
        try{
            config = new YamlConfig(path)
        } catch (Exception e) {
            wasException = true
            exceptionMsg = e.message
        }

        then:
        if (wasException){
            assert wasException == expectedException
            assert exceptionMsg.contains(expectedErrMsg)
        } else {
            assert config.accessToken == accessToken
            assert config.contentSourceKey == key
        }

        where:
        path | expectedException | expectedErrMsg | accessToken | key | endpoint
        "src/test/resources/one.yml" | _ | _ | "3a423c597442eddb09baad64793ff342fc0aa6da357f5227888d44b3386cf722" | "5e87603bf74c32fe5fa37d86" | "http://myhost:3003/api/ws/v1/"
        "src/test/resources/two.yml" | true | "must be configured" | _ | _ | _
        "src/test/resources/three.txt" | true | "must be a YAML file" | _ | _ | _
        "src/test/resources/four.yml" | true | "No such file or directory" | _ | _ | _
        "src/test/resources/five.yml" | _ | _ | "3a423c597442eddb09baad64793ff342fc0aa6da357f5227888d44b3386cf722" | "5e87603bf74c32fe5fa37d86" | "http://localhost:3002/api/ws/v1/"
    }
}
