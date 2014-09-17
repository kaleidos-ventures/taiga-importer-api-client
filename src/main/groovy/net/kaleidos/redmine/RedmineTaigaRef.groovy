package net.kaleidos.redmine

import net.kaleidos.domain.User as TaigaUser
import net.kaleidos.domain.Project as TaigaProject
import net.kaleidos.domain.Membership as TaigaMembership
import com.taskadapter.redmineapi.bean.Project as RedmineProject

class RedmineTaigaRef {

    final Integer redmineProjectId
    final TaigaProject project

    public RedmineTaigaRef(
        Integer redmineProjectId,
        TaigaProject project) {
        this.redmineProjectId = redmineProjectId
        this.project = project
    }

    String findUserEmailByRedmineId(Integer redmineUserId) {
        return findMembershipByUserMigrationRef(redmineUserId.toString()).email
    }

    TaigaMembership findMembershipByUserMigrationRef(String migrationUserId) {
        return project
            .memberships
            .find { TaigaMembership m ->
                m.userMigrationRef == migrationUserId
            }
    }

    TaigaUser findUserByMigrationId(Integer redmineUserId) {
        TaigaMembership membership =
            findMembershipByUserMigrationRef(
                redmineUserId.toString()
            )

        return new TaigaUser(
            name: membership.userName,
            email: membership.email
        )
    }

}
