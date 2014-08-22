package net.kaleidos.taiga.builder

import net.kaleidos.domain.project.Project

class ProjectBuilder implements TaigaEntityBuilder<Project> {

    @Override
    Project build(Map json) {
        Project project = new Project()

        project.with {
            id = json.id
            defaultUsStatus = json.default_us_status
            defaultTaskStatus = json.default_task_status
            defaultPriority = json.default_priority
            defaultSeverity = json.default_severity
            defaultIssueStatus = json.default_issue_status
            defaultIssueType = json.default_issue_type
            issueStatuses = json.issue_statuses.collect { new IssueStatusBuilder().build(it) }
            issueTypes = json.issue_types.collect { new IssueTypeBuilder().build(it) }
            issuePriorities = json.priorities.collect { new IssuePriorityBuilder().build(it) }
        }

        project
    }
}