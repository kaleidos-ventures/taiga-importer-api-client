package net.kaleidos.taiga.builder

import net.kaleidos.domain.Project
import net.kaleidos.domain.UserStoryStatus

class UserStoryStatusBuilder implements TaigaEntityBuilder<UserStoryStatus> {

    UserStoryStatus build(Map json, Project project) {
        def userStoryStatus = new UserStoryStatus()

        userStoryStatus.with {
            name = json.name
            isClosed = json.is_closed
        }

        userStoryStatus
    }
}