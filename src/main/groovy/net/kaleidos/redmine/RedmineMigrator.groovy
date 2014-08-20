package net.kaleidos.redmine

import net.kaleidos.taiga.TaigaClient
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

    List<TaigaProject> migrateAllProjectBasicStructure() {
        Closure<TaigaProject> basicFields = { RedmineProject rp ->
            return new TaigaProject(name: rp.name, description: rp.description)
        }

        Closure<TaigaProject> savedProjects = { TaigaProject tp ->
            return taigaClient.save(tp)
        }

        return redmineClient
            .projects
            .collect(basicFields >> savedProjects)
    }


}
