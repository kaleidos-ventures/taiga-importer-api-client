package net.kaleidos.redmine

import static groovyx.gpars.GParsPool.withPool
import com.taskadapter.redmineapi.RedmineManager
import com.taskadapter.redmineapi.bean.Issue as RedmineIssue
import com.taskadapter.redmineapi.bean.Project as RedmineProject
import com.taskadapter.redmineapi.bean.Tracker
import com.taskadapter.redmineapi.bean.User as RedmineUser
import com.taskadapter.redmineapi.bean.Membership as RedmineMembership
import com.taskadapter.redmineapi.bean.WikiPage as RedmineWikiPageSummary
import com.taskadapter.redmineapi.bean.WikiPageDetail as RedmineWikiPage

import net.kaleidos.domain.User as TaigaUser
import net.kaleidos.domain.Issue as TaigaIssue
import net.kaleidos.domain.Wikipage
import net.kaleidos.domain.IssueStatus
import net.kaleidos.domain.Project as TaigaProject
import net.kaleidos.taiga.TaigaClient

import groovy.util.logging.Log4j

@Log4j
class RedmineMigrator {

    final RedmineManager redmineClient
    final TaigaClient taigaClient

    RedmineMigrator(final RedmineManager redmineClient, final TaigaClient taigaClient) {
        this.redmineClient = redmineClient
        this.taigaClient = taigaClient
    }

    List<RedmineTaigaRef> migrateAllProjectBasicStructure() {
        List<RedmineProject> projects = redmineClient.projects

        return map(projects, addBasicFields >> addIdentifierJustInCase(projects.name) >> saveProject)
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

            if (addIdentifier) {
                log.warn "Project '${source.name}' is repeated. Modifying name..."
            }

            return [
                taigaProject  : [
                    name       : addIdentifier ? "${source.name} [${source.identifier}]" : source.name,
                    description: source.description] as TaigaProject,
                redmineProject: source
            ] as RedmineTaigaRef

        }
    }

    Closure<String> trackerToIssueType = { Tracker tracker ->
        return tracker.name
    }

    Closure<RedmineTaigaRef> saveProject = { RedmineTaigaRef ref ->
        return [
            taigaProject  : taigaClient.createProject(ref.taigaProject.name, ref.taigaProject.description),
            redmineProject: ref.redmineProject
        ] as RedmineTaigaRef

    }

    RedmineTaigaRef migrateFirstProjectBasicStructure() {
        List<RedmineProject> projects = redmineClient.projects

        saveProject << addIdentifierJustInCase(projects.name) << addBasicFields << projects.find {
            it.name.toLowerCase().contains('decathlon')
        }
    }

    List<String> migrateIssueTrackersByProject(final RedmineTaigaRef ref) {
        return map(ref.redmineProject.trackers, addedIssueType(ref))
    }

    Closure<String> addedIssueType(final RedmineTaigaRef ref) {
        return {
            taigaClient.addIssueType(it.name, ref.taigaProject)
        }
    }

    List<IssueStatus> migrateIssueStatusesByProject(final RedmineTaigaRef ref) {
        return map(redmineClient.statuses, addedIssueStatus(ref))
    }

    Closure<IssueStatus> addedIssueStatus(final RedmineTaigaRef ref) {
        return {
            taigaClient.addIssueStatus(it.name, it.isClosed(), ref.taigaProject)
        }
    }

    List<String> migrateIssuePriorities(final RedmineTaigaRef ref) {
        return map(redmineClient.issuePriorities, addedIssuePriority(ref))
    }

    Closure<String> addedIssuePriority(final RedmineTaigaRef ref) {
        return {
            taigaClient.addIssuePriority(it.name, ref.taigaProject)
        }
    }

    List<TaigaIssue> migrateIssuesByProject(final RedmineTaigaRef ref) {

        ref.taigaProject.with {
            issueStatuses = migrateIssueStatusesByProject(ref)
            issueTypes = migrateIssueTrackersByProject(ref)
            issuePriorities = migrateIssuePriorities(ref)
        }

        // Per each redmine issue first we need to add the user mail
        // and then create a new taiga issue
        return map(getIssuesByProject(ref), fullfillUserMail >> addedTaigaIssue(ref))
    }

    List<RedmineIssue> getIssuesByProject(RedmineTaigaRef ref) {
        return redmineClient.getIssues(project_id: ref.redmineProject.id.toString())
    }

    Closure<TaigaIssue> fullfillUserMail = { RedmineIssue source ->
        return [
            tracker    : source.tracker.name,
            status     : source.statusName,
            priority   : source.priorityText,
            subject    : source.subject,
            description: source.description,
            userMail   : getUserInfoById(source.author.id).mail
        ]
    }

    Closure<TaigaIssue> addedTaigaIssue(final RedmineTaigaRef ref) {
        return { Map partial ->
            log.debug("TRACKER/TYPE: ${partial.tracker}")
            taigaClient.createIssue(
                ref.taigaProject,
                partial.tracker,
                partial.status,
                partial.priority,
                partial.subject,
                partial.description,
                partial.userMail
            )
        }
    }

    List<Wikipage> migrateWikiPagesByProject(final RedmineTaigaRef ref)  {
        List<RedmineWikiPageSummary> wikiPageSummaryList =
            redmineClient.getWikiPagesByProject(ref.redmineProject)

        return map(wikiPageSummaryList, summaryToReal(ref) >> saveWikiPage(ref))
    }

    Closure<RedmineWikiPage> summaryToReal(final RedmineTaigaRef ref) {
        return { RedmineWikiPageSummary summary ->
            redmineClient.getWikiPageDetailByProjectAndTitle(ref.redmineProject, summary.title)
        }
    }

    Closure<Wikipage> saveWikiPage(final RedmineTaigaRef ref) {
        return { RedmineWikiPage wp ->
            Wikipage taigaWikiPage =
                taigaClient.createWiki(wp.title, wp.text, ref.taigaProject)

            return taigaWikiPage
        }
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

    static <T, U> List<U> mapParallel(List<T> collection, Closure<U> collector) {
        return withPool { collection.collectParallel(collector) }
    }
}
