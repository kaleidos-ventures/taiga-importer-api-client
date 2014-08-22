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
import net.kaleidos.domain.Project as TaigaProject

@Log4j
class RedmineMigrator {

    final RedmineManager redmineClient
    final TaigaClient taigaClient

    RedmineMigrator(final RedmineManager redmineClient, final TaigaClient taigaClient) {
        this.redmineClient = redmineClient
        this.taigaClient = taigaClient
    }

    Closure<Map> addBasicFields = { RedmineProject rp ->
        return [
            id: rp.id,
            name: rp.name,
            description: rp.description ?: rp.name,
            identifier: rp.identifier
        ].asImmutable()
    }

    Closure<RedmineTaigaRef> addIdentifierJustInCase = { final List<String> allNames ->
        return { Map protoTaigaProject ->
            def addIdentifier = allNames.count { it == protoTaigaProject.name} > 1 ? true : false
            def name =
                protoTaigaProject.with {
                    addIdentifier ?  "$name [$identifier]" : name
                }

            if (addIdentifier) {
                log.warn "Project '${protoTaigaProject.name}' is repeated. Trying with '${name}'"
            }

            return [
                taigaProject: [name: name, description: protoTaigaProject.description] as TaigaProject,
                redmineProject: protoTaigaProject as RedmineProject
            ] as RedmineTaigaRef

        }
    }

    Closure<RedmineTaigaRef> saveProject = { RedmineTaigaRef ref ->
        TaigaProject taigaProject =
            taigaClient.saveProject(
                ref.taigaProject.name, ref.taigaProject.description
            )

        return [
            taigaProject: taigaClient.createProject(ref.taigaProject.name, ref.taigaProject.description),
            redmineProject: ref.redmineProject
        ] as RedmineTaigaRef

    }

    List<RedmineTaigaRef> migrateAllProjectBasicStructure() {
        List<RedmineProject> projects = redmineClient.projects

        return projects.collect(
            addBasicFields >>
            addIdentifierJustInCase(projects.name) >>
            saveProject
        )
    }

    RedmineTaigaRef migrateFirstProjectBasicStructure() {
        List<RedmineProject> projects = redmineClient.projects

        saveProject << addIdentifierJustInCase(projects.name) << addBasicFields << projects.first()
    }

    List<IssueType> migrateIssueTrackersByProject(RedmineTaigaRef ref) {
        Closure<RedmineTaigaRef> tap = { log.debug("IssueType ==> ${it.name}"); it }
        Closure<IssueType> add = { taigaClient.addIssueType(it.name, ref.taigaProject) }

        // TODO failing
        //http://redmine.local/projects/yump.json?include=trackers
        return ref.redmineProject.trackers.collect(tap >> add)
    }

    List<IssueStatus> migrateIssueStatusesByProject(RedmineTaigaRef ref) {
        Closure<RedmineTaigaRef> tap = { log.debug("IssueStatus ==> ${it.name}"); it }
        Closure<IssueStatus> add = {
            taigaClient.addIssueStatus(
                it.name,
                it.isClosed(),
                ref.taigaProject
            )
        }

        return redmineClient.statuses.collect(tap >> add)
    }

    //List<IssuePriority> migrateIssuePriorities()

    List<TaigaIssue> migrateIssuesByProject(RedmineTaigaRef projectRef) {
        println redmineClient.getIssues([project_id: projectRef.redmineProject.id.toString()])

        []
    }

}
