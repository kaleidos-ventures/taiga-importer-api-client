package net.kaleidos.taiga
import net.kaleidos.domain.Project

class IssueTaigaSpec extends TaigaSpecBase {

    Project project

    def setup() {
        project = createProject()
    }

    def cleanup() {
        deleteProject(project)
    }

    void 'delete all issue types of a project'() {
        when: 'delete all issue types'
            taigaClient.deleteAllIssueTypes(project)

        then: 'the types have been deleted in Taiga'
            def projectUpdated = taigaClient.getProjectById(project.id)
            projectUpdated.issueTypes.isEmpty()

        and: 'the original project is also updated'
            project.issueTypes.isEmpty()
    }

    void 'add a new issue type'() {
        when: 'adding a new type'
            taigaClient.addIssueType(type, project)

        then: 'the type is added to Taiga'
            def projectUpdated = taigaClient.getProjectById(project.id)
            projectUpdated.findIssueTypeByName(type) != null

        and: 'the original project is also updated'
            project.findIssueTypeByName(type) != null

        where:
            type = 'Task'
    }

    void 'delete all issue statuses of a project'() {
        when: 'delete all issue statuses'
            taigaClient.deleteAllIssueStatuses(project)

        then: 'the statuses have been deleted in Taiga'
            def projectUpdated = taigaClient.getProjectById(project.id)
            projectUpdated.issueStatuses.isEmpty()

        and: 'the original project is also updated'
            project.issueStatuses.isEmpty()
    }

    void 'add a new issue status'() {
        when: 'adding a new status'
            taigaClient.addIssueStatus(status, isClosed, project)

        then: 'the type is added to Taiga'
            def projectUpdated = taigaClient.getProjectById(project.id)
            projectUpdated.findIssueStatusByName(status) != null

        and: 'the original project is also updated'
            project.findIssueStatusByName(status) != null

        where:
            isClosed << [true, false]
            status = 'Pending'
    }

    void 'delete all issue priorities of a project'() {
        when: 'delete all issue statuses'
            taigaClient.deleteAllIssuePriorities(project)

        then: 'the priorities have been deleted in Taiga'
            def projectUpdated = taigaClient.getProjectById(project.id)
            projectUpdated.issuePriorities.isEmpty()

        and: 'the original project is also updated'
            project.issuePriorities.isEmpty()
    }

    void 'add a new issue priority'() {
        when: 'adding a new status'
            taigaClient.addIssuePriority(priority, project)

        then: 'the priority is added to Taiga'
            def projectUpdated = taigaClient.getProjectById(project.id)
            projectUpdated.findIssuePriorityByName(priority) != null

        and: 'the original project is also updated'
            project.findIssuePriorityByName(priority) != null

        where:
            priority = 'I want it now!'
    }

    void 'add a new issue severity'() {
        when: 'adding a new severity'
            taigaClient.addIssueSeverity(severity, project)

        then: 'the severity is added to Taiga'
            def projectUpdated = taigaClient.getProjectById(project.id)
            projectUpdated.findIssueSeverityByName(severity) != null

        and: 'the original project is also updated'
            project.findIssueSeverityByName(severity) != null

        where:
            severity = 'Critical'
    }

    void 'create an issue'() {
        when: 'creating a new issue'
            def issue = taigaClient.createIssue(project, type, status, priority, subject, description)

        then: 'the issue is created in Taiga'
            issue.project.id == project.id
            issue.subject == subject
            issue.description == description

        where:
            type = 'Bug'
            status = 'New'
            priority = 'Normal'
            subject = 'The subject'
            description = 'The description'
    }
}