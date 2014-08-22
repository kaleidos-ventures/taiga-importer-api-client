package net.kaleidos.taiga.builder
import net.kaleidos.domain.IssueType

class IssueTypeBuilder implements TaigaEntityBuilder<IssueType> {

    @Override
    IssueType build(Map json) {
        def issueType = new IssueType()

        issueType.with {
            id = json.id
            name = json.name
        }

        issueType
    }
}
