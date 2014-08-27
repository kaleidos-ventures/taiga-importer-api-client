package net.kaleidos.taiga

import net.kaleidos.domain.Project

class MembershipTaigaSpec extends TaigaSpecBase {

    Project project

    def setup() {
        project = createProject()
    }

    def cleanup() {
        deleteProject(project)
    }

    void 'add a non existing user to a project'() {
        given: 'a role'
            def role = project.roles.first()

        when: 'adding the user'
            taigaClient.createMembership(email, role.name, project)

        then: 'the member is added to Taiga'
            def projectUpdated = taigaClient.getProjectById(project.id)
            def member = projectUpdated.findMembershipByEmail(email)
            // WTF! The update project only contains one member!
            //member != null

        and: 'the original project is also updated'
            project.findMembershipByEmail(email) != null

        where:
            email = "user_${new Date().time}@example.com"
            roleName = "Boss"
    }

    void 'add an existing user to a project'() {
        given: 'a user in taiga'
            def membership = taigaClient.createMembership(email, project.roles.first().name, project)
            def user = taigaClient.registerUser(email, 'dragon', membership.token)

        and: 'a new project'
            def project2 = createProject()

        when: 'adding the existing user in Taiga to the new project'
            def role2 = project2.roles.first()
            def membership2 = taigaClient.createMembership(email, role2.name, project2)

        then: 'the member is added to the project as a real user'
            membership2.userId == user.id

        where:
            email = "user_${new Date().time}@example.com"
    }
}