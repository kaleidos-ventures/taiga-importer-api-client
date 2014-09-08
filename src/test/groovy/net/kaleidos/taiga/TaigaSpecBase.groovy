package net.kaleidos.taiga

import net.kaleidos.domain.Issue
import net.kaleidos.domain.IssueStatus
import net.kaleidos.domain.Membership
import net.kaleidos.domain.Project
import spock.lang.Specification

class TaigaSpecBase extends Specification {

    TaigaClient taigaClient

    def setup() {
        taigaClient = createAuthenticatedTaigaClient()
    }

    TaigaClient createAuthenticatedTaigaClient() {
        def config = new ConfigSlurper().parse(new File('src/test/resources/taiga.groovy').text)
        def client = new TaigaClient(config.host)

        return client.authenticate(config.user, config.passwd)
    }

    Project createProject() {
        def project = new Project()
            .setName("name ${new Date().time}")
            .setDescription("description")
            .setIssueTypes(['Bug', 'Question', 'Enhancement'])
            .setIssueStatuses(buildIssueStatuses())
            .setIssuePriorities(['Low', 'Normal', 'High'])
            .setIssueSeverities(['Minor', 'Normal', 'Important', 'Critical'])
            .setRoles(['UX', 'Back'])
            .setMemberships([new Membership().setEmail('admin@admin.com').setRole('Back')])

        taigaClient.createProject(project)
    }

    void deleteProject(Project project) {
        taigaClient.deleteProject(project)
    }

    Issue buildBasicIssue(Project project) {
        new Issue()
            .setType('Bug')
            .setStatus('New')
            .setPriority('Normal')
            .setSeverity('Normal')
            .setSubject('The subject')
            .setDescription('The description')
            .setProject(project)
    }

    List<IssueStatus> buildIssueStatuses() {
        [
            [name: 'New', isClosed: false],
            [name: 'In progress', isClosed: false],
            [name: 'Ready for test', isClosed: true],
            [name: 'Closed', isClosed: true],
            [name: 'Needs Info', isClosed: false],
            [name: 'Rejected', isClosed: false]
        ].collect {
            buildIssueStatus(it.name, it.isClosed)
        }
    }

    IssueStatus buildIssueStatus(String name, Boolean isClosed) {
        new IssueStatus().setName(name).setIsClosed(isClosed)
    }
}