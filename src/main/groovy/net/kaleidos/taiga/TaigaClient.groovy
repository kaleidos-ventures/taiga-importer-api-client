package net.kaleidos.taiga

import groovy.util.logging.Log4j
import net.kaleidos.domain.Issue
import net.kaleidos.domain.Membership
import net.kaleidos.domain.Project
import net.kaleidos.domain.Role
import net.kaleidos.domain.User
import net.kaleidos.domain.Wikilink
import net.kaleidos.domain.Wikipage
import net.kaleidos.taiga.builder.IssueBuilder
import net.kaleidos.taiga.builder.MembershipBuilder
import net.kaleidos.taiga.builder.ProjectBuilder
import net.kaleidos.taiga.builder.RoleBuilder
import net.kaleidos.taiga.builder.UserBuilder
import net.kaleidos.taiga.builder.WikilinkBuilder
import net.kaleidos.taiga.builder.WikipageBuilder
import net.kaleidos.taiga.mapper.Mappers

@Log4j
class TaigaClient extends BaseClient {

    private static final Map URLS_IMPORTER = [
        projects: '/api/v1/importer',
        issues  : '/api/v1/importer/${projectId}/issue',
    ]

    private static final Map URLS = [
        auth           : '/api/v1/auth',
        projects       : '/api/v1/projects',
        issueTypes     : '/api/v1/issue-types',
        issueStatuses  : '/api/v1/issue-statuses',
        issuePriorities: '/api/v1/priorities',
        issueSeverities: '/api/v1/severities',
        issues         : '/api/v1/issues',
        roles          : '/api/v1/roles',
        memberships    : '/api/v1/memberships',
        registerUsers  : '/api/v1/auth/register',
        wikis          : '/api/v1/wiki',
        wikiLinks      : '/api/v1/wiki-links',
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
    List<Map> getProjects() {
        this.doGet(URLS.projects)
    }

    Project getProjectById(Long projectId) {
        def json = this.doGet("${URLS.projects}/${projectId}")

        new ProjectBuilder().build(json)
    }

    Project createProject(Project project) {
        log.debug "Saving project ==> ${project.name}"

        def params = Mappers.map(project)
        def json = this.doPost(URLS_IMPORTER.projects, params)

        new ProjectBuilder().build(json)
    }

    void deleteProject(Project project) {
        log.debug "Deleting project ==> ${project.id} - ${project.name}"
        this.doDelete("${URLS.projects}/${project.id}")
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
    Issue createIssue(Issue issue) {
        def url = this.merge(URLS_IMPORTER.issues, [projectId: issue.project.id])

        def params = Mappers.map(issue)
        def json = this.doPost(url, params)

        new IssueBuilder().build(json, issue.project)
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

    Wikilink createWikiLink(String title, String href, Project project) {
        def params = [
            title  : title,
            href   : href,
            project: project.id
        ]
        def json = this.doPost(URLS.wikiLinks, params)

        new WikilinkBuilder().build(json, project)
    }
}