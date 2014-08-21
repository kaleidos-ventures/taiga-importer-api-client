package net.kaleidos.taiga.builder

import net.kaleidos.domain.project.Project
import net.kaleidos.taiga.binding.issue.IssuePriorityBinding
import net.kaleidos.taiga.binding.issue.IssueStatusBinding
import net.kaleidos.taiga.binding.issue.IssueTypeBinding

class ProjectBuilder implements TaigaEntityBuilder<Project> {

    @Override
    Project build(Map json) {
        Project project = new Project()

        project.id = json.id
        project.defaultUsStatus = json.default_us_status
        project.defaultTaskStatus = json.default_task_status
        project.defaultPriority = json.default_priority
        project.defaultSeverity = json.default_severity
        project.defaultIssueStatus = json.default_issue_status
        project.defaultIssueType = json.default_issue_type
        project.issueStatuses = IssueStatusBinding.create(json.issue_statuses)
        project.issueTypes = IssueTypeBinding.create(json.issue_types)
        project.issuePriorities = IssuePriorityBinding.create(json.priorities)

        project
    }
}