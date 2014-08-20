package net.kaleidos.redmine.convert

import com.taskadapter.redmineapi.bean.Project as RedmineProject
import net.kaleidos.domain.project.Project as TaigaProject

class ProjectConverter implements Converter<RedmineProject, TaigaProject> {

    TaigaProject convert(final RedmineProject redmineProject) {
        // In order to avoid expression such like a.name = b.name
        Map<String,?> properties = [
            name: redmineProject.name,
            description: redmineProject.description,
        ]

        // Object is created and not modified again... in this method
        return new TaigaProject(properties)
    }

}
