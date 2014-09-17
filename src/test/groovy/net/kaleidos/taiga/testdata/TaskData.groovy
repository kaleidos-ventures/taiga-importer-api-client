package net.kaleidos.taiga.testdata
import net.kaleidos.domain.Project
import net.kaleidos.domain.Task

trait TaskData {

    Task buildBasicTask(Project project) {
        new Task()
            .setStatus('New')
            .setSubject('The subject')
            .setDescription('The description')
            .setProject(project)
    }
}