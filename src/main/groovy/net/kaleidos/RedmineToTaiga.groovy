package net.kaleidos

import groovy.util.logging.Log4j
import net.kaleidos.taiga.TaigaClient

@Log4j
class RedmineToTaiga {

    private static final String SERVER_URL = "http://localhost:8000"

    static void main(String[] args) {
        TaigaClient taiga = new TaigaClient(SERVER_URL)

        def taigaProject = taiga
            .authenticate("admin", "123123")
            .createProject("100_${new Date().time}", "la descripción bla, bla....")

        log.debug "."*100
        log.debug taigaProject
        log.debug "."*20
        log.debug taigaProject.id
        log.debug "."*100

        taiga
            .deleteAllIssueTypes(taigaProject)
            .deleteAllIssueStatuses(taigaProject)
            .deleteAllIssuePriorities(taigaProject)

        log.debug "Creando issue types..."
        ['defecto', 'historia de usuario', 'tarea'].each {
            def issueType = taiga.addIssueType(it, taigaProject)
            println issueType
        }

        log.debug "Creando issue status..."
        ['nuevo', 'haciendo', 'casi terminado', 'fin'].each {
            def issueStatus = taiga.addIssueStatus(it, true, taigaProject)
            println issueStatus
        }

        log.debug "Creando issue prioridad..."
        ['no hace falta', 'hazlo', 'lo quiero para ayer!'].each {
            def issuePriority = taiga.addIssuePriority(it, taigaProject)
            println issuePriority
        }

        taiga.createIssue(taigaProject, 'tarea', 'casi terminado', 'lo quiero para ayer!', 'Dominar el mundo', 'Plan secreto para dominar el mundo...')

        log.debug "Migration finished!"
    }

    void getProjects(TaigaClient taiga) {
        def projects = taiga
            .authenticate("admin", "123123")
            .getProjects()

        projects.each { p ->
            log.debug p.name
            log.debug p.description
        }
    }
}
