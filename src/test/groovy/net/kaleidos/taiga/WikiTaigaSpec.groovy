package net.kaleidos.taiga

import net.kaleidos.domain.Attachment
import net.kaleidos.domain.Project
import net.kaleidos.domain.Wikilink
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

    void 'create a wiki page with attachments'() {
        given: 'two files to attach to a wiki page'
            def file1Base64 = new File("src/test/resources/${filename1}").bytes.encodeBase64().toString()
            def attachment1 = new Attachment(name: filename1, data: file1Base64, owner: owner)

            def file2Base64 = new File("src/test/resources/${filename2}").bytes.encodeBase64().toString()
            def attachment2 = new Attachment(name: filename2, data: file2Base64, owner: owner)

        and: 'a wiki page to save'
            def wikipage = new Wikipage()
                .setProject(project)
                .setSlug('home')
                .setContent('Lorem ipsum...')
                .setAttachments([attachment1, attachment2])

        when: 'saving a wiki page'
            wikipage = taigaClient.createWiki(wikipage)

        then: 'the wiki page is created with the attachments'
            wikipage != null
            wikipage.attachments.size() == 2
            wikipage.attachments[0].name == filename1
            wikipage.attachments[0].data == file1Base64
            wikipage.attachments[0].owner == owner
            wikipage.attachments[1].name == filename2
            wikipage.attachments[1].data == file2Base64
            wikipage.attachments[1].owner == owner

        where:
            filename1 = 'tux.png'
            filename2 = 'debian.jpg'
            owner = 'admin@admin.com'
    }

    void 'create a wiki link'() {
        given: 'a wiki link to save'
            def wikilink = new Wikilink()
                .setProject(project)
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