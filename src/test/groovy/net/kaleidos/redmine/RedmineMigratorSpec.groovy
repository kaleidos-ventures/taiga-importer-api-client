package net.kaleidos.redmine

import spock.lang.IgnoreRest

import net.kaleidos.domain.Issue
import net.kaleidos.domain.Project
import net.kaleidos.domain.IssueType
import net.kaleidos.domain.IssueStatus
import net.kaleidos.domain.IssuePriority

import net.kaleidos.taiga.TaigaClient

import com.taskadapter.redmineapi.RedmineManager

class RedmineMigratorSpec extends MigratorToTaigaSpecBase {

    static final Double HALF_PERCENTAGE = 0.5

    void setup() {
        deleteTaigaProjects()
    }

    void 'Migrate all active projects basic structure'() {
        setup: 'redmine and taiga clients'
            RedmineManager redmineClient = createRedmineClient()
            TaigaClient taigaClient = createTaigaClient()
            RedmineMigrator migrator = new RedmineMigrator(redmineClient, taigaClient)
        when: 'invoking all names'
            List<RedmineTaigaRef> projectList = migrator.migrateAllProjectBasicStructure()
        then: 'there should be at least one project'
            projectList.taigaProject.size() > 0
        and: 'all of them have id and name'
            projectList.taigaProject.every(hasId)
            projectList.taigaProject.every(hasName)
            projectList.taigaProject.every(has('issueTypes'))
        and: 'usually most projects have description'
            projectList
                .taigaProject
                .count(hasDescription)
                .div(projectList.size()) > HALF_PERCENTAGE
    }

    void 'Migrate issue trackers from a given project'() {
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
        and: 'migrating issue types of the current project'
            List<IssueType> projectIssueTypeList = migrator.migrateIssueTrackersByProject(project)
        then: 'there should be some types'
            projectIssueTypeList.size() > 0
        and: 'cant be repeated'
            projectIssueTypeList.unique {it.name}.size() == projectIssueTypeList.size()
    }

    void 'Migrate issue status from a given project'() {
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
        and: 'migrating issue statuses of the current project'
            List<IssueStatus> projectIssueStatusList = migrator.migrateIssueStatusesByProject(project)
        then: 'there should be some types'
            projectIssueStatusList.size() > 0
        and: 'cant be repeated'
            projectIssueStatusList.unique {it.name}.size() == projectIssueStatusList.size()
    }

    void 'Migrate issue priorities from a given project'() {
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
        and: 'migrating issue priorities of the current project'
            List<IssuePriority> priorities = migrator.migrateIssuePriorities(project)
        then: 'there should be some priorities'
            priorities.size() > 0
        and: 'all of them should have name'
            priorities.every(hasId)
            priorities.every(hasName)
    }

    void 'Migrate issues from a given project'() {
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
        then: 'there should be issues'
            issues.size() > 0
            issues.every(has('id'))
            issues.every(has('project'))
            issues.every(has('subject'))
            issues.every(has('status'))
            issues.every(has('priority'))
            issues.every(has('type'))
    }


    Closure<Boolean> has = { String field ->
        return { Object p -> p."$field" }
    }

    Closure<Boolean> hasId = has('id')
    Closure<Boolean> hasName = has('name')
    Closure<Boolean> hasDescription = { Project p -> p.description }

    RedmineManager createRedmineClient() {
        def config = new ConfigSlurper().parse(new File('src/test/resources/redmine.groovy').text)
        def manager = new RedmineManager(config.host, config.apiKey)

        return manager
    }

}
