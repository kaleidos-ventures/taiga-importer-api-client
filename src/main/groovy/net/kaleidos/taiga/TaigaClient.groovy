package net.kaleidos.taiga

import groovy.util.logging.Log4j
import net.kaleidos.domain.Issue
import net.kaleidos.domain.IssuePriority
import net.kaleidos.domain.IssueStatus
import net.kaleidos.domain.IssueType
import net.kaleidos.domain.Membership
import net.kaleidos.domain.Project
import net.kaleidos.domain.Role
import net.kaleidos.domain.User
import net.kaleidos.domain.Wikipage
import net.kaleidos.taiga.builder.IssueBuilder
import net.kaleidos.taiga.builder.IssuePriorityBuilder
import net.kaleidos.taiga.builder.IssueStatusBuilder
import net.kaleidos.taiga.builder.IssueTypeBuilder
import net.kaleidos.taiga.builder.MembershipBuilder
import net.kaleidos.taiga.builder.ProjectBuilder
import net.kaleidos.taiga.builder.RoleBuilder
import net.kaleidos.taiga.builder.UserBuilder
import net.kaleidos.taiga.builder.WikipageBuilder

@Log4j
class TaigaClient extends BaseClient {

    private final Map URLS = [
        auth           : "/api/v1/auth",
        projects       : "/api/v1/projects",
        issueTypes     : "/api/v1/issue-types",
        issueStatuses  : "/api/v1/issue-statuses",
        issuePriorities: "/api/v1/priorities",
        issues         : "/api/v1/issues",
        roles          : "/api/v1/roles",
        memberships    : "/api/v1/memberships",
        registerUsers  : "/api/v1/auth/register",
        wikis          : "/api/v1/wiki",
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
    Project createProject(String name, String description) {
        log.debug "Saving project ==> ${name}"

        def params = [name: name, description: description]
        def json = this.doPost(URLS.projects, params)

        new ProjectBuilder().build(json)
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

    void deleteProject(Project project) {
        log.debug "Deleting project ==> ${project.id} - ${project.name}"
        this.doDelete("${URLS.projects}/${project.id}")
    }

    Project getProjectById(Long projectId) {
        def json = this.doGet("${URLS.projects}/${projectId}")

        new ProjectBuilder().build(json)
    }

    // ROLES
    Role addRole(String name, Project project) {
        def params = [
            project: project.id,
            name   : name
        ]
        def json = this.doPost(URLS.roles, params)

        def role = new RoleBuilder().build(json)
        project.roles << role

        role
    }

    TaigaClient deleteAllRoles(Project project) {
        def roles = project.roles
        roles.each { Role r ->
            this.doDelete("${URLS.roles}/${r.id}")
        }
        project.roles = []

        this
    }

    // MEMBERSHIPS
    Membership createMembership(String email, String role, Project project) {
        def params = [
            project: project.id,
            role   : project.findRoleByName(role).id,
            email  : email
        ]
        def json = this.doPost(URLS.memberships, params)

        def membership = new MembershipBuilder().build(json, project)
        project.memberships << membership

        membership
    }

    // ISSUES
    Issue createIssue(Project project, String type, String status, String priority, String subject, String description) {
        def params = [
            type       : project.findIssueTypeByName(type).id,
            status     : project.findIssueStatusByName(status).id,
            priority   : project.findIssuePriorityByName(priority).id,
            subject    : subject,
            description: description,
            project    : project.id,
            severity   : project.defaultSeverity
        ]

        def json = this.doPost(URLS.issues, params)

        new IssueBuilder().build(json, project)
    }

    // ISSUE TYPES
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
        def json = this.doPost(URLS.issueTypes, params)

        def issueType = new IssueTypeBuilder().build(json)
        project.issueTypes << issueType

        issueType
    }

    // ISSUE STATUSES
    TaigaClient deleteAllIssueStatuses(Project project) {
        def issueStatuses = project.issueStatuses
        issueStatuses.each { IssueStatus is ->
            this.doDelete("${URLS.issueStatuses}/${is.id}")
        }
        project.issueStatuses = []

        this
    }

    IssueStatus addIssueStatus(String name, Boolean isClosed, Project project) {
        def params = [project: project.id, name: name, is_closed: isClosed]
        def json = this.doPost(URLS.issueStatuses, params)

        def issueStatus = new IssueStatusBuilder().build(json)
        project.issueStatuses << issueStatus

        issueStatus
    }

    // ISSUE PRIORITIES
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
        def json = this.doPost(URLS.issuePriorities, params)

        def issuePriority = new IssuePriorityBuilder().build(json)
        project.issuePriorities << issuePriority

        issuePriority
    }

    // USERS
    User registerUser(String email, String password, String token) {
        def params = [
            email    : email,
            token    : token,
            username : email,
            existing : false,
            full_name: email,
            password : password,
            type     : 'private',
        ]
        def json = this.doPost(URLS.registerUsers, params)

        new UserBuilder().build(json)
    }

    // WIKIS
    Wikipage createWiki(String slug, String content, Project project) {
        def params = [
            slug   : slug,
            content: content,
            project: project.id,
        ]

        def json = this.doPost(URLS.wikis, params)

        new WikipageBuilder().build(json, project)
    }
}