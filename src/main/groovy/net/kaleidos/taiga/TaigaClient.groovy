package net.kaleidos.taiga

import groovy.util.logging.Log4j
import wslite.rest.RESTClient
import wslite.rest.Response

@Log4j
class TaigaClient {

    private final Map URLS = [
        auth    : "/api/v1/auth",
        projects: "/api/v1/projects"
    ]

    private final RESTClient client

    TaigaClient(String serverUrl) {
        client = new RESTClient(serverUrl)
        client.defaultContentTypeHeader = "application/json"
        client.defaultCharset = "UTF-8"
        client.defaultAcceptHeader = "application/json"
    }

    TaigaClient authenticate(String username, String password) {
        withClient { RESTClient client ->
            def response = client.post(path: URLS.auth) {
                json username: username,
                    password: password,
                    type: 'normal'
            }

            client.httpClient.defaultHeaders = [Authorization: "Bearer ${response.json.auth_token}"]

            this
        }
    }

    List<Map> getProjects() {
        withClient { RESTClient client ->
            Response response = client.get(path: URLS.projects)

            response.json
        }
    }

    private withClient(Closure cl) {
        try {
            cl client
        } catch (Exception e) {
            log.error "There was an error with Taiga", e
        }
    }
}
