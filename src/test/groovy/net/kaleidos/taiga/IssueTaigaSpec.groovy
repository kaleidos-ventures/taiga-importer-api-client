package net.kaleidos.taiga

import net.kaleidos.domain.Project
import spock.lang.Unroll

class IssueTaigaSpec extends TaigaSpecBase {

    Project project

    def setup() {
        project = createProject()
    }

    def cleanup() {
        deleteProject(project)
    }

    @Unroll
    void 'create an issue with the basic fields and ref = #ref'() {
        given: 'a new issue'
            def issue = buildBasicIssue(project)
                .setRef(ref)

        when: 'creating a new issue'
            issue = taigaClient.createIssue(issue)

        then: 'the issue is created in Taiga'
            issue != null
            issue.ref == (ref ?: 1)
            issue.project.id == project.id
            issue.subject == subject
            issue.description == description
            issue.type == type
            issue.status == status
            issue.priority == priority
            issue.severity == severity

        where:
            ref << [123, null]
            subject = 'The subject'
            description = 'The description'
            type = 'Bug'
            status = 'New'
            priority = 'Normal'
            severity = 'Normal'
    }

    void 'create an issue with two attachments'() {
        given: 'two files to attach to an issue'
            def attachment0 = buildBasicAttachment(filename0, owner)
            def attachment1 = buildBasicAttachment(filename1, owner)

        and: 'a new issue'
            def issue = buildBasicIssue(project)
                .setAttachments([attachment0, attachment1])

        when: 'creating a new issue'
            issue = taigaClient.createIssue(issue)

        then: 'the issue is created in Taiga with the attachments'
            issue != null
            issue.attachments.size() == 2

        and: 'the attachments are correct'
            issue.attachments[0].name == attachment0.name
            issue.attachments[0].data == attachment0.data
            issue.attachments[1].name == attachment1.name
            issue.attachments[1].data == attachment1.data

        where:
            filename0 = 'tux.png'
            filename1 = 'debian.jpg'
            owner = 'admin@admin.com'
    }

    void 'create an attachment with optional data'() {
        given: 'one file to attach to an issue'
            def attachment = buildAttachmentWithOptionalData(description, createdDate)

        and: 'a new issue'
            def issue = buildBasicIssue(project)
                .setAttachments([attachment])

        when: 'creating a new issue'
            issue = taigaClient.createIssue(issue)

        then:
            issue.attachments[0].description == description
            issue.attachments[0].createdDate == createdDate

        where:
            createdDate = Date.parse("dd/MM/yyyy", '01/01/2010')
            description = 'description'
    }

    void 'create an issue with optional fields'() {
        given: 'a new issue with optional fields'
            def issue = buildBasicIssue(project)
                .setCreatedDate(createdDate)
                .setFinishedDate(finishedDate)
                .setOwner(owner)

        when: 'creating a new issue'
            issue = taigaClient.createIssue(issue)

        then: 'the issue is created in Taiga with the optional fields'
            issue != null
            issue.owner == owner
            issue.createdDate == createdDate
            issue.finishedDate == finishedDate

        where:
            createdDate = Date.parse("dd/MM/yyyy", '01/01/2010')
            finishedDate = Date.parse("dd/MM/yyyy", '07/01/2010')
            owner = 'admin@admin.com'
    }

    void 'create an issue with history'() {
        given: 'a new issue with history'
            def user = buildBasicUser()
            def history = buildBasicHistory(user, createdAt, comment)

        and: 'the issue to create'
            def issue = buildBasicIssue(project)
                .setHistory([history])

        when: 'creating a new issue'
            issue = taigaClient.createIssue(issue)

        then: 'the issue is created in Taiga'
            issue != null
            issue.history.size() == 1
            issue.history[0].user.email == user.email
            issue.history[0].user.name == user.name
            issue.history[0].createdAt == createdAt
            issue.history[0].comment == comment

        where:
            createdAt = Date.parse("dd/MM/yyyy HH:mm", '01/01/2010 13:45')
            comment = 'The comment'
    }

    void 'create an issue with tags'() {
        given: 'a new issue with tags'
            def issue = buildBasicIssue(project)
                .setTags(tags)

        when: 'creating a new issue'
            issue = taigaClient.createIssue(issue)

        then: 'the issue is created in Taiga with the tags'
            issue != null
            issue.tags == tags

        where:
            tags = ['foo', 'bar']
    }

    void 'create an issue with assigned user'() {
        given: 'a new issue with assigned user'
            def issue = buildBasicIssue(project)
                .setAssignedTo(assignedUser)

        when: 'creating the issue'
            issue = taigaClient.createIssue(issue)

        then: 'the issue is assigned to the user'
            issue.assignedTo == assignedUser

        where:
            assignedUser = 'admin@admin.com'
    }
}