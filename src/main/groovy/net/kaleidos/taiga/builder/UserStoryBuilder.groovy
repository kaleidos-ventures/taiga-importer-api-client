package net.kaleidos.taiga.builder

import net.kaleidos.domain.Project
import net.kaleidos.domain.UserStory
import net.kaleidos.taiga.common.DateConversions

class UserStoryBuilder implements TaigaEntityBuilder<UserStory>, DateConversions {

    UserStory build(Map json, Project project) {
        def userStory = new UserStory()

        userStory.with {
            ref = json.ref
            status = json.status
            subject = json.subject
            description = json.description
            delegate.project = project
            owner = json.owner
            createdDate = parse(json.created_date)
            attachments = json.attachments.collect { new AttachmentBuilder().build(it, null) }
            history = json.history.collect { new HistoryBuilder().build(it, null) }
            rolePoints = json.role_points.collect { new RolePointBuilder().build(it, null) }
        }

        userStory
    }
}