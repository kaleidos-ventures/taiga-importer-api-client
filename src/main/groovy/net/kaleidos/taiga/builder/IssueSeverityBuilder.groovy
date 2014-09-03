package net.kaleidos.taiga.builder
import net.kaleidos.domain.IssueSeverity

class IssueSeverityBuilder {

    IssueSeverity build(Map json) {
        def issueSeverity = new IssueSeverity()

        issueSeverity.with {
            id = json.id
            name = json.name
        }

        issueSeverity
    }
}