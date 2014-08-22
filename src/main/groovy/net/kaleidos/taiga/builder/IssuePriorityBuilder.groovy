package net.kaleidos.taiga.builder

import net.kaleidos.domain.IssuePriority

class IssuePriorityBuilder implements TaigaEntityBuilder<IssuePriority> {

    @Override
    IssuePriority build(Map json) {
        def issuePriority = new IssuePriority()

        issuePriority.with {
            id = json.id
            name = json.name
        }

        issuePriority
    }
}
