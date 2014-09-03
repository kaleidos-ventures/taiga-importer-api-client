package net.kaleidos.taiga

import net.kaleidos.domain.Project

class ProjectTaigaSpec extends TaigaSpecBase {

    void 'get the list of projects'() {
        when: 'we get the list of projects'
            def projects = taigaClient.getProjects()

        then: 'there are projects'
            notThrown Exception
            projects.size() > 0
    }

    void 'get a project'() {
        given: 'an existing project'
            def project = new Project().setName("name ${new Date().time}").setDescription("description")
            project = taigaClient.createProject(project)

        when: 'tying to get the project'
            def sameProject = taigaClient.getProjectById(project.id)

        then: 'the projects are the same'
            project.id == sameProject.id
    }

    void 'save a simple project'() {
        given: 'a project to save'
            def project = new Project()
                .setName(name)
                .setDescription(description)

        when: 'saving the project'
            project = taigaClient.createProject(project)

        then: 'the project is saved'
            project != null
            project.id != null
            project.name == name
            project.description == description
            project.issueStatuses.size() == 0
            project.issueTypes.size() == 0
            project.issuePriorities.size() == 0
            project.issueSeverities.size() == 0
            project.roles.size() == 0

        cleanup:
            taigaClient.deleteProject(project)

        where:
            name = "My project ${new Date().time}"
            description = 'The description of the project'
    }

    void 'save a project with and old start date'() {
        given: 'a project to create'
            def project = new Project()
                .setName(name)
                .setDescription(description)
                .setCreatedDate(Date.parse("dd/MM/yyyy", createdDate))

        when: 'saving the project'
            project = taigaClient.createProject(project)

        then: 'the project is saved with the old date'
            project.createdDate != null
            project.createdDate.format("dd/MM/yyyy") == createdDate

        cleanup:
            taigaClient.deleteProject(project)

        where:
            name = "My project ${new Date().time}"
            description = 'The description of the project'
            createdDate = "01/01/2010"
    }

    void 'save a project with issue types, statuses, priorities and severities'() {
        given: 'a project to create'
            def project = new Project()
                .setName(name)
                .setDescription(description)
                .setIssueTypes(types)
                .setIssueStatuses(statuses)
                .setIssuePriorities(priorities)
                .setIssueSeverities(severities)

        when: 'saving the project'
            project = taigaClient.createProject(project)

        then: 'the project is saved with all the fields'
            project.issueTypes.size() == types.size()
            project.issueTypes.sort() == types.sort()
            project.issueStatuses.size() == statuses.size()
            project.issueStatuses.sort() == statuses.sort()
            project.issuePriorities.size() == priorities.size()
            project.issuePriorities.sort() == priorities.sort()
            project.issueSeverities.size() == severities.size()
            project.issueSeverities.sort() == severities.sort()

        cleanup:
            taigaClient.deleteProject(project)

        where:
            name = "My project ${new Date().time}"
            description = 'The description of the project'
            createdDate = "01/01/2010"
            types = ['Bug', 'Question', 'Enhancement']
            statuses = ['New', 'In progress', 'Ready for test', 'Closed', 'Needs Info', 'Rejected', 'Postponed']
            priorities = ['Low', 'Normal', 'High']
            severities = ['Minor', 'Normal', 'Important', 'Critical']
    }
}