package net.kaleidos.redmine

import net.kaleidos.taiga.TaigaClient
import net.kaleidos.redmine.convert.Converters
import net.kaleidos.domain.project.Project
import com.taskadapter.redmineapi.RedmineManager

class RedmineMigrator {

    final RedmineManager redmineClient
    final TaigaClient taigaClient

    RedmineMigrator(final RedmineManager redmineClient, final TaigaClient taigaClient) {
        this.redmineClient = redmineClient
        this.taigaClient = taigaClient
    }

    List<Project> listAllProject() {
        return redmineClient.projects.collect(Converters.project())
    }


}
