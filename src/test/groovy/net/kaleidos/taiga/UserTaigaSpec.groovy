package net.kaleidos.taiga

import net.kaleidos.domain.Project

class UserTaigaSpec extends TaigaSpecBase {

    Project project

    def setup() {
        project = createProject()
    }

    def cleanup() {
        deleteProject(project)
    }

    void 'accept an invitation to a project and become a real user'() {
        given: 'an invitation'
            def role = project.roles.first()
            def membership = taigaClient.createMembership(email, role.name, project)

        when: 'the user accepts it'
            def user = taigaClient.registerUser(email, 'dragon', membership.token)

        then:
            user != null
            user.email == email

        where:
            email = "user_${new Date().time}@example.com"
    }
}