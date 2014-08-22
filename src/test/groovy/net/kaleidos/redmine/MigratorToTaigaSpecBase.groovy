package net.kaleidos.redmine

import spock.lang.Specification

import groovy.util.logging.Log4j

import net.kaleidos.taiga.TaigaClient

@Log4j
class MigratorToTaigaSpecBase extends Specification {

    TaigaClient createTaigaClient() {
        def config = new ConfigSlurper().parse(new File('src/test/resources/taiga.groovy').toURL())
        def client = new TaigaClient(config.host)

        return client.authenticate(config.user, config.passwd)
    }

    void deleteTaigaProjects() {
        TaigaClient taigaClient = createTaigaClient()

        taigaClient.with {
            projects.each { p ->
                log.debug "Deleting project '${p.name}' with id ${p.id}"
                deleteProjectById("${p.id}")
            }
        }
    }

}
