package net.kaleidos.redmine

import net.kaleidos.taiga.TaigaClient
import com.taskadapter.redmineapi.RedmineManager

class RedmineMigrator {

    final RedmineManager redmineClient
    final TaigaClient taigaClient

    RedmineMigrator(final RedmineManager redmineClient, final TaigaClient taigaClient) {
        this.redmineClient = redmineClient
        this.taigaClient = taigaClient
    }

    List<String> listAllProjectNames() {
        return redmineClient.projects.name
    }


}
