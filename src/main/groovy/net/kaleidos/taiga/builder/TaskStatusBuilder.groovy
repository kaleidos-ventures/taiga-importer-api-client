package net.kaleidos.taiga.builder

import net.kaleidos.domain.Project
import net.kaleidos.domain.TaskStatus

class TaskStatusBuilder implements TaigaEntityBuilder<TaskStatus> {

    TaskStatus build(Map json, Project project) {
        def taskStatus = new TaskStatus()

        taskStatus.with {
            name = json.name
            isClosed = json.is_closed
        }

        taskStatus
    }
}