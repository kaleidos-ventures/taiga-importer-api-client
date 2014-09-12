package net.kaleidos.taiga.builder

import net.kaleidos.domain.Project
import net.kaleidos.domain.UserStory
import net.kaleidos.taiga.common.DateConversions
import net.kaleidos.taiga.common.SafeJson

class RolePointBuilder implements TaigaEntityBuilder<UserStory.RolePoint>, DateConversions, SafeJson {

    UserStory.RolePoint build(Map json, Project project) {
        def rolePoint = new UserStory.RolePoint()

        rolePoint.with {
            role = json.role
            points = nullSafe(json.points)
        }

        rolePoint
    }
}