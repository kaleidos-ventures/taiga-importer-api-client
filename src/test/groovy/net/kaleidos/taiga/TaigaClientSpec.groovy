package net.kaleidos.taiga

import spock.lang.Specification

class TaigaClientSpec extends Specification {

    TaigaClient taigaClient
    ConfigObject config

    def setup() {
        config = new ConfigSlurper().parse(new File('src/test/resources/taiga.groovy').text)
        taigaClient = new TaigaClient(config.host)
    }

    void 'authenticate a user'() {
        when: 'a user tries to login'
            taigaClient.authenticate(config.user, config.passwd)

        then: 'the authorization header is set in the client'
            taigaClient.client.httpClient.defaultHeaders.Authorization.contains('Bearer')
            !taigaClient.client.httpClient.defaultHeaders.Authorization.contains('Bearer null')
    }
}