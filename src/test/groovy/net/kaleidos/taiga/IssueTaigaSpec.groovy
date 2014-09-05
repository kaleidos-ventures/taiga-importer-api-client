package net.kaleidos.taiga

import net.kaleidos.domain.Attachment
import net.kaleidos.domain.History
import net.kaleidos.domain.Project
import net.kaleidos.domain.User
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
            def file1Base64 = new File("src/test/resources/${filename1}").bytes.encodeBase64().toString()
            def attachment1 = new Attachment(name: filename1, data: file1Base64, owner: owner)

            def file2Base64 = new File("src/test/resources/${filename2}").bytes.encodeBase64().toString()
            def attachment2 = new Attachment(name: filename2, data: file2Base64, owner: owner)

        and: 'a new issue'
            def issue = buildBasicIssue(project)
                .setAttachments([attachment1, attachment2])

        when: 'creating a new issue'
            issue = taigaClient.createIssue(issue)

        then: 'the issue is created in Taiga with the attachments'
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

    void 'create an attachment with optional data'() {
        given: 'one file to attach to an issue'
            def fileBase64 = new File("src/test/resources/tux.png").bytes.encodeBase64().toString()
            def attachment = new Attachment(name: 'tux.png', data: fileBase64, owner: 'admin@admin.com')
                .setDescription(description)
                .setCreatedDate(Date.parse("dd/MM/yyyy", createdDate))

        and: 'a new issue'
            def issue = buildBasicIssue(project)
                .setAttachments([attachment])

        when: 'creating a new issue'
            issue = taigaClient.createIssue(issue)

        then:
            issue.attachments[0].description == description
            issue.attachments[0].createdDate.format("dd/MM/yyyy") == createdDate

        where:
            createdDate = '01/01/2010'
            description = 'description'
    }

    void 'create an issue with optional fields'() {
        given: 'a new issue with optional fields'
            def issue = buildBasicIssue(project)
                .setCreatedDate(Date.parse("dd/MM/yyyy", createdDate))
                .setOwner(owner)

        when: 'creating a new issue'
            issue = taigaClient.createIssue(issue)

        then: 'the issue is created in Taiga with the optional fields'
            issue != null
            issue.owner == owner
            issue.createdDate.format("dd/MM/yyyy") == createdDate

        where:
            createdDate = '01/01/2010'
            owner = 'admin@admin.com'
    }

    void 'create an issue with comments'() {
        given: 'a new issue with comments'
            def user = new User().setEmail('admin@admin.com').setName('The fullname')
            def history = new History()
                .setUser(user)
                .setCreatedAt(Date.parse("dd/MM/yyyy HH:mm", createdAt))
                .setComment(comment)

            def issue = buildBasicIssue(project)
                .setHistory([history])

        when: 'creating a new issue'
            issue = taigaClient.createIssue(issue)

        then: 'the issue is created in Taiga'
            issue != null
            issue.history.size() == 1
            issue.history[0].user.email == user.email
            issue.history[0].user.name == user.name
            issue.history[0].createdAt.format('dd/MM/yyyy HH:mm') == createdAt

        where:
            createdAt = '01/01/2010 13:45'
            comment = 'The comment'
    }

}