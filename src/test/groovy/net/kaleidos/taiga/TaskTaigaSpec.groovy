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
}