package net.kaleidos.taiga

import groovy.util.logging.Log4j
import net.kaleidos.domain.Issue
import net.kaleidos.domain.Project
import net.kaleidos.domain.Wikilink
import net.kaleidos.domain.Wikipage
import net.kaleidos.taiga.builder.IssueBuilder
import net.kaleidos.taiga.builder.ProjectBuilder
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

    // PROJECTS
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

    // ISSUES
    Issue createIssue(Issue issue) {
        def url = this.merge(URLS_IMPORTER.issues, [projectId: issue.project.id])

        def params = Mappers.map(issue)
        def json = this.doPost(url, params)

        new IssueBuilder().build(json, issue.project)
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