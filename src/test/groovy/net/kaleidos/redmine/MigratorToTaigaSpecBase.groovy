package net.kaleidos.redmine

import groovy.util.logging.Log4j
import net.kaleidos.domain.Project
import net.kaleidos.taiga.TaigaClient
import spock.lang.Specification

@Log4j
class MigratorToTaigaSpecBase extends Specification {

    TaigaClient createTaigaClient() {
        return createTaigaClientBase()
    }

    TaigaClient createTaigaAdminClient() {
        return createTaigaClientBase("admin")
    }

    TaigaClient createTaigaClientBase(String specialUser = "") {
        def config =
            new ConfigSlurper()
                .parse(new File("src/test/resources/taiga${specialUser ? '_' + specialUser : ''}.groovy").text)
        def client = new TaigaClient(config.host)

        return client.authenticate(config.user, config.passwd)
    }

    void deleteTaigaProjects() {
        TaigaClient taigaClient = createTaigaAdminClient()

        taigaClient.with {
            projects.each { p ->
                log.debug "Deleting project '${p.name}' with id ${p.id}"
                deleteProject(new Project(id: p.id))
            }
        }
    }
}