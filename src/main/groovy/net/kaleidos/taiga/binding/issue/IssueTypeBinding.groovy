package net.kaleidos.taiga.binding.issue

import net.kaleidos.domain.issue.IssueType
import wslite.json.JSONArray

class IssueTypeBinding {

    static List<IssueType> create(JSONArray obj) {
        obj.collect {
            new IssueType(id: it.id, name: it.name)
        }
    }
}