package net.kaleidos.redmine.testdata

import com.taskadapter.redmineapi.bean.IssuePriority as RedmineIssuePriority
import com.taskadapter.redmineapi.bean.IssueStatus as RedmineIssueStatus
import com.taskadapter.redmineapi.bean.Membership as RedmineMembership
import com.taskadapter.redmineapi.bean.Project as RedmineProject
import com.taskadapter.redmineapi.bean.Role as RedmineRole
import com.taskadapter.redmineapi.bean.Tracker as RedmineTracker
import com.taskadapter.redmineapi.bean.User as RedmineUser

class ProjectDataProvider {

    RedmineProject buildRedmineProject(String projectName = "prueba") {
        return new RedmineProject(
            name: projectName,
            id: 123,
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
        return (1..5).collect { new RedmineIssueStatus(id: it, name: "status-$it") }
    }

    List<RedmineIssuePriority> buildRedmineIssuePriorityList() {
        return (1..5).collect { new RedmineIssuePriority(name: "priority-$it") }
    }

}
