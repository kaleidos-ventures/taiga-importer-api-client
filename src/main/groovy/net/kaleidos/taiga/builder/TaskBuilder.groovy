package net.kaleidos.taiga.builder

import groovy.json.JsonSlurper
import net.kaleidos.domain.Milestone
import net.kaleidos.domain.Project
import net.kaleidos.domain.Task
import net.kaleidos.domain.UserStory
import net.kaleidos.taiga.common.DateConversions
import net.kaleidos.taiga.common.SafeJson

class TaskBuilder implements TaigaEntityBuilder<Task>, DateConversions, SafeJson {

    Task build(Map json, Project project) {
        def task = new Task()

        task.with {
            ref = json.ref
            status = json.status
            subject = json.subject
            description = json.description
            delegate.project = project
            owner = json.owner
            createdDate = parse(json.created_date)
            finishedDate = parse(nullSafe(json.finished_date))
            userStory = new UserStory(ref: nullSafe(json.user_story))
            milestone = new Milestone(name: json.milestone)
            attachments = json.attachments.collect { new AttachmentBuilder().build(it, null) }
            history = json.history.collect { new HistoryBuilder().build(it, null) }
            tags = new JsonSlurper().parseText(json.tags)
            assignedTo = json.assigned_to
        }

        task
    }
}