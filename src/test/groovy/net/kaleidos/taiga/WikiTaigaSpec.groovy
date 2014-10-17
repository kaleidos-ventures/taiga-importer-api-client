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
                .setCreatedDate(createdDate)

        when: 'saving a wiki page'
            wikipage = taigaClient.createWiki(wikipage)

        then: 'the wiki page is created'
            wikipage != null
            wikipage.slug == slug
            wikipage.content == content
            wikipage.project.id == project.id
            wikipage.owner == owner
            wikipage.createdDate == createdDate

        where:
            slug = 'home'
            content = 'Lorem ipsum...'
            createdDate = Date.parse("dd/MM/yyyy", '01/01/2010')
            owner = 'admin@admin.com'
    }

    void 'create a wiki page and let Taiga convert the name to slug'() {
        given: 'a wiki page to save'
            def wikipage = new Wikipage()
                .setProject(project)
                .setSlug(slug)
                .setContent(content)

        when: 'saving a wiki page'
            wikipage = taigaClient.createWiki(wikipage)

        then: 'the wiki page is created'
            wikipage != null
            wikipage.slug == 'this-is-the-name-of-the-page'

        where:
            slug = 'This is the name of the page'
            content = 'Lorem ipsum...'
    }

    void 'create a wiki page with attachments'() {
        given: 'two files to attach to a wiki page'
            def attachment0 = buildBasicAttachment(filename0, owner)
            def attachment1 = buildBasicAttachment(filename1, owner)

        and: 'a wiki page to save'
            def wikipage = buildBasicWikipage(project)
                .setAttachments([attachment0, attachment1])

        when: 'saving a wiki page'
            wikipage = taigaClient.createWiki(wikipage)

        then: 'the wiki page is created with the attachments'
            wikipage != null
            wikipage.attachments.size() == 2

        and: 'the attachments are correct'
            wikipage.attachments[0].name == attachment0.name
            wikipage.attachments[0].data == attachment0.data
            wikipage.attachments[0].owner == attachment0.owner
            wikipage.attachments[1].name == attachment1.name
            wikipage.attachments[1].data == attachment1.data
            wikipage.attachments[1].owner == attachment1.owner

        where:
            filename0 = 'tux.png'
            filename1 = 'debian.jpg'
            owner = 'admin@admin.com'
    }

    void 'create a wiki link'() {
        given: 'a wiki link to save'
            def wikilink = buildBasicWikilink(project)
                .setTitle(title)
                .setHref(href)

        when: 'saving the wiki link'
            wikilink = taigaClient.createWikiLink(wikilink)

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