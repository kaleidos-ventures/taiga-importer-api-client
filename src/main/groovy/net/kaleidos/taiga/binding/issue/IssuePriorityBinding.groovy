package net.kaleidos.taiga.binding.issue

import net.kaleidos.domain.issue.IssuePriority
import wslite.json.JSONArray

class IssuePriorityBinding {

    static List<IssuePriority> create(JSONArray obj) {
        obj.collect {
            new IssuePriority(id: it.id, name: it.name)
        }
    }
}