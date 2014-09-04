package net.kaleidos.taiga.mapper

import net.kaleidos.domain.Issue

class IssueMapper implements Mapper<Issue> {

    @Override
    Map map(Issue issue) {
        def map = [
            type       : issue.type,
            status     : issue.status,
            priority   : issue.priority,
            severity   : issue.severity,
            subject    : issue.subject,
            description: issue.description,
            project    : issue.project.id
        ]
    }
}