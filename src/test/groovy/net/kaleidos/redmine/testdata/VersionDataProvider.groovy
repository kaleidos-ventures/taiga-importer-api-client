package net.kaleidos.redmine.testdata

import com.taskadapter.redmineapi.bean.Version as RedmineVersion

class VersionDataProvider {

    List<RedmineVersion> buildRedmineVersionList() {
        (1..10).collect(this.&buildRedmineVersionsWithIndex)
    }

    RedmineVersion buildRedmineVersionsWithIndex(Integer idx) {
        new RedmineVersion(
            name: "Sprint ${idx}",
            status: idx == 10 ? 'open' : 'closed',
            createdOn: new Date() - idx,
            dueDate: new Date()
        )
    }
}