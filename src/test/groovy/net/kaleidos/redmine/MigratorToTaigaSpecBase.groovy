package net.kaleidos.redmine

import spock.lang.Shared
import spock.lang.IgnoreIf
import spock.lang.Specification

import net.kaleidos.taiga.TaigaClient
import net.kaleidos.domain.project.Project
import com.taskadapter.redmineapi.RedmineManager

class MigratorToTaigaSpecBase extends Specification {

    TaigaClient createTaigaClient() {
        def config = new ConfigSlurper().parse(new File('src/test/resources/taiga.groovy').toURL())
        def client = new TaigaClient(config.host)

        // TODO TODO TODO TODO TODO TODO BOOORRARRRRRR CUANDO ESTE TAIGA INSTALADO
        client.metaClass.saveProject = { Project p -> p }

        // TODO Recuperar cuando taiga este instalado
        //return client.authenticate(config.user, config.passwd)
        return client
    }

}
