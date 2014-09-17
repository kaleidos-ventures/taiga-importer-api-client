package net.kaleidos.taiga.testdata

import net.kaleidos.domain.Project
import net.kaleidos.taiga.TaigaClient

trait TaigaTestData implements ProjectData, IssueData, UserStoryData,
    AttachmentData, MembershipData, HistoryData, UserData, WikipageData,
    WikilinkData, TaskData, MilestoneData {

    TaigaClient taigaClient

    TaigaClient createAuthenticatedTaigaClient() {
        def config = new ConfigSlurper().parse(new File('src/test/resources/taiga.groovy').text)
        def client = new TaigaClient(config.host)

        return client.authenticate(config.user, config.passwd)
    }

    Project createProject() {
        taigaClient.createProject(buildProject())
    }

    void deleteProject(Project project) {
        taigaClient.deleteProject(project)
    }
}