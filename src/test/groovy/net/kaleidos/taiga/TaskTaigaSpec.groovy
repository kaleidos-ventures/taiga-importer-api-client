package net.kaleidos.taiga

import net.kaleidos.domain.Project
import spock.lang.Unroll

class TaskTaigaSpec extends TaigaSpecBase {

    Project project

    def setup() {
        project = createProject()
    }

    def cleanup() {
        deleteProject(project)
    }

    @Unroll
    void 'create a new task with ref = #ref'() {
        given: 'a task'
            def task = buildBasicTask(project)
                .setRef(ref)

        when: 'creating the task'
            task = taigaClient.createTask(task)

        then: 'the task is created in Taiga'
            task != null
            task.ref == (ref ?: 1)
            task.project.id == project.id
            task.subject == subject
            task.description == description
            task.status == status

        where:
            ref << [123, null]
            subject = 'The subject'
            description = 'The description'
            status = 'New'
    }

    void 'create a task related to a user story'() {
        given: 'a user story'
            def us = buildBasicUserStory(project)
            us = taigaClient.createUserStory(us)

        and: 'a task related to the user story'
            def task = buildBasicTask(project)
                .setUserStory(us)

        when: 'creating the task'
            task = taigaClient.createTask(task)

        then: 'the task is created and it is related to the user story'
            task != null
            task.userStory.ref == us.ref
    }

    void 'create a task related to a milestone'() {
        given: 'a milestone'
            def milestone = buildBasicMilestone(project)
            milestone = taigaClient.createMilestone(milestone)

        and: 'a task related to the milestone'
            def task = buildBasicTask(project)
                .setMilestone(milestone)

        when: 'creating the task'
            task = taigaClient.createTask(task)

        then: 'the task is created and it is related to the milestone'
            task != null
            task.milestone.name == milestone.name
    }

    void 'create a task with owner, created date and finished date'() {
        given: 'a new task'
            def task = buildBasicTask(project)
                .setCreatedDate(createdDate)
                .setFinishedDate(finishedDate)
                .setOwner(owner)

        when: 'creating the task'
            task = taigaClient.createTask(task)

        then: 'the task is created in Taiga with the optional fields'
            task != null
            task.owner == owner
            task.createdDate == createdDate
            task.finishedDate == finishedDate

        where:
            createdDate = Date.parse("dd/MM/yyyy", '01/01/2010')
            finishedDate = Date.parse("dd/MM/yyyy", '07/01/2010')
            owner = 'admin@admin.com'
    }

    void 'create a task with two attachments'() {
        given: 'two files to attach to a task'
            def attachment0 = buildBasicAttachment(filename0, owner)
            def attachment1 = buildBasicAttachment(filename1, owner)

        and: 'a new task'
            def task = buildBasicTask(project)
                .setAttachments([attachment0, attachment1])

        when: 'creating the task'
            task = taigaClient.createTask(task)

        then: 'the task is created in Taiga with the attachments'
            task != null
            task.attachments.size() == 2

        and: 'the attachments are correct'
            task.attachments[0].name == attachment0.name
            task.attachments[0].data == attachment0.data
            task.attachments[1].name == attachment1.name
            task.attachments[1].data == attachment1.data

        where:
            filename0 = 'tux.png'
            filename1 = 'debian.jpg'
            owner = 'admin@admin.com'
    }

    void 'create a task with an attachment with optional data'() {
        given: 'one file to attach to a task'
            def attachment = buildAttachmentWithOptionalData(description, createdDate)

        and: 'a new task'
            def task = buildBasicTask(project)
                .setAttachments([attachment])

        when: 'creating the task'
            task = taigaClient.createTask(task)

        then:
            task.attachments[0].description == description
            task.attachments[0].createdDate == createdDate

        where:
            createdDate = Date.parse("dd/MM/yyyy", '01/01/2010')
            description = 'description'
    }

    void 'create a task with history'() {
        given: 'a new task with history'
            def user = buildBasicUser()
            def history = buildBasicHistory(user, createdAt, comment)

        and: 'the task to create'
            def task = buildBasicTask(project)
                .setHistory([history])

        when: 'creating the task'
            task = taigaClient.createTask(task)

        then: 'the task is created in Taiga'
            task != null
            task.history.size() == 1
            task.history[0].user.email == user.email
            task.history[0].user.name == user.name
            task.history[0].createdAt == createdAt
            task.history[0].comment == comment

        where:
            createdAt = Date.parse("dd/MM/yyyy HH:mm", '01/01/2010 13:45')
            comment = 'The comment'
    }

    void 'create a task with tags'() {
        given: 'a new task'
            def task = buildBasicTask(project)
                .setTags(tags)

        when: 'creating the task'
            task = taigaClient.createTask(task)

        then: 'the task is created in Taiga with the tags'
            task != null
            task.tags == tags

        where:
            tags = ['foo', 'bar']
    }

    void 'create a task with assigned user'() {
        given: 'a task with assigned user'
            def task = buildBasicTask(project)
                .setAssignedTo(assignedUser)

        when: 'creating the task'
            task = taigaClient.createTask(task)

        then: 'the task is assigned to the user'
            task.assignedTo == assignedUser

        where:
            assignedUser = 'admin@admin.com'
    }
}