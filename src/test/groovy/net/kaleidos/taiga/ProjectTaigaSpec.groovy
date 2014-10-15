package net.kaleidos.taiga

class ProjectTaigaSpec extends TaigaSpecBase {

    void 'get a project'() {
        given: 'an existing project'
            def project = taigaClient.createProject(buildBasicProject())
            assert taigaClient.getProjects().size() > 0

        when: 'tying to get the project'
            def sameProject = taigaClient.getProjectById(project.id)

        then: 'the projects are the same'
            project.id == sameProject.id

        cleanup:
            taigaClient.deleteProject(project)
    }

    void 'save a simple project'() {
        given: 'a project to save'
            def project = buildProject(name, description)

        when: 'saving the project'
            project = taigaClient.createProject(project)

        then: 'the project is saved'
            project != null
            project.id != null
            project.name == name
            project.slug != null
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
            def project = buildBasicProject()
                .setCreatedDate(createdDate)

        when: 'saving the project'
            project = taigaClient.createProject(project)

        then: 'the project is saved with the old date'
            project.createdDate != null
            project.createdDate == createdDate

        cleanup:
            taigaClient.deleteProject(project)

        where:
            createdDate = Date.parse("dd/MM/yyyy", "01/01/2010")
    }

    void 'save a project with issue types, statuses, priorities and severities'() {
        given: 'a project to create'
            def project = buildBasicProject()
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
            project.issueStatuses*.name.sort() == statuses*.name.sort()
            project.issueStatuses*.isClosed.sort() == statuses*.isClosed.sort()
            project.issuePriorities.size() == priorities.size()
            project.issuePriorities.sort() == priorities.sort()
            project.issueSeverities.size() == severities.size()
            project.issueSeverities.sort() == severities.sort()

        cleanup:
            taigaClient.deleteProject(project)

        where:
            types = ['Bug', 'Question', 'Enhancement']
            statuses = buildIssueStatuses()
            priorities = ['Low', 'Normal', 'High']
            severities = ['Minor', 'Normal', 'Important', 'Critical']
    }

    void 'save a project with roles'() {
        given: 'a project to create'
            def project = buildBasicProject()
                .setRoles(roles)

        when: 'saving the project'
            project = taigaClient.createProject(project)

        then: 'the project is saved with all the fields'
            project.roles.size() == roles.size()
            project.roles.sort() == roles.sort()

        cleanup:
            taigaClient.deleteProject(project)

        where:
            roles = ['UX', 'Design', 'Front', 'Back']
    }

    void 'save a project with memberships'() {
        given: 'some memberships to add to a project'
            def m1 = buildBasicMembership('user1@example.com', 'UX')
            def m2 = buildBasicMembership('user2@example.com', 'Back')
            def memberships = [m1, m2]

        and: 'a project to create'
            def project = buildBasicProject()
                .setMemberships(memberships)
                .setRoles(roles)

        when: 'saving the project'
            project = taigaClient.createProject(project)

        then: 'the project is saved with the members and the creator of the proyect (owner)'
            project.memberships.size() == memberships.size() + 1
            project.memberships*.email.contains(memberships[0].email)
            project.memberships*.email.contains(memberships[1].email)

        cleanup:
            taigaClient.deleteProject(project)

        where:
            roles = ['UX', 'Back']
    }

    void 'save a project with user stories statuses'() {
        given: 'a project to create'
            def project = buildBasicProject()
                .setUserStoryStatuses(usStatuses)

        when: 'saving the project'
            project = taigaClient.createProject(project)

        then: 'the project is saved'
            project != null
            project.userStoryStatuses.size() == usStatuses.size()
            project.userStoryStatuses*.name.sort() == usStatuses*.name.sort()
            project.userStoryStatuses*.isClosed.sort() == usStatuses*.isClosed.sort()

        cleanup:
            taigaClient.deleteProject(project)

        where:
            usStatuses = buildUserStoryStatuses()
    }

    void 'save a project with estimation points'() {
        given: 'a project to create'
            def project = buildBasicProject()
                .setPoints(points)

        when: 'saving the project'
            project = taigaClient.createProject(project)

        then: 'the project is saved'
            project != null
            project.points.size() == points.size()
            project.points*.name.sort() == points*.name.sort()
            project.points*.value.sort() == points*.value.sort()

        cleanup:
            taigaClient.deleteProject(project)

        where:
            points = buildEstimationPoints()
    }

    void 'save a project with task statuses'() {
        given: 'a project to create'
            def project = buildBasicProject()
                .setTaskStatuses(taskStatuses)

        when: 'saving the project'
            project = taigaClient.createProject(project)

        then: 'the project is saved'
            project != null
            project.taskStatuses.size() == taskStatuses.size()
            project.taskStatuses*.name.sort() == taskStatuses*.name.sort()
            project.taskStatuses*.isClosed.sort() == taskStatuses*.isClosed.sort()

        cleanup:
            taigaClient.deleteProject(project)

        where:
            taskStatuses = buildTaskStatuses()
    }

    void 'get the list of projects'() {
        given: 'an existing project'
            def project = taigaClient.createProject(buildBasicProject())

        when: 'getting the list of projects'
            def projects = taigaClient.getProjects()

        then:
            projects != null
            projects.size() > 0
            projects.first().hasProperty('id')

        cleanup:
            taigaClient.deleteProject(project)
    }
}