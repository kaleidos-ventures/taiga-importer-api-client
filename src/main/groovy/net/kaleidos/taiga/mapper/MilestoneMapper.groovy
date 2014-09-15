package net.kaleidos.taiga.mapper

import net.kaleidos.domain.Milestone
import net.kaleidos.taiga.common.DateConversions

class MilestoneMapper implements Mapper<Milestone>, DateConversions {

    @Override
    Map map(Milestone milestone) {
        [
            name            : milestone.name,
            closed          : milestone.isClosed,
            estimated_start : format(milestone.startDate, TAIGA_DATE_FORMAT),
            estimated_finish: format(milestone.endDate, TAIGA_DATE_FORMAT),
        ]
    }
}