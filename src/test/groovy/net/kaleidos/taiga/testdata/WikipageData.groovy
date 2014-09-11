package net.kaleidos.taiga.testdata

import net.kaleidos.domain.Project
import net.kaleidos.domain.Wikipage

trait WikipageData {

    Wikipage buildBasicWikipage(Project project) {
        new Wikipage()
            .setProject(project)
            .setSlug('home')
            .setContent('Lorem ipsum...')
    }

}