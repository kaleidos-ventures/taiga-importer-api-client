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
    static final String PATH = "net/kaleidos/redmine"

    void setup() {
        deleteTaigaProjects()
    }

    @IgnoreRest
    void 'Migrate all active projects basic structure'() {
        setup: 'Mocking redmine communication'
            HttpClient http = Stub(HttpClient) {
                execute(_) >>> [
                    buildResponseWithJson("${PATH}/projects.json"),
                    buildResponseWithJson("${PATH}/trackers.json"),
                    buildResponseWithJson("${PATH}/issue_statuses.json"),
                    buildResponseWithJson("${PATH}/issue_priorities.json"),
                    buildResponseWithJson("${PATH}/projectdetail1.json"),
                    buildResponseWithJson("${PATH}/memberships.json"),
                    buildResponseWithJson("${PATH}/user1.json"),
                    buildResponseWithJson("${PATH}/projectdetail2.json"),
                    buildResponseWithJson("${PATH}/memberships.json"),
                    buildResponseWithJson("${PATH}/user1.json")
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
            projectList.taigaProject.every(has('roles'))
            projectList.taigaProject.every(has('memberships'))
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
                    buildResponseWithJson("${PATH}/projects_only_one.json"),
                    buildResponseWithJson("${PATH}/trackers.json"),
                    buildResponseWithJson("${PATH}/issue_statuses.json"),
                    buildResponseWithJson("${PATH}/issue_priorities.json"),
                    buildResponseWithJson("${PATH}/projectdetail1.json"),
                    buildResponseWithJson("${PATH}/issues.json"),
                    buildResponseWithJson("${PATH}/user1.json"),
                    buildResponseWithJson("${PATH}/user1.json")
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
        and: 'Making sure types are present'
            issues.tracker == ['Task']
    }

    void 'Migrate issues from a given project'() {
        setup: 'Mocking redmine communication'
            HttpClient http = Stub(HttpClient) {
                execute(_) >>> [
                    buildResponseWithJson("${PATH}/projects_only_one.json"),
                    buildResponseWithJson("${PATH}/trackers.json"),
                    buildResponseWithJson("${PATH}/issue_statuses.json"),
                    buildResponseWithJson("${PATH}/issue_priorities.json"),
                    buildResponseWithJson("${PATH}/projectdetail1.json"),
                    buildResponseWithJson("${PATH}/issues.json"),
                    buildResponseWithJson("${PATH}/user1.json"),
                    buildResponseWithJson("${PATH}/issue_17002.json"),
                    buildResponseWithJson("${PATH}/user1.json")
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

    void 'Migrate wiki pages from a given project'() {
        setup: 'Mocking redmine communication'
            HttpClient http = Stub(HttpClient) {
                execute(_) >>> [
                    buildResponseWithJson("${PATH}/projects_only_one.json"),
                    buildResponseWithJson("${PATH}/trackers.json"),
                    buildResponseWithJson("${PATH}/issue_statuses.json"),
                    buildResponseWithJson("${PATH}/issue_priorities.json"),

                    buildResponseWithJson("${PATH}/projectdetail1.json"),
                    buildResponseWithJson("${PATH}/issues.json"),
                    buildResponseWithJson("${PATH}/user1.json"),
                    buildResponseWithJson("${PATH}/issue_17002.json"),
                    buildResponseWithJson("${PATH}/user1.json"),

                    buildResponseWithJson("${PATH}/wiki_index.json"),
                    buildResponseWithJson("${PATH}/wiki_page3_wiki.json"),
                    buildResponseWithJson("${PATH}/wiki_page1.json"),
                    buildResponseWithJson("${PATH}/wiki_page2.json"),
                    buildResponseWithJson("${PATH}/wiki_page3_wiki.json")
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
