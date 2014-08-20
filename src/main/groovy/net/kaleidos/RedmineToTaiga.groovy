package net.kaleidos

import net.kaleidos.domain.project.Project
import net.kaleidos.taiga.TaigaClient

class RedmineToTaiga {

    private static final String SERVER_URL = "http://localhost:8000"

    static void main(String[] args) {
        TaigaClient taiga = new TaigaClient(SERVER_URL)

        def p1 = new Project(name: "${new Date().time}_", description: "la descripción bla, bla....")

        def taigaProject = taiga
            .authenticate("admin", "123123")
            .saveProject(p1)

        println taigaProject

        taiga
            .deleteAllIssueTypes(taigaProject)
            .deleteAllIssueStatuses(taigaProject)
            .deleteAllIssuePriorities(taigaProject)

        println "Creando issue types..."
        ['defecto', 'historia de usuario', 'tarea'].each {
            def issueType = taiga.addIssueType(it, taigaProject)
            println issueType
        }

        println "Creando issue status..."
        ['nuevo', 'haciendo', 'casi terminado', 'fin'].each {
            def issueStatus = taiga.addIssueStatus(it, taigaProject)
            println issueStatus
        }

        println "Creando issue prioridad..."
        ['no hace falta', 'hazlo', 'lo quiero para ayer!'].each {
            def issuePriority = taiga.addIssuePriority(it, taigaProject)
            println issuePriority
        }

        println "Migration finished!"
    }

    void getProjects(TaigaClient taiga) {
        def projects = taiga
            .authenticate("admin", "123123")
            .getProjects()

        projects.each { p ->
            println p.name
            println p.description
        }
    }
}