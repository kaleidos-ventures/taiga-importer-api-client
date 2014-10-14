package net.kaleidos.taiga

import groovy.util.logging.Log4j
import net.kaleidos.domain.Issue
import net.kaleidos.domain.Milestone
import net.kaleidos.domain.Project
import net.kaleidos.domain.Task
import net.kaleidos.domain.UserStory
import net.kaleidos.domain.Wikilink
import net.kaleidos.domain.Wikipage
import net.kaleidos.taiga.builder.IssueBuilder
import net.kaleidos.taiga.builder.MilestoneBuilder
import net.kaleidos.taiga.builder.ProjectBuilder
import net.kaleidos.taiga.builder.TaskBuilder
import net.kaleidos.taiga.builder.UserStoryBuilder
import net.kaleidos.taiga.builder.WikilinkBuilder
import net.kaleidos.taiga.builder.WikipageBuilder
import net.kaleidos.taiga.mapper.Mappers

@Log4j
class TaigaClient extends BaseClient {

    private static final Map URLS_IMPORTER = [
        project  : '/api/v1/importer',
        issue    : '/api/v1/importer/${projectId}/issue',
        wikiPage : '/api/v1/importer/${projectId}/wiki_page',
        wikiLink : '/api/v1/importer/${projectId}/wiki_link',
        userStory: '/api/v1/importer/${projectId}/us',
        milestone: '/api/v1/importer/${projectId}/milestone',
        task     : '/api/v1/importer/${projectId}/task',
    ]

    private static final Map URLS = [
        auth    : '/api/v1/auth',
        projects: '/api/v1/projects',
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
    List<Project> getProjects() {
        def json = this.doGet("${URLS.projects}?page_size=500")

        json.collect { new ProjectBuilder().build(it, null) }
    }

    Project getProjectById(Long projectId) {
        def json = this.doGet("${URLS.projects}/${projectId}")

        new ProjectBuilder().build(json, null)
    }

    Project createProject(Project project) {
        log.debug "Saving project ==> ${project.name}"

        def params = Mappers.map(project)
        def json = this.doPost(URLS_IMPORTER.project, params)

        new ProjectBuilder().build(json, null)
    }

    void deleteProject(Project project) {
        log.debug "Deleting project ==> ${project.id} - ${project.name}"
        this.doDelete("${URLS.projects}/${project.id}")
    }

    // ISSUES
    Issue createIssue(Issue issue) {
        def url = this.merge(URLS_IMPORTER.issue, [projectId: issue.project.id])

        def params = Mappers.map(issue)
        def json = this.doPost(url, params)

        new IssueBuilder().build(json, issue.project)
    }

    // WIKIS
    Wikipage createWiki(Wikipage wikipage) {
        def url = this.merge(URLS_IMPORTER.wikiPage, [projectId: wikipage.project.id])

        def params = Mappers.map(wikipage)
        def json = this.doPost(url, params)

        new WikipageBuilder().build(json, wikipage.project)
    }

    Wikilink createWikiLink(Wikilink wikilink) {
        def url = this.merge(URLS_IMPORTER.wikiLink, [projectId: wikilink.project.id])

        def params = Mappers.map(wikilink)
        def json = this.doPost(url, params)

        new WikilinkBuilder().build(json, wikilink.project)
    }

    // USER STORIES
    UserStory createUserStory(UserStory userStory) {
        def url = this.merge(URLS_IMPORTER.userStory, [projectId: userStory.project.id])

        def params = Mappers.map(userStory)
        def json = this.doPost(url, params)

        new UserStoryBuilder().build(json, userStory.project)
    }

    // MILESTONES
    Milestone createMilestone(Milestone milestone) {
        def url = this.merge(URLS_IMPORTER.milestone, [projectId: milestone.project.id])

        def params = Mappers.map(milestone)
        def json = this.doPost(url, params)

        new MilestoneBuilder().build(json, milestone.project)
    }

    // TASKS
    Task createTask(Task task) {
        def url = this.merge(URLS_IMPORTER.task, [projectId: task.project.id])

        def params = Mappers.map(task)
        def json = this.doPost(url, params)

        new TaskBuilder().build(json, task.project)
    }
}