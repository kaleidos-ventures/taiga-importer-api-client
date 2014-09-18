package net.kaleidos.redmine

import static groovyx.gpars.GParsPool.withPool

import spock.lang.Specification
import groovy.util.logging.Log4j
import net.kaleidos.taiga.TaigaClient
import net.kaleidos.domain.Project

import com.taskadapter.redmineapi.RedmineManager
import com.taskadapter.redmineapi.RedmineManagerFactory
import com.taskadapter.redmineapi.internal.Transport
import com.taskadapter.redmineapi.internal.URIConfigurator


import org.apache.http.HttpStatus
import org.apache.http.HttpVersion
import org.apache.http.HttpResponse
import org.apache.http.client.HttpClient
import org.apache.http.entity.ContentType
import org.apache.http.entity.StringEntity
import org.apache.http.message.BasicHeader
import org.apache.http.message.BasicHttpResponse
import org.apache.http.client.methods.HttpUriRequest


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
                deleteProject(new Project(id:p.id))
            }
        }
    }

}
