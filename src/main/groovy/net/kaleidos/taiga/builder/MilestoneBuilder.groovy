package net.kaleidos.taiga.builder

import net.kaleidos.domain.Milestone
import net.kaleidos.domain.Project
import net.kaleidos.taiga.common.DateConversions

class MilestoneBuilder implements TaigaEntityBuilder<Milestone>, DateConversions {

    Milestone build(Map json, Project project) {
        def milestone = new Milestone()

        milestone.with {
            name = json.name
            isClosed = json.closed
            delegate.project = project
            startDate = parse(json.estimated_start, TAIGA_DATE_FORMAT)
            endDate = parse(json.estimated_finish, TAIGA_DATE_FORMAT)
        }

        milestone
    }
}