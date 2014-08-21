package net.kaleidos.taiga
import groovy.util.logging.Log4j
import net.kaleidos.domain.issue.IssuePriority
import net.kaleidos.domain.issue.IssueStatus
import net.kaleidos.domain.issue.IssueType
import net.kaleidos.domain.project.Project
import net.kaleidos.taiga.binding.issue.IssuePriorityBinding
import net.kaleidos.taiga.binding.issue.IssueStatusBinding
import net.kaleidos.taiga.binding.issue.IssueTypeBinding
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
        log.debug "Saving ==> ${project.name}"

        def params = [name: project.name, description: project.description]
        def response = this.doPost(URLS.projects, params)

        return ProjectBinding.bind(project, response)
    }

    TaigaClient deleteProjectById(String id) {
        log.debug "Deleting ==> ${id}"

        this.doDelete("${URLS.projects}/$id")
        this
    }

    List<Map> getProjects() {
        // TODO this has to be paginated
        return this.doGet("${URLS.projects}?page_size=500")
    }

    TaigaClient deleteAllIssueTypes(Project project) {
        def issueTypes = project.issueTypes
        issueTypes.each { IssueType it ->
            this.doDelete("${URLS.issueTypes}/${it.id}")
        }
        project.issueTypes = []

        this
    }

    IssueType addIssueType(String name, Project project) {
        def params = [project: project.id, name: name]
        def response = this.doPost(URLS.issueTypes, params)

        def issueType = IssueTypeBinding.bind(new IssueType(), response)
        project.issueTypes << issueType

        issueType
    }

    TaigaClient deleteAllIssueStatuses(Project project) {
        def issueStatuses = project.issueStatuses
        issueStatuses.each { IssueStatus is ->
            this.doDelete("${URLS.issueStatuses}/${is.id}")
        }
        project.issueStatuses = []

        this
    }

    IssueStatus addIssueStatus(String name, Project project) {
        def params = [project: project.id, name: name]
        def response = this.doPost(URLS.issueStatuses, params)

        def issueStatus = IssueStatusBinding.bind(new IssueStatus(), response)
        project.issueStatuses << issueStatus

        issueStatus
    }

    TaigaClient deleteAllIssuePriorities(Project project) {
        def issuePriorities = project.issuePriorities
        issuePriorities.each { IssuePriority ip ->
            this.doDelete("${URLS.issuePriorities}/${ip.id}")
        }
        project.issuePriorities = []

        this
    }

    IssuePriority addIssuePriority(String name, Project project) {
        def params = [project: project.id, name: name]
        def response = this.doPost(URLS.issuePriorities, params)

        def issuePriority = IssuePriorityBinding.bind(new IssuePriority(), response)
        project.issuePriorities << issuePriority

        issuePriority
    }
}
