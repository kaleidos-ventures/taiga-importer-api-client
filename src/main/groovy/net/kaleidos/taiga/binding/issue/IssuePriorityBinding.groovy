package net.kaleidos.taiga.binding.issue

import net.kaleidos.domain.issue.IssuePriority
import wslite.json.JSONArray
import wslite.json.JSONObject

class IssuePriorityBinding {

    static List<IssuePriority> create(JSONArray obj) {
        obj.collect {
            new IssuePriority(id: it.id, name: it.name)
        }
    }

    static IssuePriority bind(IssuePriority issuePriority, JSONObject json) {
        issuePriority.id = json.id
        issuePriority.name = json.name

        issuePriority
    }
}