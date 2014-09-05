package net.kaleidos.taiga.builder

import net.kaleidos.domain.Issue
import net.kaleidos.domain.Project

class IssueBuilder {

    Issue build(Map json, Project project) {
        def issue = new Issue()

        issue.with {
            ref = json.ref
            type = json.type
            status = json.status
            priority = json.priority
            severity = json.severity
            subject = json.subject
            description = json.description
            delegate.project = project
            attachments = json.attachments.collect { new AttachmentBuilder().build(it, project) }
        }

        issue
    }
}