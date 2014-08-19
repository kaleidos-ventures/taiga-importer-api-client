package net.kaleidos

import net.kaleidos.taiga.TaigaClient

class RedmineToTaiga {
    static void main(String[] args) {

        String server = "http://localhost:8000"

        TaigaClient taiga = new TaigaClient(server)

        def projects = taiga
            .authenticate("admin", "123123")
            .getProjects()

        projects.each { p ->
            println p.name
            println p.description
        }
    }
}