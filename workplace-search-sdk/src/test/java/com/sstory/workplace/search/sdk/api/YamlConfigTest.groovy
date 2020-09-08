package com.sstory.workplace.search.sdk.api

import spock.lang.Specification

class YamlConfigTest extends Specification {

    def "test yaml parsing"(){
        when:
        boolean wasException = false
        String exceptionMsg
        YamlConfig config
        Class exceptionClass
        try{
            config = new YamlConfig(path)
        } catch (Exception e) {
            wasException = true
            exceptionMsg = e.message
            exceptionClass = e.class
        }

        then:
        if (wasException){
            assert wasException == expectedException
            if (exceptionMsg && expectedErrMsg) {
                assert exceptionMsg.contains(expectedErrMsg)
            } else {
                assert exceptionClass == expectedExceptionClass
            }

        } else {
            assert config.accessToken == accessToken
            assert config.contentSourceKey == key
        }

        where:
        path | expectedException | expectedErrMsg | expectedExceptionClass | accessToken | key | endpoint | security
        "src/test/resources/one.yml" | _ | _ | _ | "3a423c597442eddb09baad64793ff342fc0aa6da357f5227888d44b3386cf722" | "5e87603bf74c32fe5fa37d86" | "http://myhost:3003/api/ws/v1/" | 'secure'
        "src/test/resources/two.yml" | true | "must be configured" | _ | _ | _ | _ | _
        "src/test/resources/three.txt" | true | "must be a YAML file" | _ | _ | _ | _ | _
        "src/test/resources/four.yml" | true | null | FileNotFoundException | _ | _ | _ | _
        "src/test/resources/five.yml" | _ | _ | _ | "3a423c597442eddb09baad64793ff342fc0aa6da357f5227888d44b3386cf722" | "5e87603bf74c32fe5fa37d86" | "http://localhost:3002/api/ws/v1/" | 'secure'
        "src/test/resources/six.yml" | true | "must be one of: [secure, insecure]" | _ | _ | _ | _ | _
        "src/test/resources/seven.yml" | _ | _ | _ | "3a423c597442eddb09baad64793ff342fc0aa6da357f5227888d44b3386cf722" | "5e87603bf74c32fe5fa37d86" | "http://myhost:3003/api/ws/v1/" | 'insecure'
    }
}
