package net.kaleidos.redmine

import spock.lang.Ignore
import spock.lang.IgnoreRest

import net.kaleidos.domain.User
import net.kaleidos.domain.Issue
import net.kaleidos.domain.Wikipage
import net.kaleidos.domain.IssueStatus
import net.kaleidos.domain.Project
import net.kaleidos.taiga.TaigaClient

import org.apache.http.client.HttpClient
import com.taskadapter.redmineapi.RedmineManager

import groovy.util.logging.Log4j

@Log4j
class RedmineMigratorSpec extends MigratorToTaigaSpecBase {

    static final Double HALF_PERCENTAGE = 0.5

    void setup() {
        deleteTaigaProjects()
    }

    void 'Migrate all active projects basic structure'() {
        setup: 'Mocking redmine communication'
            HttpClient http = Stub(HttpClient) {
                execute(_) >>> [
                    buildResponseWithJson("net/kaleidos/redmine/projects.json"),
                    buildResponseWithJson("net/kaleidos/redmine/trackers.json"),
                    buildResponseWithJson("net/kaleidos/redmine/issue_statuses.json"),
                    buildResponseWithJson("net/kaleidos/redmine/issue_priorities.json"),
                    buildResponseWithJson("net/kaleidos/redmine/projectdetail1.json"),
                    buildResponseWithJson("net/kaleidos/redmine/projectdetail2.json")
                ]
            }
        and: 'building a redmine migrator mocking redmine integration'
            RedmineMigrator migrator =
                new RedmineMigrator(
                    buildRedmineClient(http),
                    createTaigaClient())
        when: 'invoking all names'
            List<RedmineTaigaRef> projectList = migrator.migrateAllProjects()
        then: 'there should be at least one project'
            projectList.taigaProject.size() > 0
        and: 'all of them have id and name'
            projectList.taigaProject.every(hasId)
            projectList.taigaProject.every(hasName)
            projectList.taigaProject.every(has('issueTypes'))
            projectList.taigaProject.every(has('issueSeverities'))
        and: 'usually most projects have description'
            projectList
                .taigaProject
                .count(hasDescription)
                .div(projectList.size()) > HALF_PERCENTAGE
    }

    void 'Generating taiga issues with user email'() {
        setup: 'Mocking redmine communication'
            HttpClient http = Stub(HttpClient) {
                execute(_) >>> [
                    buildResponseWithJson("net/kaleidos/redmine/projects_only_one.json"),
                    buildResponseWithJson("net/kaleidos/redmine/trackers.json"),
                    buildResponseWithJson("net/kaleidos/redmine/issue_statuses.json"),
                    buildResponseWithJson("net/kaleidos/redmine/issue_priorities.json"),
                    buildResponseWithJson("net/kaleidos/redmine/projectdetail1.json"),
                    buildResponseWithJson("net/kaleidos/redmine/issues.json"),
                    buildResponseWithJson("net/kaleidos/redmine/user1.json"),
                    buildResponseWithJson("net/kaleidos/redmine/user1.json")
                ]
            }
        and: 'building a redmine migrator mocking redmine integration'
            RedmineMigrator migrator =
                new RedmineMigrator(
                    buildRedmineClient(http),
                    createTaigaClient())
        when: 'creating a new project and cleaning up its issue structure'
            RedmineTaigaRef project = migrator.migrateFirstProjectBasicStructure()
        and: 'migrating redmine current issues'
            List<Issue> issues =
                migrator
                    .getIssuesByProject(project)
                    .collect(migrator.fullfillUserMail)
        then: 'there should be issues'
            issues.size() > 0
            issues.every(has('tracker'))
            issues.every(has('subject'))
            issues.every(has('status'))
            issues.every(has('priority'))
            issues.every(has('userMail'))
    }

    void 'Migrate issues from a given project'() {
        setup: 'Mocking redmine communication'
            HttpClient http = Stub(HttpClient) {
                execute(_) >>> [
                    buildResponseWithJson("net/kaleidos/redmine/projects_only_one.json"),
                    buildResponseWithJson("net/kaleidos/redmine/trackers.json"),
                    buildResponseWithJson("net/kaleidos/redmine/issue_statuses.json"),
                    buildResponseWithJson("net/kaleidos/redmine/issue_priorities.json"),
                    buildResponseWithJson("net/kaleidos/redmine/projectdetail1.json"),
                    buildResponseWithJson("net/kaleidos/redmine/issues.json"),
                    buildResponseWithJson("net/kaleidos/redmine/user1.json"),
                    buildResponseWithJson("net/kaleidos/redmine/user1.json")
                ]
            }
        and: 'building a redmine migrator mocking redmine integration'
            RedmineMigrator migrator =
                new RedmineMigrator(
                    buildRedmineClient(http),
                    createTaigaClient())
        when: 'creating a new project and cleaning up its issue structure'
            RedmineTaigaRef project = migrator.migrateFirstProjectBasicStructure()
        and: 'migrating redmine current issues'
            List<Issue> issues = migrator.migrateIssuesByProject(project)
        then: 'there should be issues'
            issues.size() > 0
            issues.every(has('ref'))
            issues.every(has('project'))
            issues.every(has('subject'))
            issues.every(has('status'))
            issues.every(has('priority'))
            issues.every(has('type'))
    }

    @Ignore
    void 'Migrate wiki pages from a given project'() {
        setup: 'redmine and taiga clients'
            RedmineManager redmineClient = createRedmineClient()
            TaigaClient taigaClient = createTaigaClient()
            RedmineMigrator migrator = new RedmineMigrator(redmineClient, taigaClient)
        when: 'creating a new project and cleaning up its issue structure'
            RedmineTaigaRef project = migrator.migrateFirstProjectBasicStructure()
            taigaClient
                .deleteAllIssueTypes(project.taigaProject)
                .deleteAllIssueStatuses(project.taigaProject)
                .deleteAllIssuePriorities(project.taigaProject)
        and: 'migrating redmine current issues'
            List<Issue> issues = migrator.migrateIssuesByProject(project)
            List<Wikipage> wikiPages= migrator.migrateWikiPagesByProject(project)
        then: 'there should be issues'
            wikiPages.size() > 0
            wikiPages.every(has('slug'))
            wikiPages.every(has('content'))
            wikiPages.any { it.slug.toLowerCase() == 'wiki' }
            wikiPages.any { it.slug.toLowerCase() == 'home' }
    }

    Closure<Boolean> has = { String field ->
        return { Object p -> p."$field" }
    }

    Closure<Boolean> valueIsOne = { it.value == 1 }
    Closure<Boolean> hasId = has('id')
    Closure<Boolean> hasName = has('name')
    Closure<Boolean> hasDescription = { Project p -> p.description }

    RedmineManager createRedmineClient() {
        def config = new ConfigSlurper().parse(new File('src/test/resources/redmine.groovy').text)
        def manager = new RedmineManager(config.host, config.apiKey)

        return manager
    }
}
