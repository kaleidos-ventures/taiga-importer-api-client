package net.kaleidos.taiga.mapper

import net.kaleidos.domain.UserStory
import net.kaleidos.taiga.common.DateConversions

class RolePointMapper implements Mapper<UserStory.RolePoint>, DateConversions {

    @Override
    Map map(UserStory.RolePoint rolePoint) {
        [
            role  : rolePoint.role,
            points: rolePoint.points
        ]
    }
}