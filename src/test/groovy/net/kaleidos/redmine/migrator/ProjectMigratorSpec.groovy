package net.kaleidos.redmine.migrator

import spock.lang.Specification

import net.kaleidos.redmine.RedmineTaigaRef
import net.kaleidos.redmine.MigratorToTaigaSpecBase
import net.kaleidos.redmine.testdata.ProjectDataProvider

import net.kaleidos.taiga.TaigaClient
import net.kaleidos.redmine.RedmineClient

class ProjectMigratorSpec extends MigratorToTaigaSpecBase {

    @Delegate
    ProjectDataProvider projectDataProvider = new ProjectDataProvider()

    void setup() {
        deleteTaigaProjects()
    }

    void 'migrating one project'() {
        given: 'a mocked redmine client'
            RedmineClient mockedClient = Stub(RedmineClient) {
                findAllMembershipByProjectIdentifier(_) >> buildRedmineMembershipList()
                findUserFullById(_) >> { Integer id -> buildRedmineUser("${randomTime}") }
                findAllTracker() >> buildRedmineTrackerList()
                findAllIssueStatus() >> buildRedmineStatusList()
                findAllIssuePriority() >> buildRedmineIssuePriorityList()
            }
        and: 'building a simple migrator instance'
            ProjectMigrator migrator =
                new ProjectMigrator(mockedClient, createTaigaClient())
        when: 'trying to migrate basic estructure of a redmine project'
            RedmineTaigaRef migratedProjectInfo = migrator.migrateProject(buildRedmineProject())
        then: 'checking the object'
            with(migratedProjectInfo.project) {
                name
                description
                roles.size() == 3
                memberships.size() == 4 // 3 + admin
                issueTypes.size() == 5
                issueStatuses.size() == 5
                issuePriorities.size() == 5
                issueSeverities.size() == 1
            }
        and: 'checking all membership user data (admin included)'
            with(migratedProjectInfo.project) {
                memberships.every { m -> m.email }
                memberships.every { m -> m.role }
            }
        and: 'checking only migrated memberships (admin excluded)'
            with(migratedProjectInfo.project) {
                memberships.count { m -> m.userMigrationRef } == memberships.size() - 1
                memberships.count { m -> m.userName } == memberships.size() - 1
            }
    }

}

