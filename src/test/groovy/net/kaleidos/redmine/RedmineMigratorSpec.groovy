package net.kaleidos.redmine

import spock.lang.Shared
import spock.lang.IgnoreIf

import com.taskadapter.redmineapi.RedmineManager

//@IgnoreIf(NoRedmineFound)
class RedmineMigratorSpec extends MigratorToTaigaSpecBase {

    void 'Migrate all active projects'() {
        given: 'a redmine migrator'
            RedmineMigrator migrator =
                new RedmineMigrator(createRedmineClient(), createTaigaClient())
        when: 'invoking all names'
            def names = migrator.listAllProjectNames()
        then: 'names should be at least one'
            names.size() > 0
    }

    RedmineManager createRedmineClient() {
        def config = new ConfigSlurper().parse(new File('src/test/resources/redmine.groovy').toURL())
        def manager = new RedmineManager(config.host, config.apiKey)

        return manager
    }

}
