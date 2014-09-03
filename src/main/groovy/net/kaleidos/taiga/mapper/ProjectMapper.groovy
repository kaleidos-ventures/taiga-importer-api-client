package net.kaleidos.taiga.mapper

import net.kaleidos.domain.Project

class ProjectMapper implements Mapper<Project> {

    private static final String TAIGA_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZ"

    @Override
    Map map(Project project) {
        def map = [
            name        : project.name,
            description : project.description,
        ]

        if (project.createdDate) {
            map.created_date = project.createdDate?.format(TAIGA_DATE_FORMAT)
        }

        map.issue_types = project.issueTypes.collect { [name: it] }
        map.issue_statuses = project.issueStatuses.collect { [name: it] }
        map.priorities = project.issuePriorities.collect { [name: it] }
        map.severities = project.issueSeverities.collect { [name: it] }

        map
    }
}