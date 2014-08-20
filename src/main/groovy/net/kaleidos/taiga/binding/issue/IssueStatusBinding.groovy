package net.kaleidos.taiga.binding.issue

import net.kaleidos.domain.issue.IssueStatus
import wslite.json.JSONObject

class IssueStatusBinding {

    static List<IssueStatus> create(obj) {
        obj.collect {
            new IssueStatus(id: it.id, name: it.name)
        }
    }

    static IssueStatus bind(IssueStatus issueStatus, JSONObject json) {
        issueStatus.id = json.id
        issueStatus.name = json.name

        issueStatus
    }
}