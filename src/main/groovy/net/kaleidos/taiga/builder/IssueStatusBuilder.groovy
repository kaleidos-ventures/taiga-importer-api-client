package net.kaleidos.taiga.builder
import net.kaleidos.domain.IssueStatus
import net.kaleidos.domain.Project

class IssueStatusBuilder implements TaigaEntityBuilder<IssueStatus> {

    IssueStatus build(Map json, Project project) {
        def issueStatus = new IssueStatus()

        issueStatus.with {
            name = json.name
            isClosed = json.is_closed
        }

        issueStatus
    }
}