package net.kaleidos.taiga.binding.issue
import net.kaleidos.domain.issue.IssueStatus

class IssueStatusBinding {

    static List<IssueStatus> create(obj) {
        obj.collect {
            new IssueStatus(id: it.id, name: it.name)
        }
    }
}