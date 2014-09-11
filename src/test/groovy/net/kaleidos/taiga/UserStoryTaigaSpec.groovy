package net.kaleidos.taiga

import net.kaleidos.domain.Attachment
import net.kaleidos.domain.Project
import spock.lang.Unroll

class UserStoryTaigaSpec extends TaigaSpecBase {

    Project project

    def setup() {
        project = createProject()
    }

    def cleanup() {
        deleteProject(project)
    }

    @Unroll
    void 'create a user story with ref = #ref'() {
        given: 'a user story'
            def userStory = buildBasicUserStory(project)
                .setRef(ref)

        when: 'creating the user story'
            userStory = taigaClient.createUserStory(userStory)

        then: 'the user story is created in Taiga'
            userStory != null
            userStory.ref == (ref ?: 1)
            userStory.project.id == project.id
            userStory.subject == subject
            userStory.description == description
            userStory.status == status

        where:
            ref << [123, null]
            subject = 'The subject'
            description = 'The description'
            status = 'New'
    }

    void 'create a user story with owner and created date'() {
        given: 'a new user story'
            def userStory = buildBasicUserStory(project)
                .setCreatedDate(Date.parse("dd/MM/yyyy", createdDate))
                .setOwner(owner)

        when: 'creating the user story'
            userStory = taigaClient.createUserStory(userStory)

        then: 'the issue is created in Taiga with the optional fields'
            userStory != null
            userStory.owner == owner
            userStory.createdDate.format("dd/MM/yyyy") == createdDate

        where:
            createdDate = '01/01/2010'
            owner = 'admin@admin.com'
    }

    void 'create a user story with two attachments'() {
        given: 'two files to attach to a user story'
            def file1Base64 = new File("src/test/resources/${filename1}").bytes.encodeBase64().toString()
            def attachment1 = new Attachment(name: filename1, data: file1Base64, owner: owner)

            def file2Base64 = new File("src/test/resources/${filename2}").bytes.encodeBase64().toString()
            def attachment2 = new Attachment(name: filename2, data: file2Base64, owner: owner)

        and: 'a new user story'
            def userStory = buildBasicUserStory(project)
                .setAttachments([attachment1, attachment2])

        when: 'creating the user story'
            userStory = taigaClient.createUserStory(userStory)

        then: 'the user story is created in Taiga with the attachments'
            userStory != null
            userStory.attachments.size() == 2
            userStory.attachments[0].name == filename1
            userStory.attachments[0].data == file1Base64
            userStory.attachments[1].name == filename2
            userStory.attachments[1].data == file2Base64

        where:
            filename1 = 'tux.png'
            filename2 = 'debian.jpg'
            owner = 'admin@admin.com'
    }

    void 'create an attachment with optional data'() {
        given: 'one file to attach to a user story'
            def fileBase64 = new File("src/test/resources/tux.png").bytes.encodeBase64().toString()
            def attachment = new Attachment(name: 'tux.png', data: fileBase64, owner: 'admin@admin.com')
                .setDescription(description)
                .setCreatedDate(Date.parse("dd/MM/yyyy", createdDate))

        and: 'a new user story'
            def userStory = buildBasicUserStory(project)
                .setAttachments([attachment])

        when: 'creating the user story'
            userStory = taigaClient.createUserStory(userStory)

        then: 'the user story is created in Taiga'
            userStory.attachments[0].description == description
            userStory.attachments[0].createdDate.format("dd/MM/yyyy") == createdDate

        where:
            createdDate = '01/01/2010'
            description = 'description'
    }
}