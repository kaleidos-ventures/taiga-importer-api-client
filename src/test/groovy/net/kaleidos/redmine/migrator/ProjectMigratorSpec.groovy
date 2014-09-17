package net.kaleidos.redmine.migrator

import spock.lang.Specification

import net.kaleidos.redmine.RedmineTaigaRef
import net.kaleidos.redmine.MigratorToTaigaSpecBase

import net.kaleidos.taiga.TaigaClient
import net.kaleidos.redmine.RedmineClient

import net.kaleidos.domain.Project as TaigaProject
import net.kaleidos.domain.Membership as TaigaMembership
import net.kaleidos.domain.IssueStatus as TaigaIssueStatus

import com.taskadapter.redmineapi.bean.User as RedmineUser
import com.taskadapter.redmineapi.bean.Role as RedmineRole
import com.taskadapter.redmineapi.bean.Tracker as RedmineTracker
import com.taskadapter.redmineapi.bean.Project as RedmineProject
import com.taskadapter.redmineapi.bean.Membership as RedmineMembership
import com.taskadapter.redmineapi.bean.IssueStatus as RedmineIssueStatus
import com.taskadapter.redmineapi.bean.IssuePriority as RedmineIssuePriority

class ProjectMigratorSpec extends MigratorToTaigaSpecBase {

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

    RedmineProject buildRedmineProject(String projectName = "prueba"){
        return new RedmineProject(
            name: projectName,
            identifier: projectName.toLowerCase(),
            description: "Project $projectName description"
        )
    }

    List<RedmineMembership> buildRedmineMembershipList() {
        return [
            buildRedmineMembershipWithRole("admin"),
            buildRedmineMembershipWithRole("user"),
            buildRedmineMembershipWithRole("guest")
        ]
    }

    RedmineMembership buildRedmineMembership() {
        return new RedmineMembership(
            project: new RedmineProject(),
            user: buildRedmineUser(),
            roles: buildRedmineRoleList()
        )
    }

    RedmineMembership buildRedmineMembershipWithRole(String role) {
        return new RedmineMembership(
            project: new RedmineProject(),
            user: buildRedmineUser(),
            roles: [new RedmineRole(name: role)]
        )
    }

    RedmineUser buildRedmineUser(String seed = "") {
        return new RedmineUser(
            id: 1,
            mail: "larry${seed}@larry.com")
    }

    Long getRandomTime() {
        return System.currentTimeMillis()
    }

    List<RedmineRole> buildRedmineRoleList() {
        return (1..5).collect { new RedmineRole(name: "role-$it") }
    }

    List<RedmineTracker> buildRedmineTrackerList() {
        return (1..5).collect { new RedmineTracker(name: "tracker-$it") }
    }

    List<RedmineIssueStatus> buildRedmineStatusList() {
        return (1..5).collect { new RedmineIssueStatus(id: it, name: "status-$it")}
    }

    List<RedmineIssuePriority> buildRedmineIssuePriorityList() {
        return (1..5).collect { new RedmineIssuePriority(name: "priority-$it") }
    }

}

