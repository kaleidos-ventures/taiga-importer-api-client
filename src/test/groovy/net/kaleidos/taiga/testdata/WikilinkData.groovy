package net.kaleidos.taiga.testdata

import net.kaleidos.domain.Project
import net.kaleidos.domain.Wikilink

trait WikilinkData {

    Wikilink buildBasicWikilink(Project project) {
        new Wikilink().setProject(project)
    }
}
