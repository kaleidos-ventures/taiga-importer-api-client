package net.kaleidos.taiga.builder

import net.kaleidos.domain.Issue
import net.kaleidos.domain.Project
import net.kaleidos.taiga.common.DateConversions

class IssueBuilder implements DateConversions {

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
            owner = json.owner
            createdDate = parse(json.created_date)
            attachments = json.attachments.collect { new AttachmentBuilder().build(it, project) }
            history = json.history.collect { new HistoryBuilder().build(it, project) }
        }

        issue
    }
}