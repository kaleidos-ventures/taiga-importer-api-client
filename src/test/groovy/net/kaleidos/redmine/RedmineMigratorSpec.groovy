package net.kaleidos.redmine

import spock.lang.Shared
import spock.lang.IgnoreIf

import net.kaleidos.domain.project.Project
import net.kaleidos.taiga.TaigaClient
import com.taskadapter.redmineapi.RedmineManager

class RedmineMigratorSpec extends MigratorToTaigaSpecBase {

    static final Double HALF_PERCENTAGE = 0.5

    void 'Migrate all active projects'() {
        setup: 'redmine and taiga clients'
            RedmineManager redmineClient = createRedmineClient()
            TaigaClient taigaClient = createTaigaClient()
            RedmineMigrator migrator = new RedmineMigrator(redmineClient, taigaClient)
        when: 'invoking all names'
            List<Project> projectList = migrator.migrateAllProjectBasicStructure()
        then: 'there should be at least one project'
            projectList.size() > 0
        and: 'all of them have name'
            projectList.every(hasName)
        and: 'usually most projects have description'
            projectList
                .count(hasDescription)
                .div(projectList.size()) > HALF_PERCENTAGE
    }

    Closure<Boolean> hasName = { Project p -> p.name }
    Closure<Boolean> hasDescription = { Project p -> p.description }

    RedmineManager createRedmineClient() {
        def config = new ConfigSlurper().parse(new File('src/test/resources/redmine.groovy').toURL())
        def manager = new RedmineManager(config.host, config.apiKey)

        return manager
    }

}
