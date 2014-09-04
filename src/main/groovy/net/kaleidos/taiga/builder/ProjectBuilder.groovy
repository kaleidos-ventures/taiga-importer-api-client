package net.kaleidos.taiga.builder

import net.kaleidos.domain.Project

class ProjectBuilder implements SafeJson {

    private static final String TAIGA_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZ"

    Project build(Map json) {
        Project project = new Project()

        project.with {
            id = json.id
            name = json.name
            createdDate = Date.parse(TAIGA_DATE_FORMAT, json.created_date)
            description = json.description
            defaultUsStatus = nullSafe(json.default_us_status)
            defaultTaskStatus = nullSafe(json.default_task_status)
            defaultPriority = nullSafe(json.default_priority)
            defaultSeverity = nullSafe(json.default_severity)
            defaultIssueStatus = nullSafe(json.default_issue_status)
            defaultIssueType = nullSafe(json.default_issue_type)
            issueStatuses = json.issue_statuses.collect { it.name }
            issueTypes = json.issue_types.collect { it.name }
            issuePriorities = json.priorities.collect { it.name }
            issueSeverities = json.severities.collect { it.name }
            roles = json.roles.collect { new RoleBuilder().build(it) }
            //memberships = json.memberships.collect { new MembershipBuilder().build(it, project) }
        }

        project
    }
}