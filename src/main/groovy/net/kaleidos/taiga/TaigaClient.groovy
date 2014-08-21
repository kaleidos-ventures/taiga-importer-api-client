package net.kaleidos.taiga

import groovy.util.logging.Log4j
import net.kaleidos.domain.issue.Issue
import net.kaleidos.domain.issue.IssuePriority
import net.kaleidos.domain.issue.IssueStatus
import net.kaleidos.domain.issue.IssueType
import net.kaleidos.domain.project.Project
import net.kaleidos.taiga.binding.issue.IssueBinding
import net.kaleidos.taiga.binding.issue.IssuePriorityBinding
import net.kaleidos.taiga.binding.issue.IssueStatusBinding
import net.kaleidos.taiga.binding.issue.IssueTypeBinding
import net.kaleidos.taiga.builder.ProjectBuilder

@Log4j
class TaigaClient extends BaseClient {

    private final Map URLS = [
        auth           : "/api/v1/auth",
        projects       : "/api/v1/projects",
        issueTypes     : "/api/v1/issue-types",
        issueStatuses  : "/api/v1/issue-statuses",
        issuePriorities: "/api/v1/priorities",
        issues         : "/api/v1/issues"
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

    // PROJECT
    Project saveProject(String name, String description) {
        log.debug "Saving ==> ${project.name}"

        def params = [name: name, description: description]
        def json = this.doPost(URLS.projects, params)

        new ProjectBuilder().build(json)
    }

    List<Map> getProjects() {
        // TODO this has to be paginated
        return this.doGet("${URLS.projects}?page_size=500")
    }

    Issue createIssue(Issue issue) {
        def params = [
            type       : issue.type.id,
            status     : issue.status.id,
            priority   : issue.priority.id,
            subject    : issue.subject,
            description: issue.description,
            project    : issue.project.id,
            severity   : issue.project.defaultSeverity
        ]

        def response = this.doPost(URLS.issues, params)

        IssueBinding.bind(new Issue(), response.json)
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
