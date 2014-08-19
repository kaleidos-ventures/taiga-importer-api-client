package net.kaleidos.redmine

import spock.lang.Shared
import spock.lang.IgnoreIf
import spock.lang.Specification

import com.taskadapter.redmineapi.RedmineManager

//@IgnoreIf(NoRedmineFound)
class RedmineMigratorSpec extends Specification {

    @Shared RedmineManager redmineClient

    void setup() {
        redmineClient = createRedmineClient()
    }

    void 'Get all project names'() {
        when: 'invoking all names'
            def names =
                new RedmineMigrator(redmineClient, null).
                    listAllProjectNames()
        then: 'names should be at least one'
            names.size() > 0
    }

    RedmineManager createRedmineClient() {
        def config = new ConfigSlurper().parse(new File('src/test/resources/redmine.groovy').toURL())
        def manager = new RedmineManager(config.host, config.apiKey)

        return manager
    }

}
