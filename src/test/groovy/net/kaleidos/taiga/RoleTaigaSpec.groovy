package net.kaleidos.taiga

import net.kaleidos.domain.Project

class RoleTaigaSpec extends TaigaSpecBase {

    Project project

    def setup() {
        project = createProject()
    }

    def cleanup() {
        deleteProject(project)
    }

    void 'add a role to a project'() {
        when: 'adding a new role to a project'
            taigaClient.addRole(role, project)

        then: 'the role is added to Taiga'
            def projectUpdated = taigaClient.getProjectById(project.id)
            projectUpdated.findRoleByName(role) != null

        and: 'the original project is also updated'
            project.findRoleByName(role) != null

        where:
            role = 'Boss'
    }

    void 'delete all roles of a project'() {
        when: 'delete all roles'
            taigaClient.deleteAllRoles(project)

        then: 'the roles have been deleted in Taiga'
            def projectUpdated = taigaClient.getProjectById(project.id)
            projectUpdated.roles.isEmpty()

        and: 'the original project is also updated'
            project.roles.isEmpty()
    }
}