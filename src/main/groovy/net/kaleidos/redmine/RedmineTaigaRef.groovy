package net.kaleidos.redmine

import com.taskadapter.redmineapi.bean.Project as RedmineProject
import net.kaleidos.domain.Membership as TaigaMembership
import net.kaleidos.domain.Project as TaigaProject

class RedmineTaigaRef {

    RedmineProject redmineProject
    TaigaProject taigaProject
    List<TaigaMembership> memberships

}
