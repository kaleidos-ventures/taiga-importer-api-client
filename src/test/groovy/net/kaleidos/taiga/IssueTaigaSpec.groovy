package net.kaleidos.taiga

import net.kaleidos.domain.Attachment
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

    void 'create an issue with the basic fields'() {
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

    void 'create an issue with two attachments'() {
        given: 'two files to attach to an issue'
            def file1Base64 = new File("src/test/resources/${filename1}").bytes.encodeBase64().toString()
            def attachment1 = new Attachment(name: filename1, data: file1Base64, owner: owner)

            def file2Base64 = new File("src/test/resources/${filename2}").bytes.encodeBase64().toString()
            def attachment2 = new Attachment(name: filename2, data: file2Base64, owner: owner)

        and: 'a new issue'
            def issue = new Issue()
                .setType('Bug')
                .setStatus('New')
                .setPriority('Normal')
                .setSeverity('Normal')
                .setSubject('Subject')
                .setDescription('Description')
                .setProject(project)
                .setAttachments([attachment1, attachment2])

        when: 'creating a new issue'
            issue = taigaClient.createIssue(issue)

        then: 'the issue is created in Taiga'
            issue != null
            issue.attachments.size() == 2
            issue.attachments[0].name == filename1
            issue.attachments[0].data == file1Base64
            issue.attachments[1].name == filename2
            issue.attachments[1].data == file2Base64

        where:
            filename1 = 'tux.png'
            filename2 = 'debian.jpg'
            owner = 'admin@admin.com'
    }
}