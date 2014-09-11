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
}