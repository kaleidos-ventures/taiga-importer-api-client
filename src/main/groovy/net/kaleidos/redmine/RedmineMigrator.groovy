package net.kaleidos.redmine

import static groovyx.gpars.GParsPool.withPool
import com.taskadapter.redmineapi.RedmineManager
import com.taskadapter.redmineapi.bean.Issue as RedmineIssue
import com.taskadapter.redmineapi.bean.IssueStatus as RedmineIssueStatus
import com.taskadapter.redmineapi.bean.Project as RedmineProject
import com.taskadapter.redmineapi.bean.Attachment as RedmineAttachment
import com.taskadapter.redmineapi.bean.Tracker
import com.taskadapter.redmineapi.bean.Journal
import com.taskadapter.redmineapi.bean.User as RedmineUser
import com.taskadapter.redmineapi.bean.Membership as RedmineMembership
import com.taskadapter.redmineapi.bean.WikiPage as RedmineWikiPageSummary
import com.taskadapter.redmineapi.bean.WikiPageDetail as RedmineWikiPage

import net.kaleidos.domain.User as TaigaUser
import net.kaleidos.domain.Issue as TaigaIssue
import net.kaleidos.domain.Attachment as TaigaAttachment
import net.kaleidos.domain.Wikipage
import net.kaleidos.domain.History
import net.kaleidos.domain.IssueStatus
import net.kaleidos.domain.Project as TaigaProject
import net.kaleidos.taiga.TaigaClient

import groovy.util.logging.Log4j
import org.apache.http.message.BasicNameValuePair
import com.github.slugify.Slugify

@Log4j
class RedmineMigrator {

    final RedmineManager redmineClient
    final TaigaClient taigaClient
    final Object NOTHING = null
    final String SEVERITY_NORMAL = 'Normal'

    RedmineMigrator(final RedmineManager redmineClient, final TaigaClient taigaClient) {
        this.redmineClient = redmineClient
        this.taigaClient = taigaClient
    }

    List<RedmineTaigaRef> migrateAllProjects() {
        return migrateProjectList(redmineClient.projects)
    }

    List<RedmineTaigaRef> migrateProjectList(List<RedmineProject> projects) {
        return map(
            projects,
            addBasicFields >>
            addIdentifierJustInCase(projects.name) >>
            addIssueRelationShips(projectRelationships) >>
            saveProject
        )
    }

    Map getProjectRelationships() {
        return [
            issueTypes: issueTypes,
            issueStatuses: issueStatuses,
            issuePriorities: issuePriorities,
            issueSeverities: [SEVERITY_NORMAL]
        ]
    }

    List<String> getIssueTypes() {
        return map(redmineClient.trackers, extractName)
    }

    List<IssueStatus> getIssueStatuses() {
        return map(redmineClient.statuses) { RedmineIssueStatus status ->
            new IssueStatus(name: status.name, isClosed: status.isClosed())
        }
    }

    List<String> getIssuePriorities() {
        return map(redmineClient.issuePriorities, extractName)
    }

    Closure<String> extractName = { it.name }

    Closure<RedmineTaigaRef> addIssueRelationShips = { Map relationships ->
        return { RedmineTaigaRef ref ->
            ref.taigaProject.with {
                issueTypes = relationships.issueTypes
                issueStatuses = relationships.issueStatuses
                issuePriorities = relationships.issuePriorities
                issueSeverities = relationships.issueSeverities
            }
            ref
        }
    }

    Closure<RedmineProject> addBasicFields = { RedmineProject rp ->
        return redmineClient.getProjectByKey(rp.id.toString()).with { RedmineProject source ->
            source.description = source.description ?: source.name
            source
        }
    }

    Closure<RedmineTaigaRef> addIdentifierJustInCase = { final List<String> allNames ->
        return { RedmineProject source ->

            def addIdentifier = allNames.count { it.trim() == source.name.trim() } > 1 ? true : false
            def finalName = addIdentifier ? "${source.name} [${source.identifier}]" : source.name

            if (addIdentifier) {
                log.warn "Project '${source.name}' is repeated. Modifying name..."
            }

            return [
                taigaProject  : [ name: finalName, description: source.description] as TaigaProject,
                redmineProject: source
            ] as RedmineTaigaRef

        }
    }

    Closure<RedmineTaigaRef> saveProject = { RedmineTaigaRef ref ->
        return [
            taigaProject: taigaClient.createProject(ref.taigaProject),
            redmineProject: ref.redmineProject
        ] as RedmineTaigaRef

    }

    RedmineTaigaRef migrateFirstProjectBasicStructure() {
        return migrateProjectList(Arrays.asList(redmineClient.projects.first()))?.findResult{it}
    }

    List<TaigaIssue> migrateIssuesByProject(final RedmineTaigaRef ref) {
        return map(
            getIssuesByProject(ref),
            fullfillUserMail >>
            addAttachments >>
            addedTaigaIssue(ref)
        )
    }

