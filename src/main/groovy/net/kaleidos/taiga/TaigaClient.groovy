package net.kaleidos.taiga
import groovy.util.logging.Log4j
import net.kaleidos.domain.issue.IssuePriority
import net.kaleidos.domain.issue.IssueStatus
import net.kaleidos.domain.issue.IssueType
import net.kaleidos.domain.project.Project
import net.kaleidos.taiga.binding.project.ProjectBinding

@Log4j
class TaigaClient extends BaseClient {

    private final Map URLS = [
        auth           : "/api/v1/auth",
        projects       : "/api/v1/projects",
        issueTypes     : "/api/v1/issue-types",
        issueStatuses  : "/api/v1/issue-statuses",
        issuePriorities: "/api/v1/priorities",
    ]

    TaigaClient(String serverUrl) {
        super(serverUrl)
    }

    TaigaClient authenticate(String username, String password) {
        def params = [username: username, password: password, type: 'normal']

        def response = this.doPost(URLS.auth, params)
        client.httpClient.defaultHeaders = [Authorization: "Bearer ${response.auth_token}"]

        this
    }


    Project saveProject(Project project) {
        def params = [name: project.name, description: project.description]
        def response = this.doPost(URLS.projects, params)

        ProjectBinding.bind(project, response)
    }

    List<Map> getProjects() {
        this.doGet(URLS.projects)
    }

    TaigaClient deleteAllIssueTypes(Project project) {
        def issueTypes = project.issueTypes
        issueTypes.each { IssueType it ->
            this.doDelete("${URLS.issueTypes}/${it.id}")
        }
        project.issueTypes = []

        this
    }

    TaigaClient deleteAllIssueStatuses(Project project) {
        def issueStatuses = project.issueStatuses
        issueStatuses.each { IssueStatus is ->
            this.doDelete("${URLS.issueStatuses}/${is.id}")
        }
        project.issueStatuses = []

        this
    }

    TaigaClient deleteAllIssuePriorities(Project project) {
        def issuePriorities = project.issuePriorities
        issuePriorities.each { IssuePriority ip ->
            this.doDelete("${URLS.issuePriorities}/${ip.id}")
        }
        project.issuePriorities = []

        this
    }

    }
}
