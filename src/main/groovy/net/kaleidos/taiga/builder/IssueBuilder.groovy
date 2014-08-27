package net.kaleidos.taiga.builder

import net.kaleidos.domain.Issue
import net.kaleidos.domain.Project

class IssueBuilder {

    Issue build(Map json, Project project) {
        def issue = new Issue()

        issue.with {
            id = json.id
            type = project.findIssueTypeById(json.type)
            status = project.findIssueStatusById(json.status)
            priority = project.findIssuePriorityById(json.priority)
            subject = json.subject
            description = json.description
            delegate.project = project
        }

        issue
    }
}