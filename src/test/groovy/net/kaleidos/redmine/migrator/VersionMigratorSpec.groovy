package net.kaleidos.redmine.migrator

import net.kaleidos.redmine.MigratorToTaigaSpecBase
import net.kaleidos.redmine.RedmineClient
import net.kaleidos.redmine.RedmineTaigaRef
import net.kaleidos.redmine.testdata.ProjectDataProvider
import net.kaleidos.redmine.testdata.VersionDataProvider

class VersionMigratorSpec extends MigratorToTaigaSpecBase {

    @Delegate
    ProjectDataProvider projectDataProvider = new ProjectDataProvider()
    @Delegate
    VersionDataProvider versionDataProvider = new VersionDataProvider()

    void setup() {
        deleteTaigaProjects()
    }

    void 'migrate versions of one project'() {
        given: 'a mocked redmine client'
            RedmineClient mockedClient = Stub(RedmineClient) {
                findAllVersionByProjectId(_) >> buildRedmineVersionList()
            }

        and: 'a taiga client'
            def taigaClient = createTaigaClient()

        and: 'a redmine project to migrate its versions'
            ProjectMigrator projectMigrator = new ProjectMigrator(mockedClient, taigaClient)
            RedmineTaigaRef migratedProjectInfo = projectMigrator.migrateProject(buildRedmineProject())

        and: 'building a version migrator instance'
            VersionMigrator versionMigrator = new VersionMigrator(mockedClient, taigaClient)

        when: 'migration the versions of the project'
            def taigaMilestones = versionMigrator.migrateVersionsFromProject(migratedProjectInfo)

        then: 'the milestones are created in taiga'
            taigaMilestones.size() > 0
            taigaMilestones.every(basicData)
    }

    Closure<Boolean> basicData = {
        it.project &&
        it.name &&
        it.isClosed != null &&
        it.startDate &&
        it.endDate
    }
}