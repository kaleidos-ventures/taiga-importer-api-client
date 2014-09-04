package net.kaleidos.taiga.builder

import net.kaleidos.domain.Issue
import net.kaleidos.domain.Project

class IssueBuilder {

    Issue build(Map json, Project project) {
        def issue = new Issue()

        issue.with {
            id = json.id
            type = json.type
            status = json.status
            priority = json.priority
            severity = json.severity
            subject = json.subject
            description = json.description
            delegate.project = project
        }

        issue
    }
}