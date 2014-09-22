package net.kaleidos.redmine

import net.kaleidos.domain.Project as TaigaProject

class RedmineTaigaRef {

    final Integer redmineId
    final String redmineIdentifier
    final TaigaProject project

    public RedmineTaigaRef(
        final Integer redmineId,
        final String redmineIdentifier,
        final TaigaProject project) {

        this.redmineId = redmineId
        this.redmineIdentifier = redmineIdentifier
        this.project = project

    }

}
