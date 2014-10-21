package net.kaleidos.taiga.builder

import groovy.json.JsonSlurper
import net.kaleidos.domain.Issue
import net.kaleidos.domain.Project
import net.kaleidos.taiga.common.DateConversions
import net.kaleidos.taiga.common.SafeJson

class IssueBuilder implements TaigaEntityBuilder<Issue>, DateConversions, SafeJson {

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
            finishedDate = parse(nullSafe(json.finished_date))
            attachments = json.attachments.collect { new AttachmentBuilder().build(it, null) }
            history = json.history.collect { new HistoryBuilder().build(it, null) }
            tags = new JsonSlurper().parseText(json.tags)
            assignedTo = json.assigned_to
        }

        issue
    }
}