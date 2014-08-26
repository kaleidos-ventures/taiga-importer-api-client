package net.kaleidos.taiga

import spock.lang.Specification

class TaigaSpecBase extends Specification {

    TaigaClient createAuthenticatedTaigaClient() {
        def config = new ConfigSlurper().parse(new File('src/test/resources/taiga.groovy').text)
        def client = new TaigaClient(config.host)

        return client.authenticate(config.user, config.passwd)
    }

    TaigaClient taigaClient

    def setup() {
        taigaClient = createAuthenticatedTaigaClient()
    }
}