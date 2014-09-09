package net.kaleidos.taiga

import net.kaleidos.domain.Project
import net.kaleidos.domain.Wikipage

class WikiTaigaSpec extends TaigaSpecBase {

    Project project

    def setup() {
        project = createProject()
    }

    def cleanup() {
        deleteProject(project)
    }

    void 'create a wiki page'() {
        given: 'a wiki page to save'
            def wikipage = new Wikipage()
                .setProject(project)
                .setSlug(slug)
                .setContent(content)
                .setOwner(owner)
                .setCreatedDate(Date.parse("dd/MM/yyyy", createdDate))

        when: 'saving a wiki page'
            wikipage = taigaClient.createWiki(wikipage)

        then: 'the wiki page is created'
            wikipage != null
            wikipage.slug == slug
            wikipage.content == content
            wikipage.project.id == project.id
            wikipage.owner == owner
            wikipage.createdDate.format("dd/MM/yyyy") == createdDate

        where:
            slug = 'home'
            content = 'Lorem ipsum...'
            createdDate = '01/01/2010'
            owner = 'admin@admin.com'
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