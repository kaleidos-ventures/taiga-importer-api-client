package net.kaleidos.taiga.builder

import net.kaleidos.domain.Project

class ProjectBuilder implements SafeJson {

    private static final String TAIGA_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZ"

    Project build(Map json) {
        Project project = new Project()

        project.with {
            id = json.id
            name = json.name
            createdDate = Date.parse(TAIGA_DATE_FORMAT, json.created_date)
            description = json.description
            issueStatuses = json.issue_statuses.collect { it.name }
            issueTypes = json.issue_types.collect { it.name }
            issuePriorities = json.priorities.collect { it.name }
            issueSeverities = json.severities.collect { it.name }
            roles = json.roles.collect { it.name }
            //memberships = json.memberships.collect { new MembershipBuilder().build(it, project) }
        }

        project
    }
}