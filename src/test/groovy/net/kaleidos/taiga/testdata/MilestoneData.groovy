package net.kaleidos.taiga.testdata

import net.kaleidos.domain.Milestone
import net.kaleidos.domain.Project

trait MilestoneData {

    Milestone buildBasicMilestone(Project project) {
        new Milestone()
            .setName('Sprint 01')
            .setStartDate(Date.parse("dd/MM/yyyy", '01/08/2014'))
            .setEndDate(Date.parse("dd/MM/yyyy", '11/08/2014'))
            .setProject(project)
    }
}