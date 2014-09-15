package net.kaleidos.taiga

import net.kaleidos.domain.Milestone
import net.kaleidos.domain.Project
import spock.lang.Unroll

class MilestoneTaigaSpec extends TaigaSpecBase {

    Project project

    def setup() {
        project = createProject()
    }

    def cleanup() {
        deleteProject(project)
    }

    @Unroll
    void 'create a new milestone #milestoneDesc'() {
        given: 'a new milestone'
            def milestone = new Milestone()
                .setName(name)
                .setIsClosed(isClosed)
                .setStartDate(startDate)
                .setEndDate(endDate)
                .setProject(project)

        when: 'creating the milestone'
            milestone = taigaClient.createMilestone(milestone)

        then: 'the milestone is created in Taiga'
            milestone != null
            milestone.name == name
            milestone.isClosed == isClosed
            milestone.startDate == startDate
            milestone.endDate == endDate

        where:
            name = 'Sprint 01'
            isClosed << [true, false]
            startDate = Date.parse("dd/MM/yyyy", '01/08/2014')
            endDate = Date.parse("dd/MM/yyyy", '10/08/2014')

            milestoneDesc = isClosed ? 'close' : 'open'
    }
}