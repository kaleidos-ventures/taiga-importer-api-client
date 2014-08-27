package net.kaleidos.redmine

import groovy.util.logging.Log4j

import net.kaleidos.taiga.TaigaClient
import com.taskadapter.redmineapi.RedmineManager

import com.taskadapter.redmineapi.bean.Tracker
import com.taskadapter.redmineapi.bean.Issue as RedmineIssue
import com.taskadapter.redmineapi.bean.Project as RedmineProject

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

    Closure<Map> addBasicFields = { RedmineProject rp ->

        RedmineProject reloaded = redmineClient.getProjectByKey(rp.id.toString())

        return [
            id: reloaded.id,
            name: reloaded.name,
            trackers: reloaded.trackers,
            description: reloaded.description ?: reloaded.name,
            identifier: reloaded.identifier
        ].asImmutable()
    }

    Closure<IssueType> trackerToIssueType = { Tracker tracker ->
        return new IssueType(name: tracker.name)
    }

    Closure<RedmineTaigaRef> addIdentifierJustInCase = { final List<String> allNames ->
        return { Map protoTaigaProject ->
            def addIdentifier = allNames.count { it.trim() == protoTaigaProject.name.trim()} > 1 ? true : false
            def name =
                protoTaigaProject.with {
                    addIdentifier ?  "$name [$identifier]" : name
                }

            if (addIdentifier) {
                log.warn "Project '${protoTaigaProject.name}' is repeated. Trying with '${name}'"
            }

            return [
                taigaProject: [
                    name: name,
                    issueTypes: protoTaigaProject.trackers.collect(trackerToIssueType),
                    description: protoTaigaProject.description] as TaigaProject,
                redmineProject: protoTaigaProject as RedmineProject
            ] as RedmineTaigaRef

        }
    }

    Closure<RedmineTaigaRef> saveProject = { RedmineTaigaRef ref ->
        return [
            taigaProject: taigaClient.createProject(ref.taigaProject.name, ref.taigaProject.description),
            redmineProject: ref.redmineProject
        ] as RedmineTaigaRef

    }

    List<RedmineTaigaRef> migrateAllProjectBasicStructure() {
        List<RedmineProject> projects = redmineClient.projects

        return projects.collect(addBasicFields >> addIdentifierJustInCase(projects.name) >> saveProject)
    }

    RedmineTaigaRef migrateFirstProjectBasicStructure() {
        List<RedmineProject> projects = redmineClient.projects

        saveProject << addIdentifierJustInCase(projects.name) << addBasicFields << projects.first()
    }

    Closure<?> tap = { String type, String field = "name" ->
        return {
            log.debug("$type ==> ${field}:" + it."$field")
            it
        }
    }

    List<IssueType> migrateIssueTrackersByProject(final RedmineTaigaRef ref) {
        return ref.redmineProject.trackers.collect(tap("IssueType") >> addedIssueType(ref))
    }

    Closure<IssueType> addedIssueType(final RedmineTaigaRef ref) {
        return {
            taigaClient.addIssueType(it.name, ref.taigaProject)
        }
    }

    List<IssueStatus> migrateIssueStatusesByProject(final RedmineTaigaRef ref) {
        return redmineClient.statuses.collect(tap("IssueStatus") >> addedIssueStatus(ref))
    }

    Closure<IssueStatus> addedIssueStatus(final RedmineTaigaRef ref) {
        return {
            taigaClient.addIssueStatus(it.name, it.isClosed(), ref.taigaProject)
        }
    }

    List<IssuePriority> migrateIssuePriorities(final RedmineTaigaRef ref) {
        return redmineClient.issuePriorities.collect(tap("IssuePriority") >> addedIssuePriority(ref))
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

        return redmineClient
            .getIssues(project_id: ref.redmineProject.id.toString())
            .collect(tap("Issue", "subject") >> addedTaigaIssue(ref))
    }

    Closure<TaigaIssue> addedTaigaIssue(final RedmineTaigaRef ref) {
        return {
            def issue =
                taigaClient.createIssue(
                    ref.taigaProject,
                    it.tracker.name,
                    it.statusName,
                    it.priorityText,
                    it.subject,
                    it.description
                )

            issue.project = ref.taigaProject
            issue
        }
    }

}
