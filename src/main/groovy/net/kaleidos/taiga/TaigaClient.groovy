package net.kaleidos.taiga

import groovy.util.logging.Log4j
import net.kaleidos.domain.issue.IssuePriority
import net.kaleidos.domain.issue.IssueStatus
import net.kaleidos.domain.project.Project
import net.kaleidos.taiga.binding.project.ProjectBinding
import wslite.rest.RESTClient
import wslite.rest.Response

@Log4j
class TaigaClient {

    private final Map URLS = [
        auth           : "/api/v1/auth",
        projects       : "/api/v1/projects",
        issueStatuses  : "/api/v1/issue-statuses",
        issuePriorities: "/api/v1/priorities",
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

    Project saveProject(Project project) {
        withClient { RESTClient client ->
            def response = client.post(path: URLS.projects) {
                json name: project.name,
                    description: project.description
            }

            ProjectBinding.bind(project, response)
        }
    }

    TaigaClient deleteAllIssueStatuses(Project project) {
        def issueStatuses = project.issueStatuses

        issueStatuses.each { IssueStatus is ->
            withClient { RESTClient client ->
                client.delete(path: "${URLS.issueStatuses}/${is.id}")
            }
        }

        this
    }

    TaigaClient deleteAllIssuePriorities(Project project) {
        def issuePriorities = project.issuePriorities

        issuePriorities.each { IssuePriority ip ->
            withClient { RESTClient client ->
                client.delete(path: "${URLS.issuePriorities}/${ip.id}")
            }
        }

        this
    }

    private withClient(Closure cl) {
        try {
            cl client
        } catch (Exception e) {
            log.error "There was an error with Taiga", e
        }
    }
}
