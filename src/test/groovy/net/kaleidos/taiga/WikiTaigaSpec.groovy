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

    void 'create a wiki page'() {
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

    void 'create a wiki link'() {
        when: 'saving a wiki link'
            def wikilink = taigaClient.createWikiLink(title, href, project)

        then: 'the wiki link is created'
            wikilink != null
            wikilink.title == title
            wikilink.href == href
            wikilink.project.id == project.id

        where:
            title = 'Link title'
            href = 'link-title'
    }
}