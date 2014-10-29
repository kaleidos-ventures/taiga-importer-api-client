package net.kaleidos.taiga

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

    void 'create a user story with owner, created date and finished date'() {
        given: 'a new user story'
            def userStory = buildBasicUserStory(project)
                .setCreatedDate(createdDate)
                .setFinishedDate(finishedDate)
                .setOwner(owner)

        when: 'creating the user story'
            userStory = taigaClient.createUserStory(userStory)

        then: 'the issue is created in Taiga with the optional fields'
            userStory != null
            userStory.owner == owner
            userStory.createdDate == createdDate
            userStory.finishedDate == finishedDate

        where:
            createdDate = Date.parse("dd/MM/yyyy", '01/01/2010')
            finishedDate = Date.parse("dd/MM/yyyy", '07/01/2010')
            owner = 'admin@admin.com'
    }

    void 'create a user story with two attachments'() {
        given: 'two files to attach to a user story'
            def attachment0 = buildBasicAttachment(filename0, owner)
            def attachment1 = buildBasicAttachment(filename1, owner)

        and: 'a new user story'
            def userStory = buildBasicUserStory(project)
                .setAttachments([attachment0, attachment1])

        when: 'creating the user story'
            userStory = taigaClient.createUserStory(userStory)

        then: 'the user story is created in Taiga with the attachments'
            userStory != null
            userStory.attachments.size() == 2

        and: 'the attachments are correct'
            userStory.attachments[0].name == attachment0.name
            userStory.attachments[0].data == attachment0.data
            userStory.attachments[0].owner == attachment0.owner
            userStory.attachments[1].name == attachment1.name
            userStory.attachments[1].data == attachment1.data
            userStory.attachments[1].owner == attachment1.owner

        where:
            filename0 = 'tux.png'
            filename1 = 'debian.jpg'
            owner = 'admin@admin.com'
    }

    void 'create an attachment with optional data'() {
        given: 'one file to attach to a user story'
            def attachment = buildAttachmentWithOptionalData(description, createdDate)

        and: 'a new user story'
            def userStory = buildBasicUserStory(project)
                .setAttachments([attachment])

        when: 'creating the user story'
            userStory = taigaClient.createUserStory(userStory)

        then: 'the user story is created in Taiga'
            userStory.attachments[0].description == description
            userStory.attachments[0].createdDate == createdDate

        where:
            createdDate = Date.parse("dd/MM/yyyy", '01/01/2010')
            description = 'description'
    }

    void 'create a user story with history'() {
        given: 'a new user story with history'
            def user = buildBasicUser()
            def history = buildBasicHistory(user, createdAt, comment)

        and: 'the user story to create'
            def userStory = buildBasicUserStory(project)
                .setHistory([history])

        when: 'creating the user story'
            userStory = taigaClient.createUserStory(userStory)

        then: 'the user story is created in Taiga'
            userStory != null
            userStory.history.size() == 1
            userStory.history[0].user.email == user.email
            userStory.history[0].user.name == user.name
            userStory.history[0].createdAt == createdAt

        where:
            createdAt = Date.parse("dd/MM/yyyy HH:mm", '01/01/2010 13:45')
            comment = 'The comment'
    }

    void 'create a user story with estimation points'() {
        given: 'a new user story with points'
            def points0 = buildRolePoint('UX', '1/2')
            def points1 = buildRolePoint('Front', '?')
            def points2 = buildRolePoint('Back', '5')

            def userStory = buildBasicUserStory(project)
                .setRolePoints([points0, points1, points2])

        when: 'creating the user story'
            userStory = taigaClient.createUserStory(userStory)

        then: 'the user story is created in Taiga'
            userStory != null
            userStory.rolePoints.size() == 3
    }

    void 'create a user story with tags'() {
        given: 'a new user story'
            def userStory = buildBasicUserStory(project)
                .setTags(tags)

        when: 'creating the user story'
            userStory = taigaClient.createUserStory(userStory)

        then: 'the issue is created in Taiga with the tags'
            userStory != null
            userStory.tags == tags

        where:
            tags = ['foo', 'bar']
    }

    void 'create a user story with assigned user'() {
        given: 'a new user story with assigned user'
            def userStory = buildBasicUserStory(project)
                .setAssignedTo(assignedUser)

        when: 'creating the user story'
            userStory = taigaClient.createUserStory(userStory)

        then: 'the user story is assigned to the user'
            userStory.assignedTo == assignedUser

        where:
            assignedUser = 'admin@admin.com'
    }
}