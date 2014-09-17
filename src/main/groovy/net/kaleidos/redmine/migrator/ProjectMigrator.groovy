package net.kaleidos.redmine.migrator

import groovy.transform.InheritConstructors

import net.kaleidos.redmine.RedmineTaigaRef

import net.kaleidos.domain.IssueStatus as TaigaIssueStatus
import net.kaleidos.domain.Project as TaigaProject
import net.kaleidos.domain.Membership as TaigaMembership

import com.taskadapter.redmineapi.bean.User as RedmineUser
import com.taskadapter.redmineapi.bean.Project as RedmineProject
import com.taskadapter.redmineapi.bean.Membership as RedmineMembership
import com.taskadapter.redmineapi.bean.IssueStatus as RedmineIssueStatus

@InheritConstructors
class ProjectMigrator extends AbstractMigrator<TaigaProject> {

    static final String SEVERITY_NORMAL = 'Normal'

    List<TaigaProject> migrateAllProjects() {
        return redmineClient.findAllProject().collect(this.&migrateProject)
    }

    RedmineTaigaRef migrateProject(final RedmineProject redmineProject) {
        return new RedmineTaigaRef(
            redmineProject.id,
            save(buildProjectFromRedmineProject(redmineProject))
        )
    }

    TaigaProject buildProjectFromRedmineProject(final RedmineProject redmineProject) {
        List<TaigaMembership> memberships =
            getMembershipsByProjectIdentifier(redmineProject.identifier)

        return new TaigaProject(
            name: "${redmineProject.name} - [${redmineProject.identifier}]" ,
            description: redmineProject.with { description ?: name },
            roles: memberships.role.unique(),
            memberships: memberships,
            issueTypes: issueTypes,
            issueStatuses: issueStatuses,
            issuePriorities: issuePriorities,
            issueSeverities: issueSeverities
        )
    }

    List<TaigaMembership> getMembershipsByProjectIdentifier(String identifier) {
        return redmineClient
                .findAllMembershipByProjectIdentifier(identifier)
                .collect(this.&transformToTaigaMembership)
    }

    TaigaMembership transformToTaigaMembership(final RedmineMembership redmineMembership) {
        RedmineUser user =
            redmineClient.findUserFullById(redmineMembership.user.id)

        return new TaigaMembership(
            userName: user.fullName,
            userMigrationRef: user.id.toString(),
            email: user.mail,
            role: redmineMembership.roles.name.first()
        )
    }

    List<String> getIssueTypes() {
        return redmineClient.findAllTracker().collect(extractName)
    }

    List<TaigaIssueStatus> getIssueStatuses() {
        return redmineClient.findAllIssueStatus().collect(this.&taigaIssueStatusFromRedmineIssueStatus)
    }

    TaigaIssueStatus taigaIssueStatusFromRedmineIssueStatus(final RedmineIssueStatus status) {
        return new TaigaIssueStatus(name: status.name, isClosed: status.isClosed())
    }

    List<String> getIssuePriorities() {
        return redmineClient.findAllIssuePriority().collect(extractName)
    }

    List<String> getIssueSeverities() {
        return [SEVERITY_NORMAL]
    }

    Closure<String> extractName = { it.name }

    @Override
    TaigaProject save(final TaigaProject taigaProject) {
        return taigaClient.createProject(taigaProject)
    }

}
