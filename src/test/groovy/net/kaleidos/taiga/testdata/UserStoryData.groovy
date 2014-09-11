package net.kaleidos.taiga.testdata

import net.kaleidos.domain.Project
import net.kaleidos.domain.UserStory

trait UserStoryData {

    UserStory buildBasicUserStory(Project project) {
        new UserStory()
            .setStatus('New')
            .setSubject('The subject')
            .setDescription('The description')
            .setProject(project)
    }
}