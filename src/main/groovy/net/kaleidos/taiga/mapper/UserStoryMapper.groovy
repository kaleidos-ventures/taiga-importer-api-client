package net.kaleidos.taiga.mapper

import net.kaleidos.domain.UserStory
import net.kaleidos.taiga.common.DateConversions

class UserStoryMapper implements Mapper<UserStory>, DateConversions {

    @Override
    Map map(UserStory userStory) {
        [
            status      : userStory.status,
            subject     : userStory.subject,
            description : userStory.description,
            project     : userStory.project.id,
            ref         : userStory.ref,
            owner       : userStory.owner,
            created_date: format(userStory.createdDate),
            finish_date : format(userStory.finishedDate),
            attachments : userStory.attachments.collect { Mappers.map(it) },
            history     : userStory.history.collect { Mappers.map(it) },
            role_points : userStory.rolePoints.collect { Mappers.map(it) },
            tags        : userStory.tags,
            assigned_to : userStory.assignedTo,
        ]
    }
}
