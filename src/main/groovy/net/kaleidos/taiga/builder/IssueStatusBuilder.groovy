package net.kaleidos.taiga.builder

import net.kaleidos.domain.issue.IssueStatus

class IssueStatusBuilder implements TaigaEntityBuilder<IssueStatus> {

    @Override
    IssueStatus build(Map json) {
        def issueStatus = new IssueStatus()

        issueStatus.with {
            id = json.id
            name = json.name
        }

        issueStatus
    }
}
