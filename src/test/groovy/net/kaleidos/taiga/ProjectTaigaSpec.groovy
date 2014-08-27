package net.kaleidos.taiga

class ProjectTaigaSpec extends TaigaSpecBase {

    void 'get the list of projects'() {
        when: 'we get the list of projects'
            def projects = taigaClient.getProjects()

        then: 'there are projects'
            notThrown Exception
            projects.size() > 0
    }

    void 'get a project by id'() {
        given: 'an existing project'
            def project = taigaClient.createProject("name ${new Date().time}", "description")

        when: 'tying to get the project by id'
            def sameProject = taigaClient.getProjectById(project.id)

        then: 'the projects are the same'
            project.id == sameProject.id
    }

    void 'save a project'() {
        when: 'saving a project'
            def project = taigaClient.createProject(name, description)

        then: 'the project is saved'
            project != null
            project.name == name
            project.description == description
            project.issueStatuses.size() == 7
            project.issueStatuses*.name == ['New', 'In progress', 'Ready for test', 'Closed', 'Needs Info', 'Rejected', 'Postponed']
            project.issueTypes.size() == 3
            project.issueTypes*.name == ['Bug', 'Question', 'Enhancement']
            project.issuePriorities.size() == 3
            project.issuePriorities*.name == ['Low', 'Normal', 'High']
            project.roles.size() == 6
            project.roles*.name == ['UX', 'Design', 'Front', 'Back', 'Product Owner', 'Stakeholder']

        cleanup:
            taigaClient.deleteProject(project)

        where:
            name = "My project ${new Date().time}"
            description = 'The description of the project'
    }
}