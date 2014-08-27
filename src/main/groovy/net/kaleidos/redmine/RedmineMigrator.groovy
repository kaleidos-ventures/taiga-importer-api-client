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

        Closure<IssueType> add = {
            taigaClient.addIssueType(
                it.name,
                ref.taigaProject
            )
        }

        return ref.redmineProject.trackers.collect(tap("IssueType") >> add)
    }

    List<IssueStatus> migrateIssueStatusesByProject(final RedmineTaigaRef ref) {
        Closure<IssueStatus> add = {
            taigaClient.addIssueStatus(
                it.name,
                it.isClosed(),
                ref.taigaProject
            )
        }

        return redmineClient.statuses.collect(tap("IssueStatus") >> add)
    }

    List<IssuePriority> migrateIssuePriorities(final RedmineTaigaRef ref) {
        Closure<IssuePriority> add = {
            taigaClient.addIssuePriority(it.name, ref.taigaProject)
        }

        return redmineClient.issuePriorities.collect(tap("IssuePriority") >> add)
    }

    List<TaigaIssue> migrateIssuesByProject(final RedmineTaigaRef projectRef) {

        projectRef.taigaProject.with {
            issueStatuses = migrateIssueStatusesByProject(projectRef)
            issueTypes = migrateIssueTrackersByProject(projectRef)
            issuePriorities = migrateIssuePriorities(projectRef)
        }

        Closure<TaigaIssue> add = {
            def issue =
                taigaClient.createIssue(
                    projectRef.taigaProject,
                    it.tracker.name,
                    it.statusName,
                    it.priorityText,
                    it.subject,
                    it.description
                )

            issue.project = projectRef.taigaProject
            issue
        }

        def params = [project_id: projectRef.redmineProject.id.toString()]

        return redmineClient.getIssues(params).collect(tap("Issue", "subject") >> add)
    }

}
