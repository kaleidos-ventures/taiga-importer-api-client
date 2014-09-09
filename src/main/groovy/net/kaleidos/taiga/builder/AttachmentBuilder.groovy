package net.kaleidos.taiga.builder

import net.kaleidos.domain.Attachment
import net.kaleidos.domain.Project
import net.kaleidos.taiga.common.DateConversions

class AttachmentBuilder implements TaigaEntityBuilder<Attachment>, DateConversions {

    Attachment build(Map json, Project project) {
        def attachment = new Attachment()

        attachment.with {
            owner = json.owner
            name = json.attached_file.name
            data = json.attached_file.data
            createdDate = parse(json.created_date)
            description = json.description
        }

        attachment
    }
}