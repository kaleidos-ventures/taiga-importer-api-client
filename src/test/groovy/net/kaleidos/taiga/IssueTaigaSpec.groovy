package net.kaleidos.taiga

import net.kaleidos.domain.Issue
import net.kaleidos.domain.Project

class IssueTaigaSpec extends TaigaSpecBase {

    Project project

    def setup() {
        project = createProject()
    }

    def cleanup() {
        deleteProject(project)
    }

    void 'create an issue'() {
        given: 'a new issue'
            def issue = new Issue()
                            .setType(type)
                            .setStatus(status)
                            .setPriority(priority)
                            .setSeverity(severity)
                            .setSubject(subject)
                            .setDescription(description)
                            .setProject(project)

        when: 'creating a new issue'
            issue = taigaClient.createIssue(issue)

        then: 'the issue is created in Taiga'
            issue != null
            issue.project.id == project.id
            issue.subject == subject
            issue.description == description
            issue.type == type
            issue.status == status
            issue.priority == priority
            issue.severity == severity

        where:
            subject = 'The subject'
            description = 'The description'
            type = 'Bug'
            status = 'New'
            priority = 'Normal'
            severity = 'Normal'
    }
}