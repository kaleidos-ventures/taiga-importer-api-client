package net.kaleidos.taiga

import groovy.util.logging.Log4j
import wslite.rest.RESTClient
import wslite.rest.RESTClientException
import wslite.rest.Response

@Log4j
class BaseClient {

    protected final RESTClient client

    BaseClient(String serverUrl) {
        client = new RESTClient(serverUrl)
        client.defaultContentTypeHeader = "application/json"
        client.defaultCharset = "UTF-8"
        client.defaultAcceptHeader = "application/json"
    }

    protected doGet(String url, Map params = [:]) {
        withClient { RESTClient client ->
            Response response = client.get(
                path: url,
                query: params
            )

            response.json
        }
    }

    protected doPost(String url, Map params = [:]) {
        withClient { RESTClient client ->
            def response = client.post(path: url) {
                json params
            }

            response.json
        }
    }

    protected doDelete(String url) {
        withClient { RESTClient client ->
            client.delete(path: url)
        }
    }

    private withClient(Closure cl) {
        try {
            cl client
        } catch (RESTClientException e) {
            log.error "Server response ===> " + e.response.contentAsString
            throw e
        }
    }
}