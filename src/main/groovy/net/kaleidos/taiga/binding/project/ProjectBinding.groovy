package net.kaleidos.taiga.binding.project

import net.kaleidos.domain.project.Project
import net.kaleidos.taiga.binding.issue.IssuePriorityBinding
import net.kaleidos.taiga.binding.issue.IssueStatusBinding
import net.kaleidos.taiga.binding.issue.IssueTypeBinding
import wslite.rest.Response

class ProjectBinding {

    static Project bind(Project project, Response response) {
        project.defaultUsStatus = response.json.default_us_status
        project.defaultTaskStatus = response.json.default_task_status
        project.defaultPriority = response.json.default_priority
        project.defaultSeverity = response.json.default_severity
        project.defaultIssueStatus = response.json.default_issue_status
        project.defaultIssueType = response.json.default_issue_type
        project.issueStatuses = IssueStatusBinding.create(response.json.issue_statuses)
        project.issueTypes = IssueTypeBinding.create(response.json.issue_types)
        project.issuePriorities = IssuePriorityBinding.create(response.json.priorities)

        project
    }
}