package net.kaleidos.taiga

import net.kaleidos.domain.EstimationPoint
import net.kaleidos.domain.Issue
import net.kaleidos.domain.IssueStatus
import net.kaleidos.domain.Membership
import net.kaleidos.domain.Project
import net.kaleidos.domain.UserStory
import net.kaleidos.domain.UserStoryStatus
import spock.lang.Specification

class TaigaSpecBase extends Specification {

    TaigaClient taigaClient

    private static final List STATUSES = [
        [name: 'New', isClosed: false],
        [name: 'In progress', isClosed: false],
        [name: 'Ready for test', isClosed: true],
        [name: 'Closed', isClosed: true],
        [name: 'Needs Info', isClosed: false],
        [name: 'Rejected', isClosed: false]
    ]

    private static final List ESTIMATION_POINTS = [
        [name: '?', value: null],
        [name: '0', value: 0],
        [name: '1/2', value: 0.5],
        [name: '1', value: 1],
        [name: '2', value: 2],
        [name: '3', value: 3],
        [name: '5', value: 5],
        [name: '8', value: 8],
        [name: '13', value: 13]
    ]

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
            .setPoints(buildEstimationPoints())
            .setUserStoryStatuses(buildUserStoryStatuses())

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

    List<UserStoryStatus> buildUserStoryStatuses() {
        STATUSES.collect {
            new UserStoryStatus().setName(it.name).setIsClosed(it.isClosed)
        }
    }

    List<IssueStatus> buildIssueStatuses() {
        STATUSES.collect {
            new IssueStatus().setName(it.name).setIsClosed(it.isClosed)
        }
    }

    List<EstimationPoint> buildEstimationPoints() {
        ESTIMATION_POINTS.collect {
            new EstimationPoint().setName(it.name).setValue(it.value)
        }
    }

    UserStory buildBasicUserStory(Project project) {
        new UserStory()
            .setStatus('New')
            .setSubject('The subject')
            .setDescription('The description')
            .setProject(project)
    }
}