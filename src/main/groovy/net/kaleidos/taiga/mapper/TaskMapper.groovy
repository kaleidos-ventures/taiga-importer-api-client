package net.kaleidos.taiga.mapper

import net.kaleidos.domain.Task
import net.kaleidos.taiga.common.DateConversions

class TaskMapper implements Mapper<Task>, DateConversions {

    @Override
    Map map(Task task) {
        [
            status       : task.status,
            subject      : task.subject,
            description  : task.description,
            project      : task.project.id,
            ref          : task.ref,
            owner        : task.owner,
            created_date : format(task.createdDate),
            finished_date: format(task.finishedDate),
            user_story   : task.userStory?.ref,
            milestone    : task.milestone?.name,
            attachments  : task.attachments.collect { Mappers.map(it) },
            history      : task.history.collect { Mappers.map(it) },
            tags         : task.tags,
            assigned_to  : task.assignedTo,
        ]
    }
}