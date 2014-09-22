package net.kaleidos.taiga.mapper

import net.kaleidos.domain.Project
import net.kaleidos.taiga.common.DateConversions

class ProjectMapper implements Mapper<Project>, DateConversions {

    private static final List<String> defaultPermissions = [
        "add_issue",
        "modify_issue",
        "delete_issue",
        "view_issues",
        "add_milestone",
        "modify_milestone",
        "delete_milestone",
        "view_milestones",
        "view_project",
        "add_task",
        "modify_task",
        "delete_task",
        "view_tasks",
        "add_us",
        "modify_us",
        "delete_us",
        "view_us",
        "add_wiki_page",
        "modify_wiki_page",
        "delete_wiki_page",
        "view_wiki_pages",
        "add_wiki_link",
        "delete_wiki_link",
        "view_wiki_links",
    ]

    @Override
    Map map(Project project) {
        def map = [
            name       : project.name,
            description: project.description,
        ]

        if (project.createdDate) {
            map.created_date = format(project.createdDate)
        }

        map.issue_types = project.issueTypes.collect { [name: it] }
        map.issue_statuses = project.issueStatuses.collect { [name: it.name, is_closed: it.isClosed] }
        map.priorities = project.issuePriorities.collect { [name: it] }
        map.severities = project.issueSeverities.collect { [name: it] }
        map.roles = project.roles.collect { [name: it, permissions: defaultPermissions] }
        map.memberships = project.memberships.findAll{it}.collect { [email: it.email, role: it.role] }
        map.us_statuses = project.userStoryStatuses.collect { [name: it.name, is_closed: it.isClosed] }
        map.points = project.points.collect { [name: it.name, value: it.value] }
        map.task_statuses = project.taskStatuses.collect { [name: it.name, is_closed: it.isClosed] }

        map
    }
}
