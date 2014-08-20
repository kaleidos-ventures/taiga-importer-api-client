package net.kaleidos.redmine

import net.kaleidos.taiga.TaigaClient
import net.kaleidos.redmine.convert.Converters
import net.kaleidos.domain.project.Project
import com.taskadapter.redmineapi.RedmineManager

import com.taskadapter.redmineapi.bean.Project as RedmineProject
import net.kaleidos.domain.project.Project as TaigaProject

class RedmineMigrator {

    final RedmineManager redmineClient
    final TaigaClient taigaClient

    RedmineMigrator(final RedmineManager redmineClient, final TaigaClient taigaClient) {
        this.redmineClient = redmineClient
        this.taigaClient = taigaClient
    }

    List<TaigaProject> migrateAllProjects() {
        List<TaigaProject> migratedProjects =
            redmineClient
                .projects
                .collect { RedmineProject rp ->
                    def redmineProperties = [
                        name: rp.name,
                        description: rp.description
                    ]

                    return new TaigaProject(redmineProperties)
                }

        return migratedProjects
    }


}
