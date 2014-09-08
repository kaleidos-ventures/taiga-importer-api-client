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
        def config = new ConfigSlurper().parse(new File('src/test/resources/taiga.groovy').text)
        def client = new TaigaClient(config.host)

        return client.authenticate(config.user, config.passwd)
    }

    void deleteTaigaProjects() {
        TaigaClient taigaClient = createTaigaClient()

        taigaClient.with {
            projects.each { p ->
                log.debug "Deleting project '${p.name}' with id ${p.id}"
                deleteProject(new Project(id:p.id))
            }
        }
    }

    String loadResourceAsString(String resource) {
        return RedmineMigratorSpec
            .classLoader
            .getResourceAsStream(resource)
            .text
    }

    HttpResponse buildResponseWithJson(String jsonResource) {
        def response =
            new BasicHttpResponse(HttpVersion.HTTP_1_1,HttpStatus.SC_OK,"OK")

        def entity =
            new StringEntity(
                loadResourceAsString(jsonResource),
                ContentType.APPLICATION_JSON
            )

        response.setEntity(entity)
        return response
    }

    RedmineManager buildRedmineClient(HttpClient httpClient) {
        return new RedmineManager(
            new Transport(
                new URIConfigurator("http://a", "0983hr0ih23roubk"),
                httpClient
            ),
            RedmineManagerFactory.createDefaultTransportConfig().shutdownListener
        )
    }

}
