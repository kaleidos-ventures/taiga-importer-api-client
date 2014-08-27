package net.kaleidos.taiga.builder

import net.kaleidos.domain.IssueStatus

class IssueStatusBuilder {

    IssueStatus build(Map json) {
        def issueStatus = new IssueStatus()

        issueStatus.with {
            id = json.id
            name = json.name
        }

        issueStatus
    }
}