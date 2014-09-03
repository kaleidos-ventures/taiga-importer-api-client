package net.kaleidos.taiga

import net.kaleidos.domain.Project
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

    Project createProject() {
        def project = new Project().setName("name ${new Date().time}").setDescription("description")

        taigaClient.createProject(project)
    }

    void deleteProject(Project project) {
        taigaClient.deleteProject(project)
    }
}