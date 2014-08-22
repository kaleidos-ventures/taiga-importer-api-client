package net.kaleidos.redmine

import groovy.util.logging.Log4j

import net.kaleidos.taiga.TaigaClient
import com.taskadapter.redmineapi.RedmineManager

import com.taskadapter.redmineapi.bean.Project as RedmineProject
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

}
