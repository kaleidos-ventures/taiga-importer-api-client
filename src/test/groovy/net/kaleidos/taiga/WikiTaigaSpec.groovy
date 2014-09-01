package net.kaleidos.taiga

import net.kaleidos.domain.Project

class WikiTaigaSpec extends TaigaSpecBase {

    Project project

    def setup() {
        project = createProject()
    }

    def cleanup() {
        deleteProject(project)
    }

    void 'save a wiki page'() {
        when: 'saving a wiki page'
            def wikipage = taigaClient.createWiki(slug, content, project)

        then: 'the wiki page is created'
            wikipage != null
            wikipage.slug == slug
            wikipage.content == content
            wikipage.project.id == project.id

        where:
            slug = 'home'
            content = 'Lorem ipsum...'
    }
}