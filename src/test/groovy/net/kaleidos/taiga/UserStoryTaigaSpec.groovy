package net.kaleidos.taiga

import net.kaleidos.domain.Project
import net.kaleidos.domain.UserStory

class UserStoryTaigaSpec extends TaigaSpecBase {

    Project project

    def setup() {
        project = createProject()
    }

    def cleanup() {
        deleteProject(project)
    }

    void 'create a user story with ref = #ref'() {
        given: 'a user story'
            def userStory = new UserStory()
                .setRef(ref)
                .setStatus('New')
                .setSubject('The subject')
                .setDescription('The description')
                .setProject(project)

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
}