package net.kaleidos.redmine

import spock.lang.Shared
import spock.lang.IgnoreIf
import spock.lang.Specification

import net.kaleidos.taiga.TaigaClient
import com.taskadapter.redmineapi.RedmineManager

class MigratorToTaigaSpecBase extends Specification {

    TaigaClient createTaigaClient() {
        return new TaigaClient()
    }

}
