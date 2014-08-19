package net.kaleidos.redmine

import spock.lang.Shared
import spock.lang.IgnoreIf
import spock.lang.Specification

import net.kaleidos.taiga.TaigaClient
import com.taskadapter.redmineapi.RedmineManager

class MigratorToTaigaSpecBase extends Specification {

    TaigaClient createTaigaClient() {
        def config = new ConfigSlurper().parse(new File('src/test/resources/taiga.groovy').toURL())
        def client = new TaigaClient(config.host)

        return client
    }

}
