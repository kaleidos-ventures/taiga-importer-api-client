package net.kaleidos.taiga.builder

import net.kaleidos.domain.Project

class ProjectBuilder implements SafeJson {

    Project build(Map json) {
        Project project = new Project()

        project.with {
            id = json.id
            name = json.name
            description = json.description
            defaultUsStatus = nullSafe(json.default_us_status)
            defaultTaskStatus = nullSafe(json.default_task_status)
            defaultPriority = nullSafe(json.default_priority)
            defaultSeverity = nullSafe(json.default_severity)
            defaultIssueStatus = nullSafe(json.default_issue_status)
            defaultIssueType = nullSafe(json.default_issue_type)
            issueStatuses = json.issue_statuses.collect { new IssueStatusBuilder().build(it) }
            issueTypes = json.issue_types.collect { new IssueTypeBuilder().build(it) }
            issuePriorities = json.priorities.collect { new IssuePriorityBuilder().build(it) }
            issueSeverities = json.severities.collect { new IssueSeverityBuilder().build(it) }
            roles = json.roles.collect { new RoleBuilder().build(it) }
            //memberships = json.memberships.collect { new MembershipBuilder().build(it, project) }
        }

        project
    }
}