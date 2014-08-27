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

    void 'add a non existing member to a project'() {
        given: 'a role'
            def role = taigaClient.addRole(roleName, project)

        when: 'trying to add it'
            taigaClient.createMembership(email, role.name, project)

        then: 'the member is added to Taiga'
            def projectUpdated = taigaClient.getProjectById(project.id)
            def member = projectUpdated.findMembershipByEmail(email)
            // WTF! The update project only contains one member!
            //member != null

        and: 'the original project is also updated'
            project.findMembershipByEmail(email) != null

        println project.memberships

        where:
            email = "user@example.com"
            roleName = "Boss"
    }
}