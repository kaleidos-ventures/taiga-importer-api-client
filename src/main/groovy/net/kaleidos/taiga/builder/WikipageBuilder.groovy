package net.kaleidos.taiga.builder

import net.kaleidos.domain.Project
import net.kaleidos.domain.Wikipage

class WikipageBuilder {

    Wikipage build(Map json, Project project) {
        def wikipage = new Wikipage()

        wikipage.with {
            delegate.project = project
            slug = json.slug
            content = json.content
        }

        wikipage
    }
}