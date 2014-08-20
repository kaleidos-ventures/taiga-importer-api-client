package net.kaleidos.redmine.convert

import spock.lang.Specification

import com.taskadapter.redmineapi.bean.Project as RedmineProject
import net.kaleidos.domain.project.Project as TaigaProject
import net.kaleidos.redmine.convert.ProjectConverter

class ProjectConverterSpec extends Specification {

    void 'Convert from a redmine project to a taiga project'() {
        given: 'an instance of each type'
            RedmineProject redmineProject = createRedmineProject()
        when: 'converting previous instance to a taiga one'
            TaigaProject taigaProject = new ProjectConverter().convert(redmineProject)
        then: 'all properties should match the ones found in redmine'
            with(taigaProject) {
                name == redmineProject.name
                description == redmineProject.description
            }
    }

    RedmineProject createRedmineProject() {
        return new RedmineProject(
            name: 'migrated-project',
            description: 'this is a redmine project migrated to taiga'
        )
    }

}