    Closure<Map> addAttachments = { Map basicFields ->
        RedmineIssue issue =
            redmineClient.getIssueById(basicFields.ref)

        return [
            basicFields: basicFields,
            attachments: issue
                    .attachments
                    .collect { RedmineAttachment att ->
                        new TaigaAttachment(
                            data: new URL(att.contentURL).bytes.encodeBase64(),
                            name: att.fileName,
                            description: att.description,
                            owner: getUserInfoById(att.author.id).mail
                        )
                    },
            history: issue
                    .journals
                    .collect { Journal journal ->
                        new History(
                            user: "trasmallo@lala.com",//getUserInfoById(journal.user.id).mail,
                            comment: journal.notes
                        )
                    }
        ]
    }

    List<RedmineIssue> getIssuesByProject(RedmineTaigaRef ref) {
        return redmineClient.getIssues(project_id: ref.redmineProject.id.toString())
    }

    Closure<Map> fullfillUserMail = { RedmineIssue source ->
        return [
            tracker: source.tracker.name,
            status: source.statusName,
            ref: source.id,
            priority: source.priorityText,
            subject: source.subject,
            description: source.description,
            userMail   : getUserInfoById(source.author.id).mail
        ]
    }

    Closure<TaigaIssue> addedTaigaIssue(final RedmineTaigaRef ref) {
        return { Map partial ->
            log.debug("Creating issue of type: ${partial.tracker}")
            taigaClient.createIssue(
                new TaigaIssue(
                    project: ref.taigaProject,
                    ref: partial.basicFields.ref,
                    type: partial.basicFields.tracker,
                    status: partial.basicFields.status,
                    priority: partial.basicFields.priority,
                    severity: SEVERITY_NORMAL,
                    subject: partial.basicFields.subject,
                    description: partial.basicFields.description,
                    owner: partial.basicFields.userMail,
                    attachments: partial.attachments
                )
            )
        }
    }

    List<Wikipage> migrateWikiPagesByProject(final RedmineTaigaRef ref)  {

        List<RedmineWikiPageSummary> wikiPageSummaryList =
            redmineClient.getWikiPagesByProject(ref.redmineProject)

        Wikipage home = saveHomeIfNeccessary(wikiPageSummaryList, ref)

        return map(wikiPageSummaryList, summaryToReal(ref) >> saveWikiPage(ref)) + home
    }

    Wikipage saveHomeIfNeccessary(final List<RedmineWikiPageSummary> pages, final RedmineTaigaRef ref) {
       log.debug('Resolving which page will become the home page')

        RedmineWikiPageSummary home =
            filterHomePage << // If home present then it will be save normally afterwards
            applySearchIfNoResult(searchByOldest(pages)) <<
            applySearchIfNoResult(searchByTitleWiki(pages)) <<
            applySearchIfNoResult(searchByTitleHome(pages)) << NOTHING


        if (home) {
            return saveWikiPage(ref) << changeToHome << summaryToReal(ref) << home
        }

        return null
    }

    Closure<RedmineWikiPage> changeToHome = { RedmineWikiPage page ->
        log.debug("Preparing ${page.title} to be HOME")
        return new RedmineWikiPage(title: 'home', text: page.text)
    }

    Closure<?> filterHomePage = { RedmineWikiPageSummary summary ->
        if (summary && summary.title.toLowerCase()!= 'home') {
            log.debug("Page eligible to be home is ${summary.title}")
            return summary
        } else {
            log.debug("NO home available ${summary?.title}")
            return NOTHING
        }
    }

    Closure<?> searchByTitleHome = { List pages ->
        return { pages.find(filteringByTitleToLowerCase('home')) }
    }

    Closure<?> searchByTitleWiki = { List pages ->
        return { pages.find(filteringByTitleToLowerCase('wiki')) }
    }

    Closure<?> searchByOldest = { List pages ->
        return { pages.sort(inAscendingOrderBy('createdOn')).first() }
    }

    Closure<RedmineWikiPage> summaryToReal(final RedmineTaigaRef ref) {
        return { RedmineWikiPageSummary summary ->
            redmineClient
                .getWikiPageDetailByProjectAndTitle(
                    ref.redmineProject,
                    summary.title
                )
        }
    }

    Closure<Boolean> filteringByTitleToLowerCase(String title) {
        return { it.title.toLowerCase() == title }
    }

    Closure<Boolean> inAscendingOrderBy(String field) {
        return { it."$field" }
    }

    Closure<?> applySearchIfNoResult(Closure<?> search) {
        return { Object result ->
            result ? result : search()
        }
    }

    Closure<Wikipage> saveWikiPage(final RedmineTaigaRef ref) {
        return { RedmineWikiPage wp ->
            log.debug("Trying to save wiki page: ${wp.title}")
            Wikipage taigaWikiPage =
                taigaClient.createWiki(slugify(wp.title), wp.text, ref.taigaProject)
            log.debug("Wikipage saved successfully ? ${taigaWikiPage ? 'TRUE' : 'FALSE' }")

            return taigaWikiPage
        }
    }

    private String slugify(String possible) {
        return new Slugify().slugify(possible)
    }

    private RedmineUser getUserInfoById(Integer id) {
        return redmineClient.getUserById(id)
    }

    Closure<RedmineUser> extractUserFromIssue = { RedmineIssue issue ->
        return redmineClient.getUserById(issue.author.id)
    }

    static <T, U> List<U> map(List<T> collection, Closure<U> collector) {
        return collection.collect(collector)
    }

}
