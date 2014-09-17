package net.kaleidos.redmine

import net.kaleidos.domain.Project as TaigaProject

class RedmineTaigaRef {

    final String redmineIdentifier
    final TaigaProject project

    public RedmineTaigaRef(final String redmineIdentifier, final TaigaProject project) {
        this.redmineIdentifier = redmineIdentifier
        this.project = project
    }

}
