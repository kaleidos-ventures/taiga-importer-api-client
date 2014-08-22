package net.kaleidos.taiga.builder

import net.kaleidos.domain.Issue

class IssueBuilder implements TaigaEntityBuilder<Issue> {

    @Override
    Issue build(Map json) {
        def issue = new Issue()

        issue.with {
            id = json.id
            type = null
            status = null
            priority = null
            subject = json.subject
            description = json.description
            project = null
        }

        issue
    }
}
