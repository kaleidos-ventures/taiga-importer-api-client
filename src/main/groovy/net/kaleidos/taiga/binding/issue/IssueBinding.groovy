package net.kaleidos.taiga.binding.issue

import net.kaleidos.domain.issue.Issue
import net.kaleidos.domain.issue.IssuePriority
import net.kaleidos.domain.issue.IssueStatus
import net.kaleidos.domain.issue.IssueType
import net.kaleidos.domain.project.Project
import wslite.json.JSONObject

class IssueBinding {

    static Issue bind(Issue issue, JSONObject json) {
        IssueType type
        IssueStatus status
        IssuePriority priority

        String subject
        String description
        Project project


//        project.id = json.id
//        project.defaultUsStatus = json.default_us_status
//        project.defaultTaskStatus = json.default_task_status
//        project.defaultPriority = json.default_priority
//        project.defaultSeverity = json.default_severity
//        project.defaultIssueStatus = json.default_issue_status
//        project.defaultIssueType = json.default_issue_type
//        project.issueStatuses = IssueStatusBinding.create(json.issue_statuses)
//        project.issueTypes = IssueTypeBinding.create(json.issue_types)
//        project.issuePriorities = IssuePriorityBinding.create(json.priorities)

        issue
    }
}