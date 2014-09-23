package net.kaleidos.redmine.migrator

import com.taskadapter.redmineapi.bean.Version as RedmineVersion
import groovy.transform.InheritConstructors
import net.kaleidos.domain.Milestone as TaigaMilestone
import net.kaleidos.domain.Project as TaigaProject
import net.kaleidos.redmine.RedmineTaigaRef

@InheritConstructors
class VersionMigrator extends AbstractMigrator<TaigaMilestone> {


    List<TaigaMilestone> migrateVersionsFromProject(final RedmineTaigaRef ref) {
        return redmineClient
            .findAllVersionByProjectId(ref.redmineId)
            .collect(this.&buildTaigaMilestone.rcurry(ref.project))
            .collect(this.&save)
    }

    TaigaMilestone buildTaigaMilestone(final RedmineVersion version, final TaigaProject taigaProject) {
        new TaigaMilestone(
            project: taigaProject,
            name: version.name,
            isClosed: version.status == 'closed' ? true : false,
            startDate: version.createdOn,
            endDate: version.dueDate
        )
    }

    @Override
    TaigaMilestone save(final TaigaMilestone taigaMilestone) {
        return taigaClient.createMilestone(taigaMilestone)
    }
}