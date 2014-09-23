package net.kaleidos.redmine.migrator

import com.taskadapter.redmineapi.bean.WikiPage as RedmineWikiPage
import com.taskadapter.redmineapi.bean.WikiPageDetail as RedmineWikiPageDetail
import groovy.transform.InheritConstructors
import groovy.util.logging.Log4j
import net.kaleidos.domain.Wikipage as TaigaWikiPage
import net.kaleidos.redmine.RedmineTaigaRef

@Log4j
@InheritConstructors
class WikiMigrator extends AbstractMigrator<TaigaWikiPage> {

    List<TaigaWikiPage> migrateWikiPagesByProject(final RedmineTaigaRef ref) {
        log.debug("Migrating wiki pages from: '${ref.redmineIdentifier}'")

        return findAllWikiPagesFromProject(ref.redmineIdentifier)
            .collect(this.&getCompleteRedmineWikiPageFrom.rcurry(ref))
            .collect(this.&createTaigaWikiPage.rcurry(ref))
            .collect(this.&save)
    }

    List<RedmineWikiPage> findAllWikiPagesFromProject(String identifier) {
        return redmineClient.findAllWikiPageByProjectIdentifier(identifier)
    }

    TaigaWikiPage createTaigaWikiPage(
        final RedmineWikiPage redmineWikiPage,
        final RedmineTaigaRef redmineTaigaRef) {
        log.debug("Building taiga wiki page: '${redmineWikiPage.title}'")

        return new TaigaWikiPage(
            slug: slugify(redmineWikiPage.title),
            content: redmineWikiPage.text,
            project: redmineTaigaRef.project
        )
    }

    RedmineWikiPageDetail getCompleteRedmineWikiPageFrom(
        final RedmineWikiPage redmineWikiPage,
        final RedmineTaigaRef redmineTaigaRef) {
        log.debug("Getting complete redmine wiki page: '${redmineWikiPage.title}'")

        return redmineClient
            .findCompleteWikiPageByProjectIdentifierAndTitle(
            redmineTaigaRef.redmineIdentifier,
            redmineWikiPage.title
        )
    }

    @Override
    TaigaWikiPage save(TaigaWikiPage wikipage) {
        log.debug("Trying to save wiki page: ${wikipage.slug}")

        return taigaClient.createWiki(wikipage)
    }

    TaigaWikiPage setWikiHomePage(final List<TaigaWikiPage> alreadySavedWikiPages) {
        log.debug("Looking for wiki home")

        TaigaWikiPage home =
            alreadySavedWikiPages.find(wikiPageWithHomeName) ?:
                saveAlternativeWikiHome(alreadySavedWikiPages)

        return home
    }

    Closure<Boolean> wikiPageWithHomeName = { it.slug.toLowerCase() == 'home' }

    TaigaWikiPage saveAlternativeWikiHome(final List<TaigaWikiPage> alreadySavedWikiPages) {
        log.debug("No wiki home found in [${alreadySavedWikiPages?.size()}] pages...looking for an alternative")

        TaigaWikiPage alternative =
            alreadySavedWikiPages.find(byWikiTitle) ?:
                alreadySavedWikiPages.sort(byOldest).first()

        log.debug("Wiki alternative home found: ['${alternative.slug}']")

        return save(
            new TaigaWikiPage(
                slug: 'home',
                content: alternative.content,
                project: alternative.project,
                owner: alternative.owner,
                createdDate: alternative.createdDate,
                attachments: alternative.attachments
            )
        )
    }

    Closure<Boolean> byWikiTitle = filteringBySlugToLowerCase('wiki')
    Closure<Boolean> byOldest = inAscendingOrderBy('createdDate')

    Closure<Boolean> filteringBySlugToLowerCase(String title) {
        return { it.slug.toLowerCase() == title }
    }

    Closure<Boolean> inAscendingOrderBy(String field) {
        return { it."$field" }
    }

}