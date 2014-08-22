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

    List<TaigaProject> migrateAllProjectBasicStructure() {

        Closure<Map> basicFields = { RedmineProject rp ->
            return [
                name: rp.name,
                description: rp.description ?: rp.name,
                identifier: rp.identifier
            ].asImmutable()
        }

        Closure<Map> addIdentifierJustInCase = { final List<String> allNames ->
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
                    name: name,
                    description: protoTaigaProject.description
                ] as TaigaProject
            }
        }

        Closure<TaigaProject> savedProjects = { TaigaProject tp ->
            return taigaClient.saveProject(tp)
        }

        List<RedmineProject> projects = redmineClient.projects

        return projects.collect(
            basicFields >>
            addIdentifierJustInCase(projects.name) >>
            savedProjects
        )
    }


}
