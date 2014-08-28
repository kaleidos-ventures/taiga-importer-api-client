package net.kaleidos.redmine

import static groovyx.gpars.GParsPool.withPool

import groovy.util.logging.Log4j

import net.kaleidos.taiga.TaigaClient
import com.taskadapter.redmineapi.RedmineManager

import com.taskadapter.redmineapi.bean.User as RedmineUser
import com.taskadapter.redmineapi.bean.Tracker
import com.taskadapter.redmineapi.bean.Issue as RedmineIssue
import com.taskadapter.redmineapi.bean.Project as RedmineProject

import net.kaleidos.domain.User as TaigaUser
import net.kaleidos.domain.Issue as TaigaIssue
import net.kaleidos.domain.IssueType
import net.kaleidos.domain.IssueStatus
import net.kaleidos.domain.IssuePriority
import net.kaleidos.domain.Project as TaigaProject

import org.apache.http.message.BasicNameValuePair

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

            def addIdentifier = allNames.count { it.trim() == source.name.trim()} > 1 ? true : false

            if (addIdentifier) {
                log.warn "Project '${source.name}' is repeated. Modifying name..."
            }

            return [
                taigaProject: [
                    name: addIdentifier ? "${source.name} [${source.identifier}]" : source.name,
                    description: source.description ] as TaigaProject,
                redmineProject: source
            ] as RedmineTaigaRef

        }
    }

    Closure<IssueType> trackerToIssueType = { Tracker tracker ->
        return new IssueType(name: tracker.name)
    }

    Closure<RedmineTaigaRef> saveProject = { RedmineTaigaRef ref ->
        return [
            taigaProject: taigaClient.createProject(ref.taigaProject.name, ref.taigaProject.description),
            redmineProject: ref.redmineProject
        ] as RedmineTaigaRef

    }

    RedmineTaigaRef migrateFirstProjectBasicStructure() {
        List<RedmineProject> projects = redmineClient.projects

        saveProject << addIdentifierJustInCase(projects.name) << addBasicFields << projects.first()
    }

    List<IssueType> migrateIssueTrackersByProject(final RedmineTaigaRef ref) {
        return map(ref.redmineProject.trackers, addedIssueType(ref))
    }

    Closure<IssueType> addedIssueType(final RedmineTaigaRef ref) {
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

    List<IssuePriority> migrateIssuePriorities(final RedmineTaigaRef ref) {
        return map(redmineClient.issuePriorities, addedIssuePriority(ref))
    }

    Closure<IssuePriority> addedIssuePriority(final RedmineTaigaRef ref) {
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

        return mapParallel(getIssuesByProject(ref), addedTaigaIssue(ref))
    }

    private List<RedmineIssue> getIssuesByProject(RedmineTaigaRef ref) {
        return redmineClient.getIssues(project_id: ref.redmineProject.id.toString())
    }

    Closure<TaigaIssue> addedTaigaIssue(final RedmineTaigaRef ref) {
        return {
            taigaClient.createIssue(
                ref.taigaProject,
                it.tracker.name,
                it.statusName,
                it.priorityText,
                it.subject,
                it.description
            )
        }
    }

    List<RedmineUser> getUsersByProject(final RedmineTaigaRef ref) {
        return map(getIssuesByProject(ref),  extractUserFromIssue)
    }

    Closure<RedmineUser> extractUserFromIssue = { RedmineIssue issue ->
       return redmineClient.getUserById(issue.author.id)
    }

    static <T,U> List<U> map(List<T> collection, Closure<U> collector) {
        return collection.collect(collector)
    }

    static <T,U> List<U> mapParallel(List<T> collection, Closure<U> collector) {
        return withPool { collection.collectParallel(collector) }
    }

}
