package net.kaleidos.taiga

class IssueTaigaSpec extends TaigaSpecBase {

    void 'delete all issue types of a project'() {
        given: 'a project'
            def project = taigaClient.createProject("name ${new Date().time}", "description")

        when: 'delete all issue types'
            taigaClient.deleteAllIssueTypes(project)

        then: 'the issues have been deleted in Taiga'
            def projectUpdated = taigaClient.getProjectById(project.id)
            projectUpdated.issueTypes.isEmpty()

        and: 'the original object is also updated'
            project.issueTypes.isEmpty()
    }

}