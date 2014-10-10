package net.kaleidos.taiga.builder

import net.kaleidos.domain.Project
import net.kaleidos.taiga.common.DateConversions

class ProjectBuilder implements TaigaEntityBuilder<Project>, DateConversions {

    Project build(Map json, Project p) {
        Project project = new Project()

        project.with {
            id = json.id
            name = json.name
            slug = json.slug
            createdDate = parse(json.created_date)
            description = json.description
            issueStatuses = json.issue_statuses.collect { new IssueStatusBuilder().build(it, null) }
            issueTypes = json.issue_types.collect { it.name }
            issuePriorities = json.priorities.collect { it.name }
            issueSeverities = json.severities.collect { it.name }
            roles = json.roles.collect { it.name }
            memberships = json.memberships.collect { new MembershipBuilder().build(it, null) }
            userStoryStatuses = json.us_statuses.collect { new UserStoryStatusBuilder().build(it, null) }
            points = json.points.collect { new EstimationPointBuilder().build(it, null) }
            taskStatuses = json.task_statuses.collect { new TaskStatusBuilder().build(it, null) }
        }

        project
    }
}