package net.kaleidos.taiga.binding.issue

import net.kaleidos.domain.issue.IssueType
import wslite.json.JSONArray
import wslite.json.JSONObject

class IssueTypeBinding {

    static List<IssueType> create(JSONArray obj) {
        obj.collect {
            new IssueType(id: it.id, name: it.name)
        }
    }

    static IssueType bind(IssueType issueType, JSONObject json) {
        issueType.id = json.id
        issueType.name = json.name

        issueType
    }
}